# AGENTS.md

## Project Overview
This is an Android application project ("Umamusume Future Sight").
As the repository is currently in its initial state, these guidelines serve as the foundational standards for all future development.

## 1. Build & Test Commands
This project uses Gradle for build automation.

### Build
- **Assemble Debug APK:**
  ```bash
  ./gradlew assembleDebug
  ```
- **Clean Build:**
  ```bash
  ./gradlew clean build
  ```

### Testing
- **Run All Unit Tests:**
  ```bash
  ./gradlew test
  ```
- **Run Specific Test Class:**
  ```bash
  ./gradlew testDebugUnitTest --tests "com.example.package.MyClassTest"
  ```
- **Run Specific Test Method:**
  ```bash
  ./gradlew testDebugUnitTest --tests "com.example.package.MyClassTest.testMethodName"
  ```
- **Linting:**
  ```bash
  ./gradlew lintDebug
  ```

## 2. Code Style & Standards

### Language
- **Kotlin** is the primary language. Java should be avoided for new code.
- Adhere strictly to the [Official Kotlin Style Guide](https://developer.android.com/kotlin/style-guide).

### Formatting
- Use **4 spaces** for indentation.
- Max line length: **100 characters** (soft limit), **120 characters** (hard limit).
- Braces: All control structures (if, else, for, do, while) must use braces.
- **Imports:**
  - No wildcard imports (`import foo.*`).
  - Sort alphabetically.
  - Separate system/library imports from project imports with a blank line.

### Architecture
- **Pattern:** MVVM (Model-View-ViewModel) or MVI.
- **UI:** Jetpack Compose is preferred over XML layouts.
- **Dependency Injection:** Hilt (recommended) or Koin.
- **Concurrency:** Kotlin Coroutines and Flow.

### Naming Conventions
- **Classes/Objects:** `PascalCase` (e.g., `RaceCalculator`).
- **Functions/Variables:** `camelCase` (e.g., `calculateSpeed`).
- **Constants:** `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_COUNT`).
- **Composables:** `PascalCase` (noun-like, e.g., `EventCard`).
- **Layout Files (if XML used):** `snake_case` (e.g., `activity_main.xml`).

### Type Safety
- Avoid `!!` (double-bang) operators. Use `?.let`, `?:` (Elvis operator), or smart casts.
- Use explicit return types for public API methods.
- Define data classes for API responses and domain models.

### Error Handling
- Use `runCatching` for encapsulated failure handling where appropriate.
- Do not swallow exceptions. Log them or propagate a sealed `Result` class (e.g., `Result<Success, Error>`).

## 3. File Structure
Follow the standard Android directory structure:
```
app/
  src/
    main/
      java/com/saxpenguin/umamusumefuturesight/
        data/        # Repositories, API, Room DB
        di/          # Dependency Injection modules
        domain/      # Use cases, Interfaces
        ui/          # Compose screens, ViewModels, Themes
      res/           # Static resources
    test/            # Unit tests
    androidTest/     # Instrumented tests
```

## 4. Documentation
- **KDoc:** Required for all public classes and complex methods.
- **Comments:** Explain *why*, not *what*. Code should be self-documenting.

## 5. Git Conventions
- **Commit Messages:** Conventional Commits style.
  - `feat: add race simulation logic`
  - `fix: crash on rotation`
  - `chore: update gradle dependencies`
- **Branching:** Create feature branches from `main`.

---
*Note: Since this repository was initialized with minimal files, strictly adhere to these modern Android standards when scaffolding the application.*
