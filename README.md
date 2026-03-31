# UniFlow

UniFlow is an Android task management application built with Kotlin and Gradle.

## Features
- User registration and login
- Create and manage personal tasks
- Settings, help, and about screens
- Local data persistence via `DatabaseHelper`

## Project Structure
- `app/src/main/java/com/example/uniflow/` — Kotlin source files
- `app/src/main/res/` — Android resources (layouts, strings, drawables)
- `app/src/androidTest/` — Instrumented Android tests
- `app/src/test/` — Local unit tests

## Build
Use the Gradle wrapper to build the app:

```bash
./gradlew assembleDebug
```

## Test
Run local unit tests:

```bash
./gradlew testDebugUnitTest
```
