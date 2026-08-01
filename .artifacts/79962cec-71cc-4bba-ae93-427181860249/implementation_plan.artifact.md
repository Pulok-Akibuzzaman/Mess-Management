# Implementation Plan - Member List with Search

This plan outlines the steps to add a searchable member list to the `MemberActivity`, matching the provided UI design.

## Proposed Changes

### 1. Data Model
- **[NEW] [Member.java](file:///Users/fayazaislam/Documents/AndroidStudioProjects/489Project/Mess-Management/app/src/main/java/com/project/messmanagement/Member.java)**:
    - Create a data class to represent a member with fields: `name`, `initials`, `roomNumber`, `phone`, `meals`, `dueAmount`, `status` (Active/Away).

### 2. Resources (Drawables & Colors)
- **[MODIFY] [colors.xml](file:///Users/fayazaislam/Documents/AndroidStudioProjects/489Project/Mess-Management/app/src/main/res/values/colors.xml)**:
    - Add colors for status badges (`status_active_bg`, `status_active_text`, `status_away_bg`, `status_away_text`).
- **[NEW] [search_background.xml](file:///Users/fayazaislam/Documents/AndroidStudioProjects/489Project/Mess-Management/app/src/main/res/drawable/search_background.xml)**:
    - Rounded background for the search bar.
- **[NEW] [status_badge_active.xml](file:///Users/fayazaislam/Documents/AndroidStudioProjects/489Project/Mess-Management/app/src/main/res/drawable/status_badge_active.xml)**:
    - Rounded background for the "Active" status.
- **[NEW] [status_badge_away.xml](file:///Users/fayazaislam/Documents/AndroidStudioProjects/489Project/Mess-Management/app/src/main/res/drawable/status_badge_away.xml)**:
    - Rounded background for the "Away" status.
- **[NEW] [ic_search.xml](file:///Users/fayazaislam/Documents/AndroidStudioProjects/489Project/Mess-Management/app/src/main/res/drawable/ic_search.xml)**:
    - Search icon vector.

### 3. Layouts
- **[NEW] [item_member.xml](file:///Users/fayazaislam/Documents/AndroidStudioProjects/489Project/Mess-Management/app/src/main/res/layout/item_member.xml)**:
    - Design the horizontal card for each member, including the initials circle, name, status badge, room/phone details, and meal/due stats.
- **[MODIFY] [activity_member.xml](file:///Users/fayazaislam/Documents/AndroidStudioProjects/489Project/Mess-Management/app/src/main/res/layout/activity_member.xml)**:
    - Add a search `EditText` (using `search_background.xml`).
    - Replace the `ScrollView` with a `RecyclerView` for the member list.
    - Ensure the layout matches the visual hierarchy in the screenshot.

### 4. Adapter & Activity Logic
- **[NEW] [MemberAdapter.java](file:///Users/fayazaislam/Documents/AndroidStudioProjects/489Project/Mess-Management/app/src/main/java/com/project/messmanagement/MemberAdapter.java)**:
    - Implement the adapter to bind `Member` data to `item_member.xml`.
    - Add filtering logic for search.
- **[MODIFY] [MemberActivity.java](file:///Users/fayazaislam/Documents/AndroidStudioProjects/489Project/Mess-Management/app/src/main/java/com/project/messmanagement/MemberActivity.java)**:
    - Initialize the `RecyclerView` and `MemberAdapter`.
    - Populate with sample data (as shown in the image).
    - Implement `TextWatcher` on the search field to filter the list.
- **[MODIFY] [MainActivity.java](file:///Users/fayazaislam/Documents/AndroidStudioProjects/489Project/Mess-Management/app/src/main/java/com/project/messmanagement/MainActivity.java)**:
    - Handle the click on the "Members" feature to navigate to `MemberActivity`.

## Verification Plan
### Manual Verification
- Deploy the app and navigate to the Members screen.
- Verify the list matches the UI design.
- Test the search functionality to ensure it filters members by name.
- Verify that the bottom navigation works (if applicable).
