# Implementation Plan - MessMate Login Activity (Java)

This plan outlines the steps to create a `LoginActivity` in **Java** that connects to the previously created `activity_login.xml` layout.

## User Review Required

> [!IMPORTANT]
> - I will create `LoginActivity.java` in the same package as `MainActivity.kt`.
> - I will set `LoginActivity` as the new **Launcher Activity**.
> - I will use standard View binding (findViewById) to handle the UI interactions.

## Proposed Changes

### Source Code

#### [NEW] [LoginActivity.java](file:///F:/EWU/CSE489/MessManagement2/app/src/main/java/com/project/messmanagement/LoginActivity.java)
- Create a new Java class extending `AppCompatActivity`.
- Link the `activity_login.xml` layout.
- Implement basic button click listeners for role selection and sign-in.

### Configuration

#### [MODIFY] [AndroidManifest.xml](file:///F:/EWU/CSE489/MessManagement2/app/src/main/AndroidManifest.xml)
- Register `LoginActivity`.
- Move the launcher intent filter to `LoginActivity`.
