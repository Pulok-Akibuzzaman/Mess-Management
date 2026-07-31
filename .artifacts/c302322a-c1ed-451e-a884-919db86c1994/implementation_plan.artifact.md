# Material Design UI Refresh Plan

This plan outlines a comprehensive update to all MessMate app screens to fully leverage **Material Design 3 (M3)** components, ensuring a modern, standardized, and polished user experience.

## User Review Required

> [!WARNING]
> I will be updating the base theme to `Theme.Material3.DayNight.NoActionBar`. This may subtly alter the appearance of standard system widgets (like checkboxes, text fields, and ripples) across the entire app.

> [!IMPORTANT]
> I will replace custom headers with `MaterialToolbar` and standard `View`-based buttons with `MaterialButton` or `ExtendedFloatingActionButton` where appropriate.

## Proposed Changes

### Theme & Styling

#### [MODIFY] [themes.xml](file:///F:/EWU/CSE489/MessManagement/app/src/main/res/values/themes.xml)
- Change parent to `Theme.Material3.DayNight.NoActionBar`.
- Map MessMate colors (`primary_bg`, `nav_active`) to Material attributes like `colorPrimary`, `colorSurface`, etc.

### Layouts Refresh

#### [MODIFY] [activity_login.xml](file:///F:/EWU/CSE489/MessManagement/app/src/main/res/layout/activity_login.xml) & [activity_signup.xml](file:///F:/EWU/CSE489/MessManagement/app/src/main/res/layout/activity_signup.xml)
- Wrap `EditText` in `TextInputLayout` for Material 3 text field styling (floating labels, error states).
- Use `MaterialButton` for "Sign In" and "Continue".

#### [MODIFY] [activity_bazar.xml](file:///F:/EWU/CSE489/MessManagement/app/src/main/res/layout/activity_bazar.xml)
- Replace custom header with `MaterialToolbar`.
- Use `MaterialCardView` for the Total Bazar summary.
- Replace the custom add button with a standard `FloatingActionButton`.

#### [MODIFY] [activity_cash_ledger.xml](file:///F:/EWU/CSE489/MessManagement/app/src/main/res/layout/activity_cash_ledger.xml)
- Use `MaterialToolbar` for the header.
- Ensure all items in the `RecyclerView` use `MaterialCardView` (already partially done, will standardize).

#### [MODIFY] [activity_meal_routine.xml](file:///F:/EWU/CSE489/MessManagement/app/src/main/res/layout/activity_meal_routine.xml)
- Replace custom alert banner with a `MaterialCardView` or `MaterialBanner` style.
- Use `MaterialToolbar` with the month selector as a menu item or a themed `Button`.
- Use `MaterialDivider` for the bottom navigation separation.

#### [MODIFY] [activity_all_features.xml](file:///F:/EWU/CSE489/MessManagement/app/src/main/res/layout/activity_all_features.xml)
- Use `MaterialToolbar` for the header.
- Use `MaterialButton` for the "Sign Out" button (already done, will refine styling).

## Verification Plan

### Manual Verification
- Review each screen in the Android Studio layout editor to ensure the Material 3 components render correctly.
- Verify that color mapping in `themes.xml` applies consistently across all activities.
- Test ripple effects on buttons and cards to ensure standard Material interaction feedback.
