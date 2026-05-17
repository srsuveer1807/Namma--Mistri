# Namma-Mistri

Native Kotlin Android app built from the provided PDR.

## Features

- Material calculator for wall and room estimates
- Adjustable wall thickness, mortar ratio, and waste allowance
- Bill of Materials for bricks, cement bags, and sand loads
- Local material rates with cost estimation
- Labour diary with wages earned, advances paid, and balance due
- Site-wise photo gallery using Android image picker
- Offline persistence using SharedPreferences
- Kannada-friendly explanation text for estimation logic

## Open Directly In Android Studio

1. Open Android Studio.
2. Choose **File > Open**.
3. Select this project folder:

   `C:\Users\User\Documents\New project`

4. Let Gradle sync the project.
5. Run the `app` configuration on an emulator or Android phone.

Do not open only the `app` folder. Open the root folder that contains `settings.gradle`.

## Command-Line Note

The shell on this machine does not currently expose Java, Gradle, or the Android SDK, so command-line build verification and ZIP packaging were not available here. Android Studio should handle Gradle sync after opening the root folder.
