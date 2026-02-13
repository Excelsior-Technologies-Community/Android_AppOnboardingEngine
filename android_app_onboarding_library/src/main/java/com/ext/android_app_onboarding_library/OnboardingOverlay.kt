package com.ext.android_app_onboarding_library

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class OnboardingOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    // Configuration
    private var config: OnboardingConfig = OnboardingConfig()
    private var steps: List<OnboardingStep> = listOf()
    private var currentStepIndex = 0
    private var listener: OnboardingListener? = null

    // Target coordinates
    private var targetX = 0f
    private var targetY = 0f
    private var targetWidth = 0f
    private var targetHeight = 0f

    // Animation
    private var highlightAnimator: ValueAnimator? = null
    private var currentRadius = 0f

    // Paints
    private val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val descriptionPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val buttonTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Button bounds
    private val skipButtonRect = RectF()
    private val nextButtonRect = RectF()

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.OnboardingOverlay)
            config.overlayColor = typedArray.getColor(
                R.styleable.OnboardingOverlay_overlayBackgroundColor,
                config.overlayColor
            )
            config.highlightRadius = typedArray.getDimension(
                R.styleable.OnboardingOverlay_highlightRadius,
                config.highlightRadius
            )
            config.arrowColor = typedArray.getColor(
                R.styleable.OnboardingOverlay_arrowColor,
                config.arrowColor
            )
            config.titleTextSize = typedArray.getDimension(
                R.styleable.OnboardingOverlay_titleTextSize,
                config.titleTextSize
            )
            config.descriptionTextSize = typedArray.getDimension(
                R.styleable.OnboardingOverlay_descriptionTextSize,
                config.descriptionTextSize
            )
            config.titleTextColor = typedArray.getColor(
                R.styleable.OnboardingOverlay_titleTextColor,
                config.titleTextColor
            )
            config.descriptionTextColor = typedArray.getColor(
                R.styleable.OnboardingOverlay_descriptionTextColor,
                config.descriptionTextColor
            )
            typedArray.recycle()
        }
        setupPaints()
    }

    private fun setupPaints() {
        overlayPaint.color = config.overlayColor

        titlePaint.color = config.titleTextColor
        titlePaint.textSize = config.titleTextSize
        titlePaint.typeface = Typeface.DEFAULT_BOLD

        descriptionPaint.color = config.descriptionTextColor
        descriptionPaint.textSize = config.descriptionTextSize

        arrowPaint.color = config.arrowColor
        arrowPaint.strokeWidth = 5f
        arrowPaint.style = Paint.Style.STROKE
        arrowPaint.strokeCap = Paint.Cap.ROUND

        buttonPaint.color = Color.WHITE
        buttonPaint.style = Paint.Style.FILL

        buttonTextPaint.color = config.overlayColor
        buttonTextPaint.textSize = 40f
        buttonTextPaint.typeface = Typeface.DEFAULT_BOLD
        buttonTextPaint.textAlign = Paint.Align.CENTER

        indicatorPaint.style = Paint.Style.FILL
    }

    fun setConfig(config: OnboardingConfig) {
        this.config = config
        setupPaints()
        invalidate()
    }

    fun setSteps(steps: List<OnboardingStep>) {
        this.steps = steps
        currentStepIndex = 0
        setupStep()
        invalidate()
    }

    fun setListener(listener: OnboardingListener) {
        this.listener = listener
    }

    private fun setupStep() {
        if (steps.isEmpty() || currentStepIndex >= steps.size) {
            visibility = GONE
            listener?.onOnboardingComplete()
            return
        }

        val step = steps[currentStepIndex]

        // Wait for layout to calculate positions
        post {
            val loc = IntArray(2)
            val overlayLoc = IntArray(2)

            step.targetView.getLocationOnScreen(loc)
            getLocationOnScreen(overlayLoc)

            // Calculate relative positions
            targetX = (loc[0] - overlayLoc[0] + step.targetView.width / 2f)
            targetY = (loc[1] - overlayLoc[1] + step.targetView.height / 2f)
            targetWidth = step.targetView.width.toFloat()
            targetHeight = step.targetView.height.toFloat()

            // Animate highlight
            if (config.animateHighlight) {
                animateHighlight()
            } else {
                currentRadius = getHighlightRadius(step)
                invalidate()
            }

            listener?.onStepChanged(currentStepIndex, steps.size)
        }
    }

    private fun animateHighlight() {
        highlightAnimator?.cancel()
        val step = steps[currentStepIndex]
        val targetRadius = getHighlightRadius(step)

        highlightAnimator = ValueAnimator.ofFloat(0f, targetRadius).apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                currentRadius = animation.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun getHighlightRadius(step: OnboardingStep): Float {
        return when (step.highlightShape) {
            HighlightShape.CIRCLE -> {
                val maxDimension = maxOf(targetWidth, targetHeight)
                (maxDimension / 2f) + config.highlightPadding
            }
            HighlightShape.RECTANGLE, HighlightShape.ROUNDED_RECTANGLE -> {
                // For rectangles, we use radius as padding
                config.highlightPadding
            }
        }
    }

    fun nextStep() {
        if (currentStepIndex < steps.size - 1) {
            currentStepIndex++
            setupStep()
        } else {
            finish()
        }
    }

    fun previousStep() {
        if (currentStepIndex > 0) {
            currentStepIndex--
            setupStep()
        }
    }

    fun skip() {
        listener?.onSkip()
        finish()
    }

    private fun finish() {
        visibility = GONE
        listener?.onOnboardingComplete()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y

            // Check skip button
            if (config.showSkipButton && skipButtonRect.contains(x, y)) {
                skip()
                return true
            }

            // Check next button
            if (nextButtonRect.contains(x, y)) {
                nextStep()
                return true
            }

            // Check if touch is on highlighted area
            if (config.allowTargetClick && isInsideHighlight(x, y)) {
                nextStep()
                return true
            }

            return true
        }
        return super.onTouchEvent(event)
    }

    private fun isInsideHighlight(x: Float, y: Float): Boolean {
        val step = steps.getOrNull(currentStepIndex) ?: return false

        return when (step.highlightShape) {
            HighlightShape.CIRCLE -> {
                val dx = x - targetX
                val dy = y - targetY
                dx * dx + dy * dy <= currentRadius * currentRadius
            }
            HighlightShape.RECTANGLE, HighlightShape.ROUNDED_RECTANGLE -> {
                val left = targetX - targetWidth / 2 - currentRadius
                val right = targetX + targetWidth / 2 + currentRadius
                val top = targetY - targetHeight / 2 - currentRadius
                val bottom = targetY + targetHeight / 2 + currentRadius
                x >= left && x <= right && y >= top && y <= bottom
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (steps.isEmpty() || currentStepIndex >= steps.size) return

        val step = steps[currentStepIndex]

        // Draw overlay with highlight cutout
        drawOverlay(canvas, step)

        // Draw content
        val contentY = calculateContentPosition()
        drawTitle(canvas, step, contentY)
        drawDescription(canvas, step, contentY)

        if (config.showArrow) {
            drawArrow(canvas, contentY)
        }

        // Draw step indicators
        if (config.showStepIndicators && steps.size > 1) {
            drawStepIndicators(canvas)
        }

        // Draw buttons
        drawButtons(canvas)
    }

    private fun drawOverlay(canvas: Canvas, step: OnboardingStep) {
        val path = Path().apply {
            addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
        }

        val highlightPath = Path()
        when (step.highlightShape) {
            HighlightShape.CIRCLE -> {
                highlightPath.addCircle(targetX, targetY, currentRadius, Path.Direction.CCW)
            }
            HighlightShape.RECTANGLE -> {
                highlightPath.addRect(
                    targetX - targetWidth / 2 - currentRadius,
                    targetY - targetHeight / 2 - currentRadius,
                    targetX + targetWidth / 2 + currentRadius,
                    targetY + targetHeight / 2 + currentRadius,
                    Path.Direction.CCW
                )
            }
            HighlightShape.ROUNDED_RECTANGLE -> {
                highlightPath.addRoundRect(
                    targetX - targetWidth / 2 - currentRadius,
                    targetY - targetHeight / 2 - currentRadius,
                    targetX + targetWidth / 2 + currentRadius,
                    targetY + targetHeight / 2 + currentRadius,
                    config.roundedCornerRadius,
                    config.roundedCornerRadius,
                    Path.Direction.CCW
                )
            }
        }

        path.op(highlightPath, Path.Op.DIFFERENCE)
        canvas.drawPath(path, overlayPaint)
    }

    private fun calculateContentPosition(): Float {
        // Determine if content should be above or below target
        val spaceAbove = targetY - currentRadius
        val spaceBelow = height - (targetY + currentRadius)

        return if (spaceAbove > spaceBelow) {
            // Draw above target
            targetY - currentRadius - config.contentPadding
        } else {
            // Draw below target
            targetY + currentRadius + config.contentPadding
        }
    }

    private fun drawTitle(canvas: Canvas, step: OnboardingStep, baseY: Float) {
        val title = step.title
        val x = config.contentPadding
        val maxWidth = width - 2 * config.contentPadding

        // Wrap text if needed
        val lines = wrapText(title, titlePaint, maxWidth)
        var y = baseY

        for (line in lines) {
            canvas.drawText(line, x, y, titlePaint)
            y += titlePaint.textSize + 10f
        }
    }

    private fun drawDescription(canvas: Canvas, step: OnboardingStep, baseY: Float) {
        val description = step.description
        val x = config.contentPadding
        val maxWidth = width - 2 * config.contentPadding

        // Calculate starting Y (below title)
        val titleLines = wrapText(step.title, titlePaint, maxWidth)
        var y = baseY + titleLines.size * (titlePaint.textSize + 10f) + 20f

        // Wrap text if needed
        val lines = wrapText(description, descriptionPaint, maxWidth)

        for (line in lines) {
            canvas.drawText(line, x, y, descriptionPaint)
            y += descriptionPaint.textSize + 8f
        }
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val lines = mutableListOf<String>()
        val words = text.split(" ")
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val testWidth = paint.measureText(testLine)

            if (testWidth > maxWidth && currentLine.isNotEmpty()) {
                lines.add(currentLine)
                currentLine = word
            } else {
                currentLine = testLine
            }
        }

        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }

        return lines
    }

    private fun drawArrow(canvas: Canvas, contentY: Float) {
        val step = steps[currentStepIndex]
        val titleLines = wrapText(step.title, titlePaint, width - 2 * config.contentPadding)
        val descLines = wrapText(step.description, descriptionPaint, width - 2 * config.contentPadding)

        val arrowStartX = config.contentPadding + 50f
        val arrowStartY = contentY + titleLines.size * (titlePaint.textSize + 10f) +
                descLines.size * (descriptionPaint.textSize + 8f) + 40f

        val angle = atan2(targetY - arrowStartY, targetX - arrowStartX)
        val arrowLength = 80f
        val endX = arrowStartX + arrowLength * cos(angle).toFloat()
        val endY = arrowStartY + arrowLength * sin(angle).toFloat()

        // Draw arrow line
        canvas.drawLine(arrowStartX, arrowStartY, endX, endY, arrowPaint)

        // Draw arrow head
        val arrowHeadSize = 20f
        val leftX = endX - arrowHeadSize * cos(angle - Math.PI / 6).toFloat()
        val leftY = endY - arrowHeadSize * sin(angle - Math.PI / 6).toFloat()
        val rightX = endX - arrowHeadSize * cos(angle + Math.PI / 6).toFloat()
        val rightY = endY - arrowHeadSize * sin(angle + Math.PI / 6).toFloat()

        canvas.drawLine(endX, endY, leftX, leftY, arrowPaint)
        canvas.drawLine(endX, endY, rightX, rightY, arrowPaint)
    }

    private fun drawStepIndicators(canvas: Canvas) {
        val indicatorSize = 12f
        val indicatorSpacing = 20f
        val totalWidth = steps.size * indicatorSize + (steps.size - 1) * indicatorSpacing
        val startX = (width - totalWidth) / 2f
        val y = height - 150f

        for (i in steps.indices) {
            val x = startX + i * (indicatorSize + indicatorSpacing)
            indicatorPaint.color = if (i == currentStepIndex) Color.WHITE else Color.GRAY
            canvas.drawCircle(x + indicatorSize / 2, y, indicatorSize / 2, indicatorPaint)
        }
    }

    private fun drawButtons(canvas: Canvas) {
        val buttonY = height - 80f
        val buttonHeight = 60f
        val buttonWidth = 120f
        val margin = 20f

        // Next/Done button
        val nextButtonLeft = width - buttonWidth - margin
        nextButtonRect.set(
            nextButtonLeft,
            buttonY - buttonHeight / 2,
            nextButtonLeft + buttonWidth,
            buttonY + buttonHeight / 2
        )

        canvas.drawRoundRect(nextButtonRect, 10f, 10f, buttonPaint)
        val nextText = if (currentStepIndex == steps.size - 1) "Done" else "Next"
        canvas.drawText(
            nextText,
            nextButtonRect.centerX(),
            nextButtonRect.centerY() + buttonTextPaint.textSize / 3,
            buttonTextPaint
        )

        // Skip button
        if (config.showSkipButton) {
            val skipButtonLeft = margin
            skipButtonRect.set(
                skipButtonLeft,
                buttonY - buttonHeight / 2,
                skipButtonLeft + buttonWidth,
                buttonY + buttonHeight / 2
            )

            val skipButtonPaint = Paint(buttonPaint).apply {
                color = Color.TRANSPARENT
                style = Paint.Style.STROKE
                strokeWidth = 3f
                color = Color.WHITE
            }
            canvas.drawRoundRect(skipButtonRect, 10f, 10f, skipButtonPaint)

            val skipTextPaint = Paint(buttonTextPaint).apply {
                color = Color.WHITE
            }
            canvas.drawText(
                "Skip",
                skipButtonRect.centerX(),
                skipButtonRect.centerY() + skipTextPaint.textSize / 3,
                skipTextPaint
            )
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        highlightAnimator?.cancel()
    }
}