# TaskMaster Android

TaskMaster Android is a mobile application built with Kotlin and Jetpack Compose for project and task management. It provides authentication, project administration, task tracking, calendar views, notifications, user profiles, and statistics for projects and team members.

## Features

- User sign in and registration
- Project creation and management
- Task creation, editing, assignment, and filtering
- Calendar-based task visualization
- Notifications view
- User profile management
- Project and member statistics

## Tech Stack

- Kotlin
- Jetpack Compose
- Navigation Compose
- Retrofit
- Gson
- OkHttp
- Coil
- Gradle Kotlin DSL

## Project Structure

```text
app/
  src/main/java/com/example/taskmaster/   # Application source code
  src/main/res/                           # Drawables, fonts, themes, and other resources
gradle/                                   # Gradle version catalog and wrapper configuration
```

## Requirements

- Android Studio
- JDK 11 or higher
- Android SDK configured with `compileSdk 36`
- A running TaskMaster backend

## Getting Started

1. Clone or download this repository.
2. Open the project in Android Studio.
3. Let Gradle sync all dependencies.
4. Run the app on an Android emulator or a physical device.

## Build

From the project root:

```powershell
.\gradlew.bat assembleDebug
```

## Backend Configuration

The backend is deployed on Render and the app currently uses this base URL:

```text
https://backend-taskmaster-1.onrender.com/
```

API documentation is available at:

```text
https://backend-taskmaster-1.onrender.com/swagger-ui/index.html#/
```

This value is defined in:

```text
app/src/main/java/com/example/taskmaster/viewmodel/data/net/RetrofitProvider.kt
```

If you want to run the app against a local backend, update `BASE_URL` with an address reachable from Android:

- Android Emulator: `http://10.0.2.2:8080/`
- Physical Device: `http://<your-local-ip>:8080/`

Example:

```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/"
```

## Permissions

The application requests:

- `android.permission.INTERNET`
- `android.permission.ACCESS_NETWORK_STATE`

## Notes

- Networking is handled through Retrofit and OkHttp.
- Images are loaded with Coil.
- Session-related data is currently stored locally with `SharedPreferences`.
- The project builds successfully with `assembleDebug`.
