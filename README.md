# **🚀 Android App Onboarding Library**


---
A customizable and lightweight Android Onboarding Overlay Library that helps you create beautiful guided tours for your app.

---

## ✨ **Features**

- 🎯 Highlight any View dynamically

- 🔵 Multiple highlight shapes:

   - Circle

   - Rectangle

   - Rounded Rectangle

- 🎨 Fully customizable UI:

   - Overlay color

   - Arrow color

   - Text size & color

   - Padding & corner radius

- 🏹 Animated arrow pointing to target

- 🎬 Smooth highlight animation

- 🔢 Step indicators

- ⏭ Skip & Next/Done buttons

- 👆 Optional click on highlighted target

- 🛠 Builder pattern configuration

- 📱 Works with any layout



  ---

# **Preview**
---
<p align="center">
  <img src="https://github.com/S13reya/Android_SwipeTransition/blob/stages/app/src/main/assets/demovideo.gif" height="320"/>




</p>


## ⚡ **Installation**

**Step 1:** Add JitPack repository to your root build.gradle:

```gradle
maven { url = uri("https://jitpack.io") }
```

**Step 2:** Add the dependency in your app `build.gradle` (example if hosted on JitPack):  

```gradle
dependencies {
	        implementation 'com.github.Excelsior-Technologies-Community:Android_SwipeTransition:1.0.0'

}
```
## ⚡ **attrs file**

```

<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="OnboardingOverlay">
        <!-- Overlay colors -->
        <attr name="overlayBackgroundColor" format="color"/>

        <!-- Highlight settings -->
        <attr name="highlightRadius" format="dimension"/>

        <!-- Text colors -->
        <attr name="titleTextColor" format="color"/>
        <attr name="descriptionTextColor" format="color"/>

        <!-- Text sizes -->
        <attr name="titleTextSize" format="dimension"/>
        <attr name="descriptionTextSize" format="dimension"/>

        <!-- Arrow settings -->
        <attr name="arrowColor" format="color"/>
    </declare-styleable>
</resources>
```

## ⚡ **Usage**

1. Add the Overlay in your Layout

```
  <com.ext.android_app_onboarding_library.OnboardingOverlay
    android:id="@+id/onboardingOverlay"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:visibility="gone"
    android:clickable="true"
    android:focusable="true"
    app:overlayBackgroundColor="#DD000000"
    app:highlightRadius="200dp"
    app:titleTextSize="60sp"
    app:descriptionTextSize="42sp"
    app:arrowColor="#4CAF50"/>

```

## ⚡ **Main Activity**


Step 1: Configure Overlay in Activity
```
val config = OnboardingConfig.Builder()
    .overlayColor(Color.parseColor("#DD000000"))
    .highlightPadding(40f)
    .titleTextSize(70f)
    .descriptionTextSize(45f)
    .arrowColor(Color.parseColor("#4CAF50"))
    .showArrow(true)
    .showSkipButton(true)
    .animateHighlight(true)
    .build()

overlay.setConfig(config)

```
Step 2: Create Onboarding Steps

```
val steps = listOf(
    OnboardingStep(
        targetView = titleText,
        title = "Welcome!",
        description = "This is your main title.",
        highlightShape = HighlightShape.ROUNDED_RECTANGLE
    ),
    OnboardingStep(
        targetView = btnFeature1,
        title = "Feature 1",
        description = "Click here to access this feature.",
        highlightShape = HighlightShape.CIRCLE
    )
)

overlay.setSteps(steps)
overlay.visibility = View.VISIBLE

```

Step 3: Add Listener (Optional)

```
overlay.setListener(object : OnboardingListener {
    override fun onStepChanged(currentStep: Int, totalSteps: Int) {
        Log.d("Onboarding", "Step ${currentStep + 1} of $totalSteps")
    }

    override fun onSkip() {
        Log.d("Onboarding", "Skipped")
    }

    override fun onOnboardingComplete() {
        Log.d("Onboarding", "Completed")
    }
})

```

##  **🔷 Highlight Shapes**

```
HighlightShape.CIRCLE
HighlightShape.RECTANGLE
HighlightShape.ROUNDED_RECTANGLE
```

## **📄 License**

**MIT License**  
```
Copyright (c) 2025 Excelsior Technologies

Permission is hereby granted, free of charge, to any person obtaining a copy  
of this software and associated documentation files (the "Software"), to deal  
in the Software without restriction, including without limitation the rights  
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
copies of the Software, and to permit persons to whom the Software is  
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all  
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED **"AS IS"**, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```



  
