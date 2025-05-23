# SafetyAndCheap Android Application

## Overview

**SafetyAndCheap** is an Android application designed for buying and renting housing, prioritizing safety and affordability. The app leverages modern Android development practices, using **Jetpack Compose** for the UI, **Hilt** for dependency injection, **Firebase** for storage and Firestore database, and **Retrofit** for network requests to a local backend server hosted in the **SafetyAndCheap-back** repository. The app supports features like property browsing, user authentication, and secure data handling.

## Features

- Browse and search for properties available for sale or rent.
- Secure user authentication and data storage using Firebase Authentication and Firestore.
- Image storage and retrieval with Firebase Storage.
- Network communication with a local backend server using Retrofit and Gson.
- Phone number validation using `libphonenumber`.
- Modern, declarative UI built with Jetpack Compose.
- Dependency injection with Hilt for modular and testable code.
- Secure storage of sensitive data using Jetpack Security Crypto.
- Navigation handled with Jetpack Navigation Compose.

## Tech Stack

- **Language**: Kotlin (JVM Target: 21)
- **UI Framework**: Jetpack Compose
- **Dependency Injection**: Hilt
- **Networking**: Retrofit, OkHttp, Gson
- **Backend Services**: Firebase (Storage, Firestore)
- **Data Storage**: Jetpack DataStore
- **Image Loading**: Coil
- **Testing**: JUnit, Espresso, Compose Testing
- **Build Tool**: Gradle (Kotlin DSL)
- **Minimum SDK**: 24
- **Target SDK**: 34
- **Compile SDK**: 35

## Prerequisites

To build and run the **SafetyAndCheap** app, ensure you have the following installed:

- **Android Studio**: Latest stable version (recommended: Koala or later).
- **JDK**: Version 21.
- **Git**: To clone the repository.
- **Firebase Account**: For Firebase services configuration.
- **Local Backend Server**: The backend server from the **SafetyAndCheap-back** repository must be running locally.

## Setup Instructions

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/<your-username>/SafetyAndCheap.git
   cd SafetyAndCheap
   ```

2. **Set Up the Backend**:
    - Clone the backend repository:
      ```bash
      git clone https://github.com/<your-username>/SafetyAndCheap-back.git
      ```
    - Follow the setup instructions in the **SafetyAndCheap-back** repository to start the local server.
    - Ensure the backend server is running and accessible (e.g., `http://localhost:8080`).

3. **Configure Firebase**:
    - Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com).
    - Add an Android app to your Firebase project, using the package name `com.example.safetyandcheap`.
    - Download the `google-services.json` file and place it in the `app/` directory.
    - Enable Firebase Authentication, Firestore, and Storage in the Firebase Console.

4. **API Keys and Secrets**:
    - Use the **Secrets Gradle Plugin** to manage sensitive keys (e.g., API keys for Google Maps or other services).
    - Add your API keys to `local.properties` or use a secure method as per the plugin's documentation.

5. **Build the Project**:
    - Open the project in Android Studio.
    - Sync the project with Gradle.
    - Build the app using `Build > Make Project`.

6. **Run the App**:
    - Connect an Android device (API 24 or higher) or use an emulator.
    - Run the app via Android Studio’s `Run` button.

## Project Structure

```
SafetyAndCheap/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/safetyandcheap/
│   │   │   │   ├── data/              # Data layer (Repositories, DataStore, Retrofit APIs)
│   │   │   │   ├── di/                # Hilt dependency injection modules
│   │   │   │   ├── model/             # Data models
│   │   │   │   ├── ui/                # Jetpack Compose UI components and screens
│   │   │   │   ├── util/              # Utility classes
│   │   │   │   ├── viewmodel/         # ViewModels for UI logic
│   │   │   ├── res/                   # Resources (drawables, strings, etc.)
│   │   │   ├── AndroidManifest.xml
│   │   ├── build.gradle.kts           # App module build script
├── gradle/
│   ├── libs.versions.toml             # Dependency versions
├── build.gradle.kts                   # Project-level build script
├── settings.gradle.kts                # Gradle settings
```

## Dependencies

Key dependencies used in the project (defined in `app/build.gradle.kts` and `gradle/libs.versions.toml`):

- **AndroidX**: Core KTX, Lifecycle, Activity Compose, Navigation Compose, DataStore.
- **Jetpack Compose**: UI, Material3, Tooling, LiveData integration.
- **Hilt**: Dependency injection (Hilt Android, Hilt Navigation Compose).
- **Retrofit & OkHttp**: For API calls to the local backend server.
- **Firebase**: Storage, Firestore, and BOM for dependency management.
- **Coil**: Image loading for Compose.
- **Libphonenumber**: Phone number validation.
- **Testing**: JUnit, Espresso, Compose Testing.

## Build Configuration

- **Compile SDK**: 35
- **Minimum SDK**: 24
- **Target SDK**: 34
- **Kotlin Compiler**: 1.5.12 (for Jetpack Compose)
- **Java Version**: 21
- **Build Features**: Jetpack Compose, BuildConfig
- **ProGuard**: Disabled for release builds (configurable in `proguard-rules.pro`).

## Testing

The project includes unit and instrumentation tests:

- **Unit Tests**: Run with JUnit (`testImplementation`).
- **Instrumentation Tests**: Run with Espresso and Compose Testing (`androidTestImplementation`).
- To execute tests:
  ```bash
  ./gradlew test
  ./gradlew connectedAndroidTest
  ```

## Contributing

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m "Add your feature"`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Create a Pull Request.

## Contact

For issues or feature requests, please open an issue on the [GitHub repository](https://github.com/<your-username>/SafetyAndCheap).