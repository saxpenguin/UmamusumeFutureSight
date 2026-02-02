# Umamusume Future Sight

賽馬娘未來視 - An Android application for Umamusume Pretty Derby game assistance.

## Overview

Umamusume Future Sight is an Android application built with Jetpack Compose that provides tools and information for the Umamusume Pretty Derby mobile game. The app features a banner tracking system and future planning utilities.

## Features

- **Banner Tracking**: View upcoming and current in-game banners
- **Future Planning**: Plan your resources and strategies ahead
- **Modern UI**: Built with Jetpack Compose and Material3 design
- **MVVM Architecture**: Clean, maintainable code structure

## Tech Stack

- **Language**: Kotlin 1.8.20 (JVM target 17)
- **UI Framework**: Jetpack Compose (BOM 2023.08.00, compiler 1.4.6)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Build System**: Gradle 8.2 with Android Gradle Plugin 8.0.2
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

### Dependencies

- Jetpack Compose with Material3
- Kotlin Coroutines and Flow
- AndroidX Lifecycle (ViewModel, Compose integration)
- Hilt (DI)
- Retrofit + OkHttp + Kotlinx Serialization (networking)
- Room (local storage)
- Coil (image loading)
- JUnit 4, MockK, Turbine, Coroutines test (unit testing)
- Espresso (UI testing)

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK 34

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/UmamusumeFutureSight.git
   cd UmamusumeFutureSight
   ```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Run the app on an emulator or physical device

## Building

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Clean Build
```bash
./gradlew clean build
```

## Testing

### Run Unit Tests
```bash
./gradlew test
./gradlew testDebugUnitTest
```

### Run a Single Unit Test
```bash
./gradlew testDebugUnitTest --tests "com.saxpenguin.umamusumefuturesight.data.BannerRepositoryTest"
./gradlew testDebugUnitTest --tests "com.saxpenguin.umamusumefuturesight.data.BannerRepositoryTest.testFetchBanners"
```

### Run Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### Run Lint
```bash
./gradlew lintDebug
```

For more detailed build and test commands, see [AGENTS.md](AGENTS.md).

## Project Structure

```
app/src/main/java/com/saxpenguin/umamusumefuturesight/
├── data/           # Repositories and data sources
│   └── BannerRepository.kt
├── model/          # Domain models
│   └── Banner.kt
├── ui/             # Compose UI components
│   ├── MainScreen.kt
│   ├── MainViewModel.kt
│   └── theme/      # Theme definitions
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── MainActivity.kt
```

## Contributing

Please read [AGENTS.md](AGENTS.md) for detailed information on:
- Code style guidelines
- Build and test commands
- Git conventions
- Project standards

## Development Status

⚠️ **Early Development**: The project is in its initial phase with core infrastructure configured.

## License

[Add your license information here]

## Acknowledgments

- Umamusume Pretty Derby is a trademark of Cygames, Inc.
- This is a fan-made tool and is not affiliated with or endorsed by Cygames.
