package com.ext.android_app_onboarding_library

import android.graphics.Color

/**
 * Configuration class for the onboarding overlay
 */
data class OnboardingConfig(
    // Overlay colors
    var overlayColor: Int = Color.parseColor("#CC000000"),

    // Highlight settings
    var highlightRadius: Float = 200f,
    var highlightPadding: Float = 30f,
    var roundedCornerRadius: Float = 20f,

    // Text settings
    var titleTextColor: Int = Color.WHITE,
    var descriptionTextColor: Int = Color.LTGRAY,
    var titleTextSize: Float = 60f,
    var descriptionTextSize: Float = 42f,

    // Arrow settings
    var arrowColor: Int = Color.WHITE,
    var showArrow: Boolean = true,

    // UI elements
    var showSkipButton: Boolean = true,
    var showStepIndicators: Boolean = true,

    // Behavior
    var allowTargetClick: Boolean = true,
    var animateHighlight: Boolean = true,
    var contentPadding: Float = 50f
) {
    class Builder {
        private val config = OnboardingConfig()

        fun overlayColor(color: Int) = apply { config.overlayColor = color }
        fun highlightRadius(radius: Float) = apply { config.highlightRadius = radius }
        fun highlightPadding(padding: Float) = apply { config.highlightPadding = padding }
        fun roundedCornerRadius(radius: Float) = apply { config.roundedCornerRadius = radius }

        fun titleTextColor(color: Int) = apply { config.titleTextColor = color }
        fun descriptionTextColor(color: Int) = apply { config.descriptionTextColor = color }
        fun titleTextSize(size: Float) = apply { config.titleTextSize = size }
        fun descriptionTextSize(size: Float) = apply { config.descriptionTextSize = size }

        fun arrowColor(color: Int) = apply { config.arrowColor = color }
        fun showArrow(show: Boolean) = apply { config.showArrow = show }

        fun showSkipButton(show: Boolean) = apply { config.showSkipButton = show }
        fun showStepIndicators(show: Boolean) = apply { config.showStepIndicators = show }

        fun allowTargetClick(allow: Boolean) = apply { config.allowTargetClick = allow }
        fun animateHighlight(animate: Boolean) = apply { config.animateHighlight = animate }
        fun contentPadding(padding: Float) = apply { config.contentPadding = padding }

        fun build() = config
    }
}