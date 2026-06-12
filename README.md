<p align="center">
  <img src="docs/assets/travio_icon.png" alt="Travio Logo" width="120" />
</p>

<h1 align="center">Travio</h1>

<p align="center">
  <b>Your AI-Powered Travel Companion</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" alt="Platform" />
  <img src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white" alt="Compose" />
  <img src="https://img.shields.io/badge/Min%20SDK-29-brightgreen" alt="Min SDK" />
  <img src="https://img.shields.io/badge/Target%20SDK-36-blue" alt="Target SDK" />
  <img src="https://img.shields.io/badge/License-Proprietary-red" alt="License" />
</p>

---

## 📖 Overview

**Travio** is a feature-rich Android travel companion application built entirely with **Jetpack Compose** and **Material 3**. It helps travelers discover destinations, search and book flights & hotels, plan AI-generated trip itineraries, share travel moments with a community, and manage favorites — all from a single, beautifully designed mobile experience.

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🏠 **Home Dashboard** | Personalized feed with destination recommendations, nearby hotels, and flight deals |
| 🤖 **AI Trip Planner** | Conversational AI chat that generates full trip itineraries with day-by-day plans |
| ✈️ **Flight Search & Booking** | Search flights, view detailed offer breakdowns, and book with integrated payment |
| 🏨 **Hotel Search & Booking** | Search hotels by destination/dates, view details & reviews, check room availability, and book with Stripe checkout |
| 🗺️ **Destination Explorer** | Rich destination detail pages with nearby attractions, map integration, and related destinations |
| ❤️ **Favorites** | Save and manage favorite destinations, trips, and hotels |
| 👥 **Community** | Social feed where users share travel moments with photos, locations, likes, and comments |
| 👤 **Profile Management** | View and edit user profiles with photo upload |
| 📝 **Travel Preference Survey** | Onboarding survey to personalize recommendations |
| 🔐 **Authentication** | Full auth flow with email/password, Google Sign-In, email verification, and password reset |
| 💳 **Stripe Payments** | Secure payment processing for flight and hotel bookings |
| 🌐 **Real-Time Sync** | SignalR-based real-time updates for trip generation and data synchronization |

---

## 🏗️ Architecture

Travio follows **Clean Architecture** with a strict **multi-module** structure, enforcing clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                          :app                               │
│          (Navigation, DI wiring, Application class)         │
├──────────────────────┬──────────────────────────────────────┤
│                      │                                      │
│   ┌──────────────────▼─────────────────────────────┐       │
│   │              :feature:*                         │       │
│   │  (UI Screens, ViewModels, Feature-local logic)  │       │
│   └──────────────────┬─────────────────────────────┘       │
│                      │                                      │
│   ┌──────────────────▼─────────────────────────────┐       │
│   │              :domain                            │       │
│   │    (Use Cases, Repository Interfaces, Models)   │       │
│   └──────────────────┬─────────────────────────────┘       │
│                      │                                      │
│   ┌──────────────────▼─────────────────────────────┐       │
│   │               :data                             │       │
│   │  (Repository Impls, API Services, Data Sources) │       │
│   └──────────────────┬─────────────────────────────┘       │
│                      │                                      │
│   ┌──────────────────▼─────────────────────────────┐       │
│   │              :core:*                            │       │
│   │  (network, designsystem, common, database)      │       │
│   └────────────────────────────────────────────────┘       │
└─────────────────────────────────────────────────────────────┘
```

### Module Dependency Rules

| Dependency | Allowed |
|------------|---------|
| `:app` → `:feature:*`, `:core:*` | ✅ |
| `:feature:*` → `:core:*` | ✅ |
| `:feature:*` → `:feature:*` | ❌ Forbidden |
| `:core:*` → `:feature:*` | ❌ Forbidden |
| `:core:common` → (no project deps) | ✅ Leaf module |

---

## 📁 Project Structure

```
Travio/
├── app/                          # Application module (entry point, navigation, DI)
├── data/                         # Data layer (API services, repositories, data sources)
├── domain/                       # Domain layer (use cases, models, repository interfaces)
├── core/
│   ├── common/                   # Shared utilities, navigation routes, UiText
│   ├── designsystem/             # Theme, typography, color tokens, shared composables
│   ├── network/                  # Retrofit setup, interceptors, API configuration
│   └── database/                 # Room database, DAOs, entities
├── feature/
│   ├── ai/                       # AI trip planning feature
│   ├── auth/                     # Authentication (login, signup, password reset, verification)
│   ├── booking/                  # Flight booking & payment
│   ├── chat/                     # AI chat, trip generation, trip details
│   ├── community/                # Social feed, posts, location picker
│   ├── destination/              # Destination detail & exploration
│   ├── favorite/                 # Favorites management
│   ├── home/                     # Home dashboard
│   ├── hotel/                    # Hotel search, details, checkout
│   ├── onboarding/               # Onboarding flow & language selection
│   ├── profile/                  # User profile & editing
│   ├── search/                   # Flight search & results
│   ├── survey/                   # Travel preference survey
│   └── utils/                    # Shared feature utilities
├── fastlane/                     # Fastlane configuration for CI/CD distribution
├── .github/workflows/            # GitHub Actions CI pipelines
├── gradle/libs.versions.toml     # Gradle version catalog
└── specs/                        # Feature specifications
```

---

## 🛠️ Tech Stack

### Core

| Technology | Version | Purpose |
|------------|---------|---------|
| **Kotlin** | 2.0.21 | Primary language |
| **Jetpack Compose** | BOM 2024.09.00 | Declarative UI framework |
| **Material 3** | Latest | Design system & theming |
| **Kotlin Coroutines** | 1.9.0 | Asynchronous programming |
| **Kotlin Serialization** | 1.7.3 | JSON serialization for type-safe navigation |

### Architecture & DI

| Technology | Purpose |
|------------|---------|
| **Hilt** (2.52) | Dependency injection |
| **Navigation Compose** (2.9.5) | Type-safe navigation with `@Serializable` routes |
| **Lifecycle** (2.9.4) | ViewModel, lifecycle-aware components |
| **DataStore** | Preferences & settings persistence |
| **Room** (2.8.4) | Local database |

### Networking & Backend

| Technology | Purpose |
|------------|---------|
| **Retrofit** (2.11.0) | REST API client |
| **OkHttp** (4.12.0) | HTTP client & interceptors |
| **SignalR** (8.0.0) | Real-time communication (trip sync) |
| **Gson** | JSON parsing |

### Authentication & Security

| Technology | Purpose |
|------------|---------|
| **Google Credential Manager** | Google Sign-In |
| **JWT Decode** (2.0.2) | Token decoding & validation |
| **Tink** (1.18.0) | Cryptographic operations |
| **Secrets Gradle Plugin** | Secure secrets management |

### Payments

| Technology | Purpose |
|------------|---------|
| **Stripe SDK** (21.3.0) | Payment processing |

### UI & Media

| Technology | Purpose |
|------------|---------|
| **Coil** (2.7.0) | Image loading (with SVG support) |
| **Google Maps Compose** (6.4.1) | Map integration |
| **Accompanist Permissions** | Runtime permissions |
| **Splash Screen API** | Animated splash screen |

### Code Quality

| Technology | Purpose |
|------------|---------|
| **Spotless** + **ktlint** | Code formatting |
| **Timber** (5.0.1) | Logging (debug-only `DebugTree`) |

### Testing

| Technology | Purpose |
|------------|---------|
| **JUnit 4** | Unit testing |
| **Mockito** + **Mockito-Kotlin** | Mocking framework |
| **Compose UI Test** | Compose integration testing |
| **Espresso** | UI testing |

### CI/CD

| Technology | Purpose |
|------------|---------|
| **GitHub Actions** | PR checks, build validation |
| **Fastlane** | Build automation & Firebase App Distribution |
| **Firebase App Distribution** | Beta & tester builds |

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Ladybug (2024.2+) or later
- **JDK 17** (for Gradle builds)
- **Android SDK** with API 36 installed
- **Ruby** (for Fastlane, optional)

### Clone

```bash
git clone https://github.com/muhamedmuhsen/Travio.git
cd Travio
```

### Environment Configuration

Travio uses a layered environment configuration system. Create the following files:

#### 1. `local.properties`

```properties
sdk.dir=/path/to/your/android/sdk
GOOGLE_WEB_CLIENT_ID=your_google_web_client_id
```

#### 2. `app/config/environment.secrets.properties` (gitignored)

```properties
STRIPE_PUBLISHABLE_KEY=pk_test_your_stripe_key
EMULATOR_BASE_URL=http://10.0.2.2:5116/api/
EMULATOR_IMAGE_BASE_URL=http://10.0.2.2:5116
TESTER_DEVICE_BASE_URL=http://your-tester-server:5116/api/
TESTER_DEVICE_IMAGE_BASE_URL=http://your-tester-server:5116
PRODUCTION_BASE_URL=https://your-production-server/api/
PRODUCTION_IMAGE_BASE_URL=https://your-production-server
```

#### 3. Google Maps API Key

Add your Maps API key via the Secrets Gradle Plugin in `local.properties`:

```properties
MAPS_API_KEY=your_google_maps_api_key
```

### Build & Run

```bash
# Debug build for emulator
./gradlew assembleEmulatorDebug

# Install on connected emulator
./gradlew installEmulatorDebug

# Tester device release
./gradlew assembleDeviceTesterRelease

# Production release
./gradlew assembleProductionRelease
```

---

## 🔧 Build Variants

Travio uses **product flavors** to manage different environments:

| Flavor | Build Type | Use Case |
|--------|-----------|----------|
| `emulatorDebug` | Debug | Local development on emulator |
| `deviceTesterRelease` | Release | QA testing on physical devices |
| `productionRelease` | Release | Production distribution |

Only **canonical** variant combinations are enabled — non-canonical combinations (e.g., `emulatorRelease`) are automatically disabled.

---

## 🧪 Testing

```bash
# Run all unit tests
./gradlew test

# Run specific module tests
./gradlew :domain:test
./gradlew :data:test

# Run Compose UI tests
./gradlew connectedAndroidTest

# Check code formatting
./gradlew spotlessCheck

# Auto-fix formatting
./gradlew spotlessApply
```

### Testing Conventions

- Test naming: `given_when_then` or `should_when` pattern
- Coroutines: `runTest` + `TestDispatcher` (never `runBlocking`)
- Prefer **fakes** over mocks for repository boundaries
- All ViewModels and Use Cases must have unit tests

---

## 🔄 CI/CD

### GitHub Actions

- **PR Checks** (`pr_checks.yml`): Runs on every PR to `master`
  - Spotless code format check
  - Debug build compilation
  - Tester device release artifact build
  - Release endpoint validation

- **Auto Branch Cleanup** (`delete-branch-on-merge.yml`): Deletes feature branches after PR merge

### Fastlane

```bash
# Distribute beta build via Firebase App Distribution
bundle exec fastlane distribute_beta

# Distribute tester device build
bundle exec fastlane distribute_tester_device
```

---

## 📐 Code Style

- **Formatter**: Spotless with ktlint (auto-applied on every Kotlin compile)
- **Max line length**: 140 characters
- **Trailing commas**: Disabled
- **Function signatures**: Force multiline when ≥2 parameters
- **Compose naming**: `@Composable` functions exempt from standard function naming rules

---

## 🤝 Contributing

1. Create a feature branch from `master`:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. Follow the [engineering rules](.github/copilot-instructions.md) — key principles:
   - Clean Architecture (Data → Domain → Presentation)
   - Unidirectional Data Flow with `StateFlow`
   - Sealed interfaces for UI state (`Loading`, `Success`, `Error`)
   - All strings in `strings.xml` via `UiText`
   - MaterialTheme tokens only — no hardcoded colors/spacing
   - Every public Composable accepts `modifier: Modifier = Modifier`

3. Ensure your code passes:
   ```bash
   ./gradlew spotlessCheck
   ./gradlew assembleEmulatorDebug
   ./gradlew test
   ```

4. Open a PR to `master` with:
   - Conventional commit message (e.g., `feat(hotel): add room availability dialog`)
   - Description of what changed, why, and how to test

---

## 📄 License

This project is proprietary. All rights reserved.

---

<p align="center">
  Built with ❤️ using Jetpack Compose & Kotlin
</p>
