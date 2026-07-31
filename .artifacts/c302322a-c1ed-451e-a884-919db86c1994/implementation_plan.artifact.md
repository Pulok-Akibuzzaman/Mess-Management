# Signup Activity Implementation Plan

This plan outlines the creation of the `SignupActivity.java` file and the necessary updates to the layout and navigation.

## User Review Required

> [!IMPORTANT]
> I will be adding IDs to existing UI elements in `activity_signup.xml` to reference them in code.
> I will also add a navigation link from the Login screen to the Signup screen.

## Proposed Changes

### Resources

#### [MODIFY] [strings.xml](file:///F:/EWU/CSE489/MessManagement/app/src/main/res/values/strings.xml)
- Add `dont_have_account` and `sign_up_link` strings.

### Layouts

#### [MODIFY] [activity_signup.xml](file:///F:/EWU/CSE489/MessManagement/app/src/main/res/layout/activity_signup.xml)
- Add IDs to `EditText` fields: `et_full_name`, `et_email`, `et_phone`.
- Add ID to the Sign In link: `tv_signin_link`.

#### [MODIFY] [activity_login.xml](file:///F:/EWU/CSE489/MessManagement/app/src/main/res/layout/activity_login.xml)
- Add a "Don't have an account? Sign Up" link container at the bottom, above the footer.

### Code

#### [NEW] [SignupActivity.java](file:///F:/EWU/CSE489/MessManagement/app/src/main/java/com/project/messmanagement/SignupActivity.java)
- Initialize UI components.
- Implement "Continue" button logic (basic validation and Toast).
- Implement "Sign In" link logic (navigate back to `LoginActivity`).

#### [MODIFY] [LoginActivity.java](file:///F:/EWU/CSE489/MessManagement/app/src/main/java/com/project/messmanagement/LoginActivity.java)
- Add logic to handle the new "Sign Up" link to navigate to `SignupActivity`.

### Manifest

#### [MODIFY] [AndroidManifest.xml](file:///F:/EWU/CSE489/MessManagement/app/src/main/AndroidManifest.xml)
- Register `SignupActivity`.

## Verification Plan

### Automated Tests
- Run `gradle build` to ensure no syntax errors.

### Manual Verification
- Deploy to device/emulator.
- Verify navigation between Login and Signup screens.
- Verify that clicking "Continue" in Signup shows a Toast or error message if fields are empty.
