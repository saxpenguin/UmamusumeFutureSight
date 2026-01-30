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
- [ ] **UI - Banner List**
    - [x] Design Banner List Item card (Image, Title, Date range)
    - [x] Implement filtering (Character/Support Card)
    - [x] Implement sorting (Date, Type)
    - [x] Add loading and error states
- [ ] **UI - Banner Detail**
    - [x] Create Detail Screen
    - [x] Display featured characters/cards details
    - [x] Add "link to wiki/external site" functionality

## Phase 3: Future Planning Utilities
- [ ] **Resource Management**
    - [x] Create data model for User Resources (Jewels, Tickets)
    - [x] Implement "Resource Calculator" logic
- [ ] **Planning UI**
    - [x] Create Planner Screen
    - [ ] Allow users to mark "Target Banners"
    - [x] Calculate estimated savings by banner date

## Phase 4: Testing & Quality
- [ ] **Unit Tests**
    - [ ] Setup JUnit 5 (optional) or JUnit 4
    - [ ] Add MockK and Turbine dependencies
    - [ ] Write tests for `BannerRepository`
    - [ ] Write tests for ViewModels
- [ ] **UI Tests**
    - [ ] Write Compose instrumented tests for MainScreen
    - [ ] Test navigation flows

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
