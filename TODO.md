# TODO List - Umamusume Future Sight

## Current Status Summary
- MVVM + Compose screens exist: banner list, banner detail, planner.
- Hilt, Room, Retrofit, Coil are wired and working.
- Banner data is seeded from assets with fallback to API.
- Unit and instrumented tests exist and run.

## Phase 1: Infrastructure & Architecture Setup
- [x] **Dependency Injection (Hilt)**
    - [x] Add Hilt Gradle plugin and dependencies
    - [x] Create Application class annotated with `@HiltAndroidApp`
    - [x] Configure `AndroidManifest.xml` to use the application class
    - [x] Set up Hilt modules (`AppModule`, `DataModule`, `NetworkModule`, `DatabaseModule`)
- [x] **Navigation**
    - [x] Add `androidx.navigation:navigation-compose` dependency
    - [x] Set up `NavHost` and routes (`BannerList`, `BannerDetail`, `Planner`)
- [x] **Networking (Retrofit)**
    - [x] Add Retrofit & OkHttp dependencies
    - [x] Add Kotlinx Serialization dependency
    - [x] Create API interface definitions
    - [x] Configure Retrofit instance in Hilt module
    - [ ] Replace placeholder `BASE_URL` and endpoints with real data source
- [x] **Local Database (Room)**
    - [x] Add Room dependencies (runtime, ktx, compiler)
    - [x] Define entity + DAO for banners
    - [x] Setup Room database instance
    - [ ] Add migration strategy (avoid `fallbackToDestructiveMigration` in release)
- [x] **Image Loading (Coil)**
    - [x] Add Coil dependency
    - [x] Create reusable `NetworkImage` composable

## Phase 2: Core Feature - Banner Tracking
- [x] **Data Layer**
    - [x] Implement `BannerRepository`
    - [x] Seed data from assets (`timetable.json`, `characters.json`, `cards.json`)
    - [x] Store banners in Room and reuse cached data
    - [ ] Add explicit error surface (avoid swallow-all catch blocks)
- [x] **UI - Banner List**
    - [x] Banner list item card (image, title, date range)
    - [x] Filtering (Character/Support Card)
    - [x] Sorting (Date, Type)
    - [x] Loading and error states
- [x] **UI - Banner Detail**
    - [x] Detail screen
    - [x] Show dates and status
    - [x] External link button (uses `linkUrl` if provided)
    - [ ] Populate `linkUrl` from real data source

## Phase 3: Future Planning Utilities
- [x] **Resource Management**
    - [x] User resources model (jewels, tickets)
    - [x] Resource calculator
- [x] **Planning UI**
    - [x] Planner screen
    - [x] Target banners stored in local DB
    - [x] Projected savings by banner date
    - [ ] Add cumulative projection (account for earlier banner pulls)

## Phase 4: Testing & Quality
- [x] **Unit Tests**
    - [x] Setup JUnit 4 + MockK + Turbine
    - [x] `BannerRepositoryTest`
    - [x] `PlannerViewModelTest`
    - [ ] Add tests for `MainViewModel` sorting/filtering
- [x] **Instrumented Tests**
    - [x] Hilt test runner
    - [x] Basic navigation launch test
    - [ ] Add UI assertions with fake data source

## Phase 5: Polish & Release
- [x] **Visuals**
    - [x] App icon
    - [x] Splash screen
    - [x] Material3 theme (light/dark)
- [x] **Build Configuration**
    - [x] ProGuard/R8 config wired
    - [x] Release signing config placeholder
    - [ ] Generate and verify signed APK/AAB for release

## Phase 6: Data & UX Improvements
- [ ] Refresh asset data and add update workflow
- [ ] Add in-app settings for TW offset days
- [ ] Add localization strategy for mixed JP/EN/zh strings
