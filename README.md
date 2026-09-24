# Family Locator

A cross-platform Family Locator application built with Kotlin Multiplatform (KMP) and Compose Multiplatform (CMP) for Android and iOS.

The app allows family members and friends to connect, share their locations in real time, and view each other's live locations on Google Maps.

## Features

* Real-time location sharing between family members and friends
* Live location updates using WebSockets
* Google Maps integration for displaying live locations
* Background location services on Android and iOS
* Location updates continue when the app is running in the background or closed, subject to platform restrictions and permissions
* QR code generation for connecting users
* Native QR code scanning on Android and iOS
* Family/group-based location sharing
* Ktor-based REST APIs
* MongoDB backend
* Cross-platform UI using Compose Multiplatform
* Native platform implementations where required

## Tech Stack

### Mobile

* Kotlin Multiplatform
* Compose Multiplatform
* Kotlin Coroutines & Flow
* Koin for Dependency Injection
* Clean Architecture
* Ktor Client
* Google Maps
* Android Location Services
* iOS Location Services
* Native QR generation and scanning

### Backend

* Ktor Server
* Kotlin
* MongoDB
* WebSockets
* REST APIs

## Architecture

The project follows Clean Architecture with a shared codebase wherever possible.

```text
                ┌──────────────────────┐
                │   Compose Multiplatform │
                │          UI          │
                └──────────┬───────────┘
                           │
                ┌──────────▼───────────┐
                │      ViewModels       │
                │   State / Events      │
                └──────────┬───────────┘
                           │
                ┌──────────▼───────────┐
                │       Domain         │
                │  Business Logic      │
                └──────────┬───────────┘
                           │
                ┌──────────▼───────────┐
                │        Data          │
                │ API / WebSocket / DB │
                └──────────┬───────────┘
                           │
              ┌────────────▼────────────┐
              │     Ktor Backend        │
              │                          │
              │   REST + WebSockets      │
              └────────────┬─────────────┘
                           │
                    ┌──────▼──────┐
                    │   MongoDB   │
                    └─────────────┘
```

Platform-specific functionality such as location services, QR scanning, and other native capabilities is implemented separately for Android and iOS while keeping the core application logic shared.

## Project Structure

```text
FamilyLocator/
│
├── androidApp/
│   └── Android application
│
├── iosApp/
│   └── iOS application
│
├── shared/
│   └── src/
│       ├── commonMain/
│       │   └── Shared Android/iOS code
│       │
│       ├── androidMain/
│       │   └── Android-specific implementations
│       │
│       └── iosMain/
│           └── iOS-specific implementations
│
└── server/
    └── Ktor backend
```

## Real-Time Location Sharing

The application uses WebSockets for real-time communication.

The basic flow is:

```text
User A
   │
   │ Location
   ▼
KMP Mobile App
   │
   │ WebSocket
   ▼
Ktor Server
   │
   │ WebSocket
   ▼
KMP Mobile App
   │
   ▼
User B
```

When a user's location changes, the mobile application sends the updated coordinates through the WebSocket connection. The Ktor server distributes the location information to the relevant connected family members.

Google Maps is used on the client side to display the received locations.

## Backend

The backend is built using Ktor and MongoDB.

It provides:

* REST APIs
* WebSocket connections
* User/group management
* Real-time location communication
* MongoDB data persistence

The backend can be deployed independently from the mobile applications.

## Android & iOS

Most of the application logic and UI is shared through Kotlin Multiplatform and Compose Multiplatform.

Native implementations are used where platform-specific APIs are required.

### Android

Android-specific functionality includes:

* Background/foreground location services
* Native QR scanning
* Native QR generation
* Android permissions
* Google Maps integration

### iOS

iOS-specific functionality includes:

* Core Location / background location services
* Native QR scanning
* Native QR generation
* iOS permissions
* iOS-specific platform integrations

## Getting Started

### Requirements

* Android Studio
* Xcode
* JDK 21+
* Kotlin Multiplatform environment
* MongoDB
* Gradle

### Android

Run the Android application using Android Studio or:

```bash
./gradlew :androidApp:assembleDebug
```

### iOS

Open:

```text
iosApp/
```

in Xcode and run the application on an iOS simulator or physical device.

## Backend Configuration

The Ktor backend uses environment variables for configuration.

Example:

```text
MONGO_DB_CONNECT_STRING=your_mongodb_connection_string
```

The server listens on the port provided by the environment, with `8080` used as the local default.

Example:

```yaml
ktor:
  application:
    modules:
      - com.example.ApplicationKt.module

  deployment:
    port: ${PORT:8080}
    host: 0.0.0.0
```

## Location Permissions

Because the application provides background location sharing, users must grant the required location permissions.

The exact permission flow differs between Android and iOS due to platform-specific privacy and background execution requirements.

## Development

Clone the repository:

```bash
git clone <repository-url>
```

Open the project in Android Studio.

For iOS development, open the `iosApp` project in Xcode.

Configure the required MongoDB connection string and API/WebSocket URLs before running the application.

## Testing

Run Android tests:

```bash
./gradlew :shared:testAndroidHostTest
```

Run iOS simulator tests:

```bash
./gradlew :shared:iosSimulatorArm64Test
```

## Technologies

```text
Kotlin
Kotlin Multiplatform
Compose Multiplatform
Ktor
MongoDB
WebSockets
Ktor Client
Koin
Coroutines
Flow
Clean Architecture
Google Maps
Android
iOS
```

## Purpose

This project was built to explore a production-style cross-platform architecture using Kotlin Multiplatform while still taking advantage of native Android and iOS capabilities where required.

The main focus is real-time location sharing, background location services, cross-platform UI, native platform integrations, and a Ktor + MongoDB backend.
