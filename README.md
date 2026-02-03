# Weight Plate Calculator

A mobile Android app for quickly calculating weight plates needed to hit a specific weight on barbells, trap bars, or loading pins.

## Features

### Calculate Mode
- Enter a target weight and the app calculates which plates you need
- Shows plates needed per side for barbells and trap bars
- Shows total stack for loading pins
- Indicates when exact target weight cannot be achieved with available plates

### Reverse Mode
- Enter the plates currently on your equipment
- App calculates the total weight
- Useful for quickly checking weight on the bar

### Equipment Support
- **Preset equipment:**
  - Olympic Barbell (45 lb)
  - Trap Bar (60 lb)
  - EZ Curl Bar (25 lb)
  - Loading Pin (0 lb starting weight)
- **Custom equipment:** Add up to 10 custom bar weights
- **On-the-fly custom weight:** Use a one-time starting weight without saving it

### Plate Inventory
- Standard plate options: 100, 55, 45, 35, 25, 10, 5, 2.5, 1.25, 1, 0.75, 0.5, 0.25 lb
- Set how many of each plate you have available
- Calculator only uses plates you have in inventory
- Plates default to 0 - must be manually added

## Technical Details

- **Platform:** Android
- **Minimum SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **UI Framework:** Jetpack Compose with Material 3
- **Data Storage:** DataStore Preferences (secure local storage)
- **Architecture:** MVVM with ViewModels

## Security & Compliance

This app follows Google Play Store requirements and secure coding practices:
- No internet permissions required
- All data stored locally using secure DataStore
- No sensitive data collection
- Input validation on all user inputs
- ProGuard enabled for release builds

## Building

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device or emulator

```bash
./gradlew assembleDebug
```

## Project Structure

```
app/
├── src/main/
│   ├── java/com/weightplatecalculator/
│   │   ├── data/
│   │   │   ├── model/         # Data classes
│   │   │   └── repository/    # Data persistence
│   │   ├── ui/
│   │   │   ├── components/    # Reusable UI components
│   │   │   ├── screens/       # App screens
│   │   │   └── theme/         # Material theme
│   │   ├── util/              # Calculator logic
│   │   └── MainActivity.kt
│   └── res/                   # Android resources
└── build.gradle.kts
```

## License

All rights reserved.
