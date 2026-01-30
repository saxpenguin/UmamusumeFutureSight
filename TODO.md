# TODO List - Umamusume Future Sight

## Phase 1: Infrastructure & Architecture Setup
- [x] **Dependency Injection (Hilt)**
    - [x] Add Hilt Gradle plugin and dependencies
    - [x] Create Application class annotated with `@HiltAndroidApp`
    - [x] Configure `AndroidManifest.xml` to use the application class
    - [x] Set up basic Hilt modules (`AppModule`, `DataModule`)
- [x] **Navigation**
    - [x] Add `androidx.navigation:navigation-compose` dependency
    - [x] Set up `NavHost` in `MainActivity`
    - [x] Define routes (e.g., `BannerList`, `BannerDetail`, `Planner`)
- [x] **Networking (Retrofit)**
    - [x] Add Retrofit & OkHttp dependencies
    - [x] Add Kotlinx Serialization dependency
    - [x] Create API interface definitions
    - [x] Configure Retrofit instance in Hilt module
- [x] **Local Database (Room)**
    - [x] Add Room dependencies (runtime, ktx, compiler)
    - [x] Define Entity classes for Banners
    - [x] Create DAO interfaces
    - [x] Setup Room Database instance
- [x] **Image Loading (Coil)**
    - [x] Add Coil dependency
    - [x] Create reusable `NetworkImage` composable


## Phase 2: Core Feature - Banner Tracking
- [x] **Data Layer**
    - [x] Implement `BannerRepository` with basic setup
    - [x] Create data models for Banner info (Type, Dates, Featured Characters)
    - [x] Implement caching logic (Room/Retrofit)
- [x] **UI - Banner List**
    - [x] Design Banner List Item card (Image, Title, Date range)
    - [x] Implement filtering (Character/Support Card)
    - [x] Implement sorting (Date, Type)
    - [x] Add loading and error states
- [x] **UI - Banner Detail**
    - [x] Create Detail Screen
    - [x] Display featured characters/cards details
    - [x] Add "link to wiki/external site" functionality

## Phase 3: Future Planning Utilities
- [x] **Resource Management**
    - [x] Create data model for User Resources (Jewels, Tickets)
    - [x] Implement "Resource Calculator" logic
- [x] **Planning UI**
    - [x] Create Planner Screen
    - [x] Allow users to mark "Target Banners" (Save to local DB)
    - [x] Calculate estimated savings by banner date (Integrate Calculator with Target Banners)

## Phase 4: Testing & Quality
- [x] **Unit Tests**
    - [x] Setup JUnit 4 and Test Dependencies (MockK, Turbine)
    - [x] Write tests for `BannerRepository`
    - [x] Write tests for ViewModels
    - [x] Run tests and verify coverage

## Phase 5: Polish & Release
- [ ] **Visuals**
    - [ ] Add App Icon
    - [ ] Add Splash Screen
    - [ ] Refine Material3 Theme (Colors, Typography)
    - [ ] Support Dark/Light mode
- [ ] **Build Configuration**
    - [ ] Configure ProGuard/R8 rules
    - [ ] Setup Signing Config for release
    - [ ] Generate Signed APK/AAB
