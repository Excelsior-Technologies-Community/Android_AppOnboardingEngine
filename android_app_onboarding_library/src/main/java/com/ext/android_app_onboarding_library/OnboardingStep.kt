package com.ext.android_app_onboarding_library

import android.view.View

/**
 * Represents a single step in the onboarding flow
 */
data class OnboardingStep(
    val targetView: View,
    val title: String,
    val description: String,
    val highlightShape: HighlightShape = HighlightShape.CIRCLE,
    val customHighlightRadius: Float? = null,
    val delay: Long = 0L
)

/**
 * Shape of the highlight around the target view
 */
enum class HighlightShape {
    CIRCLE,
    RECTANGLE,
    ROUNDED_RECTANGLE
}