# AGENTS.md

## Project Overview

Umamusume Future Sight is an Android app built with Jetpack Compose.
Repository is in early development with MVVM scaffolding.

## 1. Build, Lint, Test

### Prereqs
- Android Studio Hedgehog (2023.1.1)+
- JDK 17
- Android SDK 34 (compileSdk)

### Build
```bash
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew clean
./gradlew clean build
```

### Unit Tests
```bash
./gradlew test
./gradlew testDebugUnitTest
./gradlew testDebugUnitTest --tests "com.saxpenguin.umamusumefuturesight.data.BannerRepositoryTest"
./gradlew testDebugUnitTest --tests "com.saxpenguin.umamusumefuturesight.data.BannerRepositoryTest.testFetchBanners"
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.saxpenguin.umamusumefuturesight.MainActivityTest
```

### Lint
```bash
./gradlew lintDebug
./gradlew lintRelease
./gradlew lint
```

### Coverage
```bash
./gradlew testDebugUnitTestCoverage
```

## 2. Code Style & Architecture

### Language and Tooling
- Kotlin only for new code (avoid Java).
- Kotlin 1.8.20, JVM target 17.
- Follow the official Kotlin style guide.

### Formatting
- 4 spaces, no tabs.
- Line length: 100 soft, 120 hard.
- Braces required for all control structures.
- Trailing commas required in multi-line parameter lists.

### Imports
- No wildcard imports.
- Sort alphabetically within groups.
- Separate Android/system, project, and third-party groups with blank lines.

Example:
```kotlin
import android.content.Context
import android.os.Bundle

import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.ui.MainScreen

import kotlinx.coroutines.flow.Flow
```

### Project Structure
```
com.saxpenguin.umamusumefuturesight/
├── data/        # Repositories, data sources, API clients
├── model/       # Domain models and data classes
├── ui/          # Compose screens, ViewModels, components
│   └── theme/   # Theme definitions
└── di/          # Dependency Injection
```

### Architecture
- MVVM with Compose + Material3.
- State: Compose state + ViewModel.
- Concurrency: Coroutines and Flow.

### Naming
- Classes/objects: `PascalCase`.
- Functions/variables: `camelCase`.
- Constants: `UPPER_SNAKE_CASE`.
- Composables: `PascalCase`, noun-like.
- XML resources: `snake_case`.

### Type Safety
- Never use `!!`.
- Public API methods must have explicit return types.
- Prefer `Result<T>` or sealed classes for operation results.

Example:
```kotlin
fun fetchBanner(id: String): Result<Banner> = runCatching {
    apiService.getBanner(id)
}
```

### Error Handling
- Use `runCatching` for encapsulated failures.
- Do not swallow exceptions; log or propagate.
- Prefer sealed classes for domain errors.

Example:
```kotlin
sealed class BannerError {
    data class NetworkError(val message: String) : BannerError()
    data class NotFound(val id: String) : BannerError()
}
```

### Compose Guidelines
- Add `@Preview` for UI components when feasible.
- Keep Composables focused and testable; delegate side effects to ViewModels.

### Documentation
- KDoc required for public classes and methods with non-obvious behavior.
- Comments explain why, not what.

## 3. Testing Notes

- Instrumentation runner: `com.saxpenguin.umamusumefuturesight.HiltTestRunner`.
- Test directories need to exist under `app/src/test` and `app/src/androidTest`.
- For single-test runs, use the fully-qualified class or method name with `--tests`.

## 4. Dependencies and Stack

- Gradle 8.2, AGP 8.0.2.
- Compose compiler 1.4.6, BOM 2023.08.00.
- DI: Hilt.
- Storage: Room.
- Networking: Retrofit, OkHttp, Kotlinx Serialization.
- Images: Coil.
- Testing: JUnit4, MockK, Turbine, Coroutines test.

## 5. Git Conventions

### Commit Messages
Use Conventional Commits:
```
<type>: <description>
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`.

Examples:
```
feat: add banner detail screen
fix: resolve race condition in repository
chore: update Gradle to 8.2
```

### Branching
- Main: `main`
- Features: `feature/short-description`
- Fixes: `fix/short-description`

## 6. Security and Secrets

- Do not commit secrets.
- `app/build.gradle.kts` contains placeholder signing config values; treat as local-only.

## 7. Cursor/Copilot Rules

- No `.cursor/rules`, `.cursorrules`, or `.github/copilot-instructions.md` found.
