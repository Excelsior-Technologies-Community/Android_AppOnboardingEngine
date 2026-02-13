package com.ext.android_app_onboarding_library



/**
 * Listener interface for onboarding events
 */
interface OnboardingListener {
    /**
     * Called when the current step changes
     * @param currentStep The current step index (0-based)
     * @param totalSteps The total number of steps
     */
    fun onStepChanged(currentStep: Int, totalSteps: Int) {}

    /**
     * Called when the user clicks the skip button
     */
    fun onSkip() {}

    /**
     * Called when the onboarding is complete (all steps shown or skipped)
     */
    fun onOnboardingComplete() {}
}