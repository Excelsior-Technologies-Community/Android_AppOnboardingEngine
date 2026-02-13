package com.ext.android_apponboardingengine

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ext.android_app_onboarding_library.*

class MainActivity : AppCompatActivity() {

    private lateinit var overlay: OnboardingOverlay
    private lateinit var titleText: TextView
    private lateinit var btnFeature1: Button
    private lateinit var btnFeature2: Button
    private lateinit var btnFeature3: Button
    private lateinit var btnStartOnboarding: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        titleText = findViewById(R.id.titleText)
        btnFeature1 = findViewById(R.id.btnFeature1)
        btnFeature2 = findViewById(R.id.btnFeature2)
        btnFeature3 = findViewById(R.id.btnFeature3)
        btnStartOnboarding = findViewById(R.id.btnStartOnboarding)
        overlay = findViewById(R.id.onboardingOverlay)

        // Configure the onboarding overlay
        setupOnboarding()

        // Start onboarding button
        btnStartOnboarding.setOnClickListener {
            startOnboarding()
        }

        // Feature button click listeners
        btnFeature1.setOnClickListener {
            Toast.makeText(this, "Feature 1 clicked!", Toast.LENGTH_SHORT).show()
        }

        btnFeature2.setOnClickListener {
            Toast.makeText(this, "Feature 2 clicked!", Toast.LENGTH_SHORT).show()
        }

        btnFeature3.setOnClickListener {
            Toast.makeText(this, "Feature 3 clicked!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupOnboarding() {
        // Create custom configuration
        val config = OnboardingConfig.Builder()
            .overlayColor(Color.parseColor("#DD000000"))
            .highlightPadding(40f)
            .titleTextSize(70f)
            .descriptionTextSize(45f)
            .titleTextColor(Color.WHITE)
            .descriptionTextColor(Color.parseColor("#CCCCCC"))
            .arrowColor(Color.parseColor("#4CAF50"))
            .showArrow(true)
            .showSkipButton(true)
            .showStepIndicators(true)
            .allowTargetClick(true)
            .animateHighlight(true)
            .roundedCornerRadius(25f)
            .build()

        overlay.setConfig(config)

        // Set up listener for onboarding events
        overlay.setListener(object : OnboardingListener {
            override fun onStepChanged(currentStep: Int, totalSteps: Int) {
                Toast.makeText(
                    this@MainActivity,
                    "Step ${currentStep + 1} of $totalSteps",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onSkip() {
                Toast.makeText(this@MainActivity, "Onboarding skipped", Toast.LENGTH_SHORT).show()
            }

            override fun onOnboardingComplete() {
                Toast.makeText(this@MainActivity, "Onboarding complete!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startOnboarding() {
        // Create onboarding steps with different highlight shapes
        val steps = listOf(
            OnboardingStep(
                targetView = titleText,
                title = "Welcome!",
                description = "This is your app's main title. Let's explore the amazing features together!",
                highlightShape = HighlightShape.ROUNDED_RECTANGLE
            ),
            OnboardingStep(
                targetView = btnFeature1,
                title = "Feature 1",
                description = "Click here to access the first awesome feature. This button demonstrates circular highlights.",
                highlightShape = HighlightShape.CIRCLE
            ),
            OnboardingStep(
                targetView = btnFeature2,
                title = "Feature 2",
                description = "This is your second feature. Notice the rectangular highlight around this button!",
                highlightShape = HighlightShape.RECTANGLE
            ),
            OnboardingStep(
                targetView = btnFeature3,
                title = "Feature 3",
                description = "Finally, this is the third feature with a rounded rectangle highlight. You're all set!",
                highlightShape = HighlightShape.ROUNDED_RECTANGLE
            )
        )

        overlay.setSteps(steps)
        overlay.visibility = android.view.View.VISIBLE
    }

    override fun onBackPressed() {
        if (overlay.visibility == android.view.View.VISIBLE) {
            overlay.skip()
        } else {
            super.onBackPressed()
        }
    }
}