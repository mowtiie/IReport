# CLAUDE.md

Project: IReport (`com.mowtiie.ireport`), a community issue reporting app for residents to report damaged roads, overflowing trash bins and broken streetlights.

Core flow: Resident reports issue -> Admin reviews and assigns -> Field Worker fixes and uploads "After" photo -> Resident sees status update.

This is a simulation. There is no backend, no payment gateway, no email/SMS, no real push notifications. Everything must look and behave correctly on screen during a demo. All app data is stored in local SQLite so it persists across launches.

Source of requirements: the CodeFest Final Activity spec (Community Issue Reporting App). Read it fully before building. Where the spec is vague, pick the simplest simulated behavior and note it here.

Status: build order steps 1 to 4 (auth, Resident, Admin and Field Worker modules) done.

Notes from step 4: picking an After photo on Task Detail saves it immediately, so Resolved only checks the stored `resolution_media`. In Progress and Resolved buttons disable once they no longer apply. The worker list shows unresolved tasks first.

Notes from step 3: the dashboard counts cover all tickets, not the filtered list. The status dropdown on Admin Ticket saves as soon as an option is picked. Assigning needs a department, a worker, or both. `Data.changeTicketStatus` is the one place that updates status and writes the owner notification, so the Worker module should reuse it. Last verified build: `assembleDebug` succeeded, not yet run on an emulator.

Notes from step 2: "Nearby" means the resident's home barangay, taken as the barangay of most of their own tickets (falls back to the first barangay), because `users` has no barangay column. Report Issue also requires a title and both barangay and street. Sample media are vector drawables `sample_*` in `res/drawable`, kept from shrinking by `res/raw/keep.xml`. Notification rows and nearby tickets do not open from the feed; notifications open Ticket Detail.

Notes from step 1: the theme style is `Theme.IReport` (set in the manifest). A fourth seeded account, `neighbor@demo.com` (Maria Santos, resident, password `demo1234`), owns the other-resident tickets so Community Feed is not empty. Seed media names (`sample_*`) are vector drawables in `res/drawable`, kept from shrinking by `res/raw/keep.xml`; real uploads are files in `filesDir/media`.

## Prime directive: build the fastest way possible

Speed beats elegance. Pick the shortest path that works in a live demo.

- No structures. No architecture patterns (MVVM, MVP, repositories, use cases, DI), no layers, no feature packages, no interfaces or abstract classes, no custom model/POJO classes. Exception: a single shared `GenericAdapter<T>` with a nested `Binder` interface.
- One Activity per screen, one shared `Data.java` holding the constants, the `USER_*` fields and static helper methods that read and write the database.
- Read rows from a `Cursor` into `String[]` or `ArrayList<String>`. No custom model/POJO classes.
- Hardcode seed data and insert it once, when the database is first created. Never fetch or sync anything. All records live in the SQLite tables listed under Database and persist across launches. Only the logged-in user (`Data.USER_*`) lives in memory.
- Duplicate code instead of adding an abstraction layer, but keep each copy readable and clean (see Code quality).
- No tests, no logging frameworks, no error handling beyond field validation.
- Do not add libraries or touch build.gradle dependencies. Enabling `buildFeatures { viewBinding = true }` is allowed.

## Build order

Build in this order. The core flow must work end to end before anything is polished.

1. Auth: Login, Sign Up (role tabs), Password Reset, role-based home routing.
2. Resident: Home, Report Issue (category, description, urgency, location, photo/video attach), My Reports (status tracking and history), Ticket Detail, Community Feed (nearby issues, announcements, upvotes), Notifications, Profile.
3. Admin: Home with Ticket Dashboard (filter/sort), Ticket Detail with Assign, Status Updater and Notes.
4. Field Worker: Home with Assigned Tasks, Task Detail with Resolution Upload.
5. UI polish, then run the whole flow on an emulator and fix what turns up.

## Stack

- Java, XML layouts, AppCompat and Material 3, single app module. minSdk 24, targetSdk 36.
- No Fragments, no Navigation component, no Jetpack Compose. No permissions, no internet.
- Views via view binding. Use the generated `*Binding` classes, never `findViewById`.
- Lists: `RecyclerView` with the one shared `GenericAdapter<T>` and shared row layout `item_row.xml`. Never write a per-screen adapter.
- Database: pure SQLite (`SQLiteOpenHelper`, `ContentValues`, `Cursor`). No Room, no ORM, no DAO layer.
- Dropdowns: `TextInputLayout` (ExposedDropdownMenu style) with `MaterialAutoCompleteTextView`. Never `Spinner`.
- Messages: `Snackbar` for validation and errors on a screen that stays open. `Toast` only for simulated notifications and messages shown as a screen closes. `MaterialAlertDialogBuilder` (never plain `AlertDialog.Builder`) for dialogs, `BottomSheetDialog` for add/edit sheets.
- Media: no camera or storage permissions. "Photo/video upload" uses the system Photo Picker (`ActivityResultContracts.PickVisualMedia` with a `PickVisualMediaRequest`, built by `Data.imageRequest()` and `Data.imageAndVideoRequest()`). The picked file is copied into `filesDir/media` by `Data.saveMedia` (photos are downscaled and rotated to match their EXIF orientation, videos are copied as is), and only the file name is stored as text in the row. Show it with `Data.showMedia`, which also renders the seeded `sample_*` drawable names.
- Location: no GPS and no maps. Location is a text field plus a dropdown of hardcoded barangay/purok names.

## Code layout

Package `com.mowtiie.ireport`, three flat folders. Do not nest them or add per-feature packages.

- `activities/`: every Activity.
- `data/`: `Data.java` (constants, `USER_*`, database helper methods) and `DbHelper.java` (tables and seed).
- `adapters/`: `GenericAdapter.java`.

Navigation is `startActivity(new Intent(...))` with `putExtra`. After a flow finishes, start the final screen with `NEW_TASK | CLEAR_TASK`. Logout clears the task and returns to Login.

## Database

Every record the app creates or edits is a row in a SQLite table, written immediately and reloaded from the database in `onResume`.

`DbHelper` has a `DATABASE_VERSION` constant. `onCreate` creates every table and then calls `seedDemo`. `onUpgrade` drops all tables and calls `onCreate`. Bump the version whenever a table changes during development.

Tables:

- `users` (id, email, password, name, role, phone, notify_enabled)
- `tickets` (id, user_id, category, title, description, urgency, location, media, status, assigned_to, assigned_dept, created_at, upvotes, resolution_media)
- `ticket_notes` (id, ticket_id, author_id, note, created_at): internal notes by admin
- `upvotes` (id, ticket_id, user_id): one row per user per ticket
- `announcements` (id, title, body, created_at)
- `notifications` (id, user_id, ticket_id, message, created_at, is_read)

Enum-like values are stored as the text of their ALL_CAPS constants. Dates are stored as the display text from the picker.

Constants in `Data.java`:

- Roles: `ROLE_RESIDENT`, `ROLE_ADMIN`, `ROLE_WORKER`
- Status: `STATUS_SUBMITTED`, `STATUS_UNDER_REVIEW`, `STATUS_IN_PROGRESS`, `STATUS_RESOLVED` (display text: Submitted, Under Review, In Progress, Resolved)
- Category: `CAT_ROADS`, `CAT_SANITATION`, `CAT_LIGHTING` (display: Roads, Sanitation, Lighting)
- Urgency: `URGENCY_LOW`, `URGENCY_MEDIUM`, `URGENCY_HIGH`

## UI rules

- Material 3 components, not plain framework widgets: `MaterialToolbar`, `MaterialTextView`, `MaterialButton`, `MaterialCardView`, `MaterialDivider`, `TextInputLayout` with `TextInputEditText`, `MaterialSwitch`, `TabLayout`, `Chip` / `ChipGroup`, progress indicators.
- Every screen except Login has a `MaterialToolbar` with a title and a back arrow (content description set), except root screens.
- Toolbar actions use menu resources via `app:menu` and `setOnMenuItemClickListener` (no `setSupportActionBar`).
- Empty lists use a shared `view_empty.xml` through `<include>`.
- Theme: generated by Material Theme Builder (`themes.xml`, `colors.xml`, `theme_overlays.xml`). Do not hand-edit. Colors come only from `?attr/color*` and `?attr/textAppearance*`. No hex values or custom colors.
- Page background and toolbars `colorSurfaceContainer`, sticky bars `colorSurfaceContainerHigh`, brand accents `colorPrimary`, secondary actions tonal, one filled button per screen.
- Status is shown with a `Chip` styled by `?attr/color*` (for example Submitted: `colorSurfaceVariant`, Under Review: `colorSecondaryContainer`, In Progress: `colorTertiaryContainer`, Resolved: `colorPrimaryContainer`). Urgency is plain text next to the category, not a chip, because it is a static fact.
- Insets: `EdgeToEdge.enable(this)` right after `super.onCreate` in every Activity, `fitsSystemWindows="true"` on every layout root. Auth screens sit in a `ScrollView` with `fillViewport="true"`.
- Spacing: screen padding 16dp (24dp on auth), gaps 8dp between fields and cards, 16dp between sections. Lists use `clipToPadding="false"` with 8dp bottom padding.
- Dividers only above a sticky bottom bar or between distinct sections, never under a toolbar or between a heading and a list.
- Chips are for interactive items (filters, status). Static facts go in text.
- Destructive actions offer a Snackbar Undo.
- Accessibility: content descriptions on icons, 48dp touch targets, decorative emoji hidden from screen readers.

## Forms

- Every `TextInputLayout` has helper text with the format when it matters.
- Validate with inline `setError` on the wrong field, not a generic Snackbar. Clear the error on the next attempt.
- Password fields use the toggle end icon, 6+ characters. Emails use `Patterns.EMAIL_ADDRESS`. Mobile numbers: 11 digits starting with `09`.
- Report form requires category, description (10+ characters), urgency and location. Media is optional.

## Naming and style

- Constants `ALL_CAPS_WITH_UNDERSCORES`.
- View ID prefixes: `til`, `et`, `dd`, `tv`, `btn`, `rv`, `chip`, `sw`, `card`, `ll`.
- 4-space indent, `binding.xxx` access, lambdas for click listeners. Match surrounding code.

## Code quality

Fast does not mean messy. The code must be readable, maintainable and clean.

- Use clear, descriptive names for variables, methods, constants and view IDs. No single-letter names except loop indexes, no cryptic abbreviations.
- Keep methods short and single-purpose. Split a long `onCreate` into small private methods such as `setupToolbar()`, `loadTickets()` and `bindListeners()`.
- No magic strings or numbers. Use the constants in `Data.java`.
- No dead code, unused imports, unused variables or commented-out code. Remove them as you go.
- Keep formatting consistent: one statement per line, blank lines between methods, no deeply nested blocks (return early instead).
- Comments are minimal. Write one only when the reason behind the code is not obvious from reading it, and explain why, never what. Good names replace comments. No file headers, no section banners, no comments restating the code.

## Account and notifications defaults

- Sign up, login, logout, profile edit and password reset are fake but validated. Accounts live in the SQLite `users` table (with a `role` column), and the logged-in user is copied into `Data.USER_*` including `USER_ROLE`.
- Three roles: Resident, Admin, Field Worker. Sign up picks the role with a `TabLayout` at the top (Resident, Admin, Worker). Login accepts email or mobile number plus password and has no role switch: the stored account decides which home screen opens. Each role has its own root screen (`ResidentHomeActivity`, `AdminHomeActivity`, `WorkerHomeActivity`), and Logout is only on Profile.
- "Social accounts" sign-in is a button that shows a Toast ("Simulated sign-in") and logs in the demo resident. No real OAuth.
- Create one demo account per role automatically: `resident@demo.com`, `admin@demo.com`, `worker@demo.com`, all with password `demo1234`.
- Seed demo data (several tickets across all four statuses and all three categories, two or three announcements, a few notifications) in one `seedDemo(SQLiteDatabase)` method called from `DbHelper.onCreate`, so it runs once on first install and no screen is empty in a demo. Never reseed on login or launch.
- Never put a role's screens behind a check in the UI only. Every role-specific Activity is reached from that role's home screen.
- Light and dark mode are both supported through the generated theme. Do not hardcode colors.
- Notifications are in-app only (Toast, Snackbar, list). Never use real Android notifications or channels.

## App-specific rules

1. **Resident module**
   - Home has bottom-of-screen actions to Report Issue, My Reports, Community Feed, Notifications and Profile (cards or buttons, no BottomNavigationView).
   - Report Issue: category dropdown, title, description, urgency dropdown, location (barangay dropdown plus street text), photo/video picker. Saves a ticket with status `STATUS_SUBMITTED`, then closes with a Toast.
   - My Reports: all tickets of the logged-in user, newest first, with a `ChipGroup` filter by status. Tapping opens Ticket Detail showing details, media name, status timeline (the four steps, current one highlighted) and resolution "After" photo when resolved.
   - Community Feed: `TabLayout` with two tabs, Nearby Issues (all other residents' tickets in the same barangay) and Announcements. Nearby issues have an upvote button; one upvote per user per ticket, toggling off removes it (`upvotes` table, counter in `tickets.upvotes`).
   - Profile: edit name, mobile, email, notification switch (`MaterialSwitch`, stored in `users.notify_enabled`), logout.
2. **Admin and Dispatcher module**
   - Ticket Dashboard: all tickets in a list, filter by category, status and urgency (`ChipGroup`), sort by newest or urgency. Counts per status shown at the top as plain text.
   - Ticket Detail (admin): change status via dropdown, assign to a department or worker via dropdown (hardcoded departments: Public Works, Sanitation Office, Electrical Team; workers are the users with role worker), add internal notes (bottom sheet, list of notes), attach "After" resolution proof (Photo Picker).
   - Assigning a ticket sets `assigned_to` and `assigned_dept`; if status is Submitted it moves to Under Review.
3. **Field Worker module**
   - Assigned Tasks: only tickets where `assigned_to` is the logged-in worker, not yet Resolved first, with a status filter.
   - Task Detail: shows ticket info, buttons to set status to In Progress or Resolved. Resolved requires an "After" photo (Photo Picker); show an inline error on the field if missing.
4. **Notifications**
   - Whenever a ticket status changes (by admin or worker), insert a row into `notifications` for the ticket owner and show a Toast "Ticket #id is now <status>" only if the owner is the logged-in user (otherwise they see it in their list on next open).
   - Notifications screen lists the user's notifications newest first, unread in bold, marks all read in `onResume` of the screen after displaying. Respect the `notify_enabled` switch: if off, still store the row but skip the Toast.

## Screens (one Activity each)

Auth: `LoginActivity` (launcher), `SignUpActivity`, `ResetPasswordActivity`.
Resident: `ResidentHomeActivity`, `ReportIssueActivity`, `MyReportsActivity`, `TicketDetailActivity`, `CommunityFeedActivity`, `NotificationsActivity`, `ProfileActivity` (shared by all roles, Logout lives here).
Admin: `AdminHomeActivity` (ticket dashboard), `AdminTicketActivity`.
Worker: `WorkerHomeActivity` (assigned tasks), `WorkerTaskActivity`.

## Manifest

Only `LoginActivity` (launcher) is `exported="true"`. Every other Activity is `exported="false"`. Register every new Activity.

## Release

- `release` uses `isMinifyEnabled = true` and `isShrinkResources = true`. Add keep rules to `proguard-rules.pro` only when a release crash needs one.
- Signing reads `keystore.properties` in the project root (see `keystore.properties.example`). Never commit `keystore.properties`, `*.jks` or `*.keystore`.
- Verify with `apksigner verify --print-certs --verbose <apk>`.

## Commands

- Build: `./gradlew assembleDebug`
- Release: `./gradlew assembleRelease`
- Lint: `./gradlew lintDebug`
- Run: Android Studio's Run button on an emulator.

## Commit rules

- Never list Claude as an author, co-author or contributor. No `Co-Authored-By` line mentioning Claude or Anthropic. No "Generated with Claude Code" footer in commits or PR descriptions.
- Never mention Claude or Anthropic anywhere: not in commit messages, PR descriptions, code, comments, file headers, README, docs, manifest or resources.
- Do not change `git config user.name` or `user.email`.
- Short, plain, imperative messages, for example `Add report issue screen`.
- Do not commit unless asked.

## Working agreements

- Keep changes small and in scope. Do not refactor or restyle code you were not asked to touch.
- After a change, run the build and report failures with the output.
- Update this file when a rule changes or a screen is added.
