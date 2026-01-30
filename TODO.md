# TODO List - Umamusume Future Sight

## Phase 1: Infrastructure & Architecture Setup
- [x] **Dependency Injection (Hilt)**
    - [x] Add Hilt Gradle plugin and dependencies
    - [x] Create Application class annotated with `@HiltAndroidApp`
    - [x] Configure `AndroidManifest.xml` to use the application class
    - [x] Set up basic Hilt modules (`AppModule`, `DataModule`)
- [ ] **Navigation**
    - [ ] Add `androidx.navigation:navigation-compose` dependency
    - [ ] Set up `NavHost` in `MainActivity`
    - [ ] Define routes (e.g., `BannerList`, `BannerDetail`, `Planner`)
- [ ] **Networking (Retrofit)**
    - [ ] Add Retrofit & OkHttp dependencies
    - [ ] Add Kotlinx Serialization dependency
    - [ ] Create API interface definitions
    - [ ] Configure Retrofit instance in Hilt module
- [ ] **Local Database (Room)**
    - [ ] Add Room dependencies (runtime, ktx, compiler)
    - [ ] Define Entity classes for Banners
    - [ ] Create DAO interfaces
    - [ ] Setup Room Database instance
- [ ] **Image Loading (Coil)**
    - [ ] Add Coil dependency
    - [ ] Create reusable `NetworkImage` composable

## Phase 2: Core Feature - Banner Tracking
- [x] **Data Layer**
    - [x] Implement `BannerRepository` with basic setup
    - [ ] Create data models for Banner info (Type, Dates, Featured Characters) - Already done
    - [ ] Implement caching logic (Room/Retrofit)
- [ ] **UI - Banner List**
    - [ ] Design Banner List Item card (Image, Title, Date range)
    - [ ] Implement filtering (Character/Support Card)
    - [ ] Implement sorting (Date, Type)
    - [ ] Add loading and error states
- [ ] **UI - Banner Detail**
    - [ ] Create Detail Screen
    - [ ] Display featured characters/cards details
    - [ ] Add "link to wiki/external site" functionality

## Phase 3: Future Planning Utilities
- [ ] **Resource Management**
    - [ ] Create data model for User Resources (Jewels, Tickets)
    - [ ] Implement "Resource Calculator" logic
- [ ] **Planning UI**
    - [ ] Create Planner Screen
    - [ ] Allow users to mark "Target Banners"
    - [ ] Calculate estimated savings by banner date

## Phase 4: Testing & Quality
- [ ] **Unit Tests**
    - [ ] Setup JUnit 5 (optional) or JUnit 4
    - [ ] Add MockK and Turbine dependencies
    - [ ] Write tests for `BannerRepository`
    - [ ] Write tests for ViewModels
- [ ] **UI Tests**
    - [ ] Write Compose instrumented tests for MainScreen
    - [ ] Test navigation flows
- [ ] **CI/CD**
    - [ ] Setup GitHub Actions for Build & Test
    - [ ] Configure Lint checks on PR

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
