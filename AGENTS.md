# AGENTS.md

## Project Overview

Umamusume Future Sight - An Android application built with Jetpack Compose.

**Current Status:** Initial development phase with basic MVVM architecture scaffolded.

## 1. Build & Test Commands

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK 34 (compileSdk)

### Build Commands
```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew clean                  # Clean build artifacts
./gradlew clean build            # Full clean build
```

### Test Commands
**Important:** Test directories (`test/` and `androidTest/`) need to be created under `app/src/`.

```bash
./gradlew test                   # Run all unit tests
./gradlew testDebugUnitTest      # Run debug unit tests only
./gradlew testDebugUnitTest --tests "com.saxpenguin.umamusumefuturesight.data.BannerRepositoryTest"
./gradlew testDebugUnitTest --tests "com.saxpenguin.umamusumefuturesight.data.BannerRepositoryTest.testFetchBanners"
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.saxpenguin.umamusumefuturesight.MainActivityTest
```

### Lint Commands
```bash
./gradlew lintDebug              # Run lint on debug variant
./gradlew lintRelease            # Run lint on release variant
./gradlew lint                   # Generate lint report
```

## 2. Code Style & Standards

### Language
- **Primary:** Kotlin (strictly avoid Java for new code)
- **Target JVM:** 1.8, **Kotlin:** 1.8.20
- **Style Guide:** [Official Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)

### Formatting
- **Indentation:** 4 spaces (no tabs)
- **Line Length:** 100 (soft), 120 (hard limit)
- **Braces:** Required for all control structures
- **Trailing Commas:** Required in multi-line parameter lists

### Imports
```kotlin
// CORRECT - No wildcards, sorted alphabetically, groups separated by blank line
import android.content.Context
import android.os.Bundle

import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.ui.MainScreen
import kotlinx.coroutines.flow.Flow

// INCORRECT - Wildcard imports
import android.content.*
import com.saxpenguin.umamusumefuturesight.*
```

**Rules:**
- No wildcard imports (`import foo.*`)
- Sort alphabetically within groups
- Separate Android/system imports from project imports
- Separate project imports from third-party library imports

### Architecture
- **Pattern:** MVVM (Model-View-ViewModel)
- **UI:** Jetpack Compose with Material3
- **State:** Compose State + ViewModel
- **Concurrency:** Kotlin Coroutines and Flow
- **Package Structure:**
```
com.saxpenguin.umamusumefuturesight/
├── data/        # Repositories, data sources, API clients
├── model/       # Domain models and data classes
├── ui/          # Compose screens, ViewModels, components
│   └── theme/   # Theme definitions (Color, Theme, Type)
└── di/          # Dependency Injection (to be added)
```

### Naming Conventions
- **Classes/Objects:** `PascalCase` (e.g., `BannerRepository`, `MainViewModel`)
- **Functions/Variables:** `camelCase` (e.g., `fetchBanners`, `isLoading`)
- **Constants:** `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_COUNT`, `BASE_URL`)
- **Composables:** `PascalCase` noun-like (e.g., `MainScreen`, `BannerCard`)
- **XML Resources:** `snake_case` (e.g., `activity_main`)

### Type Safety
- **Never use `!!`** - use `?.let`, `?:`, or smart casts instead
- Explicit return types required for public API methods
- Use `Result<T>` or sealed classes for operation results

```kotlin
// CORRECT
fun fetchBanner(id: String): Result<Banner> = runCatching {
    apiService.getBanner(id)
}
val banner = bannerRepository.getBanner(id).getOrNull() ?: return

// INCORRECT
fun fetchBanner(id: String): Banner? = apiService.getBanner(id)!!
```

### Error Handling
- Use `runCatching` for encapsulated failure handling
- Never swallow exceptions - log them or propagate
- Prefer sealed classes for domain errors

```kotlin
sealed class BannerError {
    data class NetworkError(val message: String) : BannerError()
    data class NotFound(val id: String) : BannerError()
}

fun fetchBanner(id: String): Result<Banner, BannerError> = runCatching {
    apiService.getBanner(id)
}.fold(
    onSuccess = { Result.success(it) },
    onFailure = { Result.failure(BannerError.NetworkError(it.message ?: "Unknown")) }
)
```

## 3. Documentation
- **KDoc:** Required for all public classes and methods with non-obvious behavior
- **Comments:** Explain *why*, not *what*. Code should be self-documenting
- **Composable Previews:** Add `@Preview` annotations for UI components

```kotlin
/**
 * Displays a scrollable list of banners with loading and error states.
 * @param banners List of banners to display
 * @param isLoading Whether data is currently loading
 * @param onBannerClick Callback when a banner is clicked
 */
@Composable
fun BannerList(
    banners: List<Banner>,
    isLoading: Boolean,
    onBannerClick: (Banner) -> Unit
) {
    // Implementation
}
```

## 4. Git Conventions

### Commit Messages
Follow [Conventional Commits](https://www.conventionalcommits.org/):
```
<type>: <description>
```

**Types:**
- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation changes
- `style:` Code style changes (formatting)
- `refactor:` Code refactoring
- `test:` Adding/updating tests
- `chore:` Build process changes

**Examples:**
```
feat: add banner detail screen
fix: resolve race condition in repository
chore: update Gradle to 8.2
test: add BannerRepository unit tests
```

### Branching
- Main: `main`
- Features: `feature/banner-detail-screen`
- Fixes: `fix/memory-leak-in-viewmodel`

## 5. Project Configuration

### Tech Stack
- **Gradle:** 8.2, **AGP:** 8.0.2, **Kotlin:** 1.8.20
- **SDK:** Compile 34, Min 24, Target 34
- **Compose:** Compiler 1.4.6, BOM 2023.08.00

### Future Dependencies
- **DI:** Hilt (not yet configured)
- **Networking:** Retrofit + OkHttp + Kotlinx Serialization
- **Storage:** Room
- **Images:** Coil
- **Testing:** MockK, Turbine

---

**Note:** This project is in early development. Test directories and DI configuration need to be added.
