# SpeedMathNativeClone

Native Kotlin / Jetpack Compose implementation of the Speed Math calculation trainer.

## Current repair focus

This package repairs the app at the product-logic level, not only the UI layer:

- Arithmetic Memory is a dedicated running-total memory workout.
- Revision content is stored in a native SQLite database seeded from `app/src/main/assets/revision_seed.tsv`.
- Arithmetic Memory round history is stored in native SQLite tables.
- Revision search and sort are implemented manually in `SearchSortEngine`.
- Navigation is now custom in-app state, not Navigation Compose.
- The controller is plain Kotlin state, not lifecycle ViewModel dependency.
- No Room, no Retrofit, no Hilt, no image loader, no chart library, no external animation library, no third-party dependencies.
- Only official AndroidX / Jetpack Compose runtime libraries remain for native UI rendering.

## Important files

```text
app/src/main/assets/revision_seed.tsv
app/src/main/java/com/sameer/speedmath/data/NativeSpeedMathDatabase.kt
app/src/main/java/com/sameer/speedmath/logic/SearchSortEngine.kt
app/src/main/java/com/sameer/speedmath/logic/ArithmeticMemoryEngine.kt
app/src/main/java/com/sameer/speedmath/ui/screens/ArithmeticMemoryScreen.kt
app/src/main/java/com/sameer/speedmath/ui/screens/RevisionTableScreen.kt
app/src/main/java/com/sameer/speedmath/MainActivity.kt
```

## Build

Open in Android Studio, use the Embedded JDK/JBR, Sync, Clean, Rebuild, Run.

Windows:

```powershell
.\gradlew.bat --stop
.\gradlew.bat clean :app:assembleDebug
```

macOS/Linux:

```bash
./gradlew --stop
./gradlew clean :app:assembleDebug
```
