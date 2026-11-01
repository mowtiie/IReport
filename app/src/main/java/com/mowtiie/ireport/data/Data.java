package com.mowtiie.ireport.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.color.MaterialColors;
import com.mowtiie.ireport.activities.AdminHomeActivity;
import com.mowtiie.ireport.activities.LoginActivity;
import com.mowtiie.ireport.activities.ResidentHomeActivity;
import com.mowtiie.ireport.activities.WorkerHomeActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Data {

    public static final String ROLE_RESIDENT = "ROLE_RESIDENT";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_WORKER = "ROLE_WORKER";
    public static final String[] ROLES = {ROLE_RESIDENT, ROLE_ADMIN, ROLE_WORKER};

    public static final String STATUS_SUBMITTED = "STATUS_SUBMITTED";
    public static final String STATUS_UNDER_REVIEW = "STATUS_UNDER_REVIEW";
    public static final String STATUS_IN_PROGRESS = "STATUS_IN_PROGRESS";
    public static final String STATUS_RESOLVED = "STATUS_RESOLVED";

    public static final String CAT_ROADS = "CAT_ROADS";
    public static final String CAT_SANITATION = "CAT_SANITATION";
    public static final String CAT_LIGHTING = "CAT_LIGHTING";

    public static final String URGENCY_LOW = "URGENCY_LOW";
    public static final String URGENCY_MEDIUM = "URGENCY_MEDIUM";
    public static final String URGENCY_HIGH = "URGENCY_HIGH";

    public static final String DEPT_PUBLIC_WORKS = "Public Works";
    public static final String DEPT_SANITATION_OFFICE = "Sanitation Office";
    public static final String DEPT_ELECTRICAL_TEAM = "Electrical Team";

    public static final String[] STATUS_FLOW = {
            STATUS_SUBMITTED, STATUS_UNDER_REVIEW, STATUS_IN_PROGRESS, STATUS_RESOLVED
    };
    public static final String[] CATEGORIES = {CAT_ROADS, CAT_SANITATION, CAT_LIGHTING};
    public static final String[] URGENCIES = {URGENCY_LOW, URGENCY_MEDIUM, URGENCY_HIGH};

    public static final String[] BARANGAYS = {"Poblacion", "San Isidro", "Santa Cruz", "Bagong Silang"};
    public static final String LOCATION_SEPARATOR = ", ";

    public static final String[] SAMPLE_MEDIA = {
            "sample_pothole", "sample_trash", "sample_streetlight", "sample_clip_road"
    };

    public static final String[] SAMPLE_AFTER_MEDIA = {
            "sample_after_road", "sample_after_trash", "sample_after_streetlight"
    };

    public static final String EXTRA_TICKET_ID = "ticket_id";

    public static final String DEMO_RESIDENT_EMAIL = "resident@demo.com";
    public static final String DEMO_ADMIN_EMAIL = "admin@demo.com";
    public static final String DEMO_WORKER_EMAIL = "worker@demo.com";
    public static final String DEMO_NEIGHBOR_EMAIL = "neighbor@demo.com";
    public static final String DEMO_PASSWORD = "demo1234";

    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final String MOBILE_PATTERN = "09\\d{9}";
    public static final String NO_USER_ID = "0";

    public static final String TABLE_USERS = "users";
    public static final String TABLE_TICKETS = "tickets";
    public static final String TABLE_TICKET_NOTES = "ticket_notes";
    public static final String TABLE_UPVOTES = "upvotes";
    public static final String TABLE_ANNOUNCEMENTS = "announcements";
    public static final String TABLE_NOTIFICATIONS = "notifications";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_NOTIFY_ENABLED = "notify_enabled";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_URGENCY = "urgency";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_MEDIA = "media";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_ASSIGNED_TO = "assigned_to";
    public static final String COLUMN_ASSIGNED_DEPT = "assigned_dept";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPVOTES = "upvotes";
    public static final String COLUMN_RESOLUTION_MEDIA = "resolution_media";
    public static final String COLUMN_TICKET_ID = "ticket_id";
    public static final String COLUMN_AUTHOR_ID = "author_id";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_BODY = "body";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_IS_READ = "is_read";

    public static final int TICKET_ID = 0;
    public static final int TICKET_USER_ID = 1;
    public static final int TICKET_CATEGORY = 2;
    public static final int TICKET_TITLE = 3;
    public static final int TICKET_DESCRIPTION = 4;
    public static final int TICKET_URGENCY = 5;
    public static final int TICKET_LOCATION = 6;
    public static final int TICKET_MEDIA = 7;
    public static final int TICKET_STATUS = 8;
    public static final int TICKET_ASSIGNED_TO = 9;
    public static final int TICKET_ASSIGNED_DEPT = 10;
    public static final int TICKET_CREATED_AT = 11;
    public static final int TICKET_UPVOTES = 12;
    public static final int TICKET_RESOLUTION_MEDIA = 13;

    public static final int NOTIFICATION_TICKET_ID = 2;
    public static final int NOTIFICATION_MESSAGE = 3;
    public static final int NOTIFICATION_CREATED_AT = 4;
    public static final int NOTIFICATION_IS_READ = 5;

    public static final int ANNOUNCEMENT_TITLE = 1;
    public static final int ANNOUNCEMENT_BODY = 2;
    public static final int ANNOUNCEMENT_CREATED_AT = 3;

    public static final int USER_ROW_ID = 0;
    public static final int USER_ROW_NAME = 3;

    public static final int NOTE_TEXT = 3;
    public static final int NOTE_CREATED_AT = 4;

    private static final String ORDER_NEWEST = COLUMN_ID + " DESC";
    private static final String ORDER_URGENCY = "CASE " + COLUMN_URGENCY
            + " WHEN '" + URGENCY_HIGH + "' THEN 0 WHEN '" + URGENCY_MEDIUM + "' THEN 1 ELSE 2 END, "
            + ORDER_NEWEST;

    public static String USER_ID;
    public static String USER_EMAIL;
    public static String USER_NAME;
    public static String USER_ROLE;
    public static String USER_PHONE;
    public static boolean USER_NOTIFY_ENABLED;

    private static DbHelper dbHelper;

    private static SQLiteDatabase database(Context context) {
        if (dbHelper == null) {
            dbHelper = new DbHelper(context.getApplicationContext());
        }
        return dbHelper.getWritableDatabase();
    }

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidMobile(String mobile) {
        return mobile.matches(MOBILE_PATTERN);
    }

    public static boolean isValidPassword(String password) {
        return password.length() >= MIN_PASSWORD_LENGTH;
    }

    public static String roleLabel(String role) {
        if (ROLE_ADMIN.equals(role)) {
            return "Admin";
        }
        if (ROLE_WORKER.equals(role)) {
            return "Field Worker";
        }
        return "Resident";
    }

    public static String statusLabel(String status) {
        if (STATUS_UNDER_REVIEW.equals(status)) {
            return "Under Review";
        }
        if (STATUS_IN_PROGRESS.equals(status)) {
            return "In Progress";
        }
        if (STATUS_RESOLVED.equals(status)) {
            return "Resolved";
        }
        return "Submitted";
    }

    public static boolean login(Context context, String identifier, String password) {
        String identifierColumn = isValidEmail(identifier) ? COLUMN_EMAIL : COLUMN_PHONE;
        String where = identifierColumn + " = ? COLLATE NOCASE AND " + COLUMN_PASSWORD + " = ?";
        try (Cursor cursor = database(context).query(TABLE_USERS, null, where,
                new String[]{identifier, password}, null, null, null)) {
            if (!cursor.moveToFirst()) {
                return false;
            }
            copyUserFields(cursor);
            return true;
        }
    }

    public static void signUp(Context context, String role, String name, String mobile,
                              String email, String password) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_ROLE, role);
        values.put(COLUMN_PHONE, mobile);
        values.put(COLUMN_NOTIFY_ENABLED, 1);
        database(context).insert(TABLE_USERS, null, values);
    }

    public static boolean isUserFieldTaken(Context context, String column, String value,
                                           String excludedUserId) {
        String where = column + " = ? COLLATE NOCASE AND " + COLUMN_ID + " != ?";
        try (Cursor cursor = database(context).query(TABLE_USERS, new String[]{COLUMN_ID}, where,
                new String[]{value, excludedUserId}, null, null, null)) {
            return cursor.moveToFirst();
        }
    }

    public static boolean resetPassword(Context context, String identifier, String newPassword) {
        String identifierColumn = isValidEmail(identifier) ? COLUMN_EMAIL : COLUMN_PHONE;
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);
        int updatedRows = database(context).update(TABLE_USERS, values,
                identifierColumn + " = ? COLLATE NOCASE", new String[]{identifier});
        return updatedRows > 0;
    }

    public static void updateProfile(Context context, String name, String mobile, String email) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, mobile);
        values.put(COLUMN_EMAIL, email);
        database(context).update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{USER_ID});
        USER_NAME = name;
        USER_PHONE = mobile;
        USER_EMAIL = email;
    }

    public static void setNotifyEnabled(Context context, boolean enabled) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIFY_ENABLED, enabled ? 1 : 0);
        database(context).update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{USER_ID});
        USER_NOTIFY_ENABLED = enabled;
    }

    public static ArrayList<String[]> loadNotifications(Context context) {
        return queryRows(context, TABLE_NOTIFICATIONS, COLUMN_USER_ID + " = ?",
                new String[]{USER_ID}, ORDER_NEWEST);
    }

    public static void markNotificationsRead(Context context) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_READ, 1);
        database(context).update(TABLE_NOTIFICATIONS, values, COLUMN_USER_ID + " = ?", new String[]{USER_ID});
    }

    public static ArrayList<String[]> loadAnnouncements(Context context) {
        return queryRows(context, TABLE_ANNOUNCEMENTS, null, null, ORDER_NEWEST);
    }

    public static ArrayList<String[]> loadUserTickets(Context context, String status) {
        if (status == null) {
            return queryRows(context, TABLE_TICKETS, COLUMN_USER_ID + " = ?",
                    new String[]{USER_ID}, ORDER_NEWEST);
        }
        return queryRows(context, TABLE_TICKETS, COLUMN_USER_ID + " = ? AND " + COLUMN_STATUS + " = ?",
                new String[]{USER_ID, status}, ORDER_NEWEST);
    }

    public static ArrayList<String[]> loadAssignedTickets(Context context, String status) {
        String openFirst = "CASE " + COLUMN_STATUS + " WHEN '" + STATUS_RESOLVED + "' THEN 1 ELSE 0 END, "
                + ORDER_NEWEST;
        if (status == null) {
            return queryRows(context, TABLE_TICKETS, COLUMN_ASSIGNED_TO + " = ?",
                    new String[]{USER_ID}, openFirst);
        }
        return queryRows(context, TABLE_TICKETS, COLUMN_ASSIGNED_TO + " = ? AND " + COLUMN_STATUS + " = ?",
                new String[]{USER_ID, status}, openFirst);
    }

    public static ArrayList<String[]> loadNearbyTickets(Context context, String barangay) {
        String where = COLUMN_USER_ID + " != ? AND " + COLUMN_LOCATION + " LIKE ?";
        return queryRows(context, TABLE_TICKETS, where,
                new String[]{USER_ID, barangay + LOCATION_SEPARATOR + "%"}, ORDER_NEWEST);
    }

    public static ArrayList<String[]> loadAllTickets(Context context, String category, String status,
                                                     String urgency, boolean sortByUrgency) {
        ArrayList<String> conditions = new ArrayList<>();
        ArrayList<String> args = new ArrayList<>();
        addFilter(conditions, args, COLUMN_CATEGORY, category);
        addFilter(conditions, args, COLUMN_STATUS, status);
        addFilter(conditions, args, COLUMN_URGENCY, urgency);
        String where = conditions.isEmpty() ? null : TextUtils.join(" AND ", conditions);
        String[] whereArgs = args.isEmpty() ? null : args.toArray(new String[0]);
        return queryRows(context, TABLE_TICKETS, where, whereArgs, sortByUrgency ? ORDER_URGENCY : ORDER_NEWEST);
    }

    public static long countTickets(Context context, String status) {
        return DatabaseUtils.queryNumEntries(database(context), TABLE_TICKETS,
                COLUMN_STATUS + " = ?", new String[]{status});
    }

    public static ArrayList<String[]> loadWorkers(Context context) {
        return queryRows(context, TABLE_USERS, COLUMN_ROLE + " = ?", new String[]{ROLE_WORKER}, COLUMN_NAME);
    }

    public static String loadUserName(Context context, String userId) {
        ArrayList<String[]> rows = queryRows(context, TABLE_USERS, COLUMN_ID + " = ?",
                new String[]{userId}, null);
        return rows.get(0)[USER_ROW_NAME];
    }

    public static ArrayList<String[]> loadNotes(Context context, String ticketId) {
        return queryRows(context, TABLE_TICKET_NOTES, COLUMN_TICKET_ID + " = ?",
                new String[]{ticketId}, ORDER_NEWEST);
    }

    public static void addNote(Context context, String ticketId, String note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TICKET_ID, ticketId);
        values.put(COLUMN_AUTHOR_ID, USER_ID);
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_CREATED_AT, today());
        database(context).insert(TABLE_TICKET_NOTES, null, values);
    }

    public static void setResolutionMedia(Context context, String ticketId, String media) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RESOLUTION_MEDIA, media);
        database(context).update(TABLE_TICKETS, values, COLUMN_ID + " = ?", new String[]{ticketId});
    }

    public static void changeTicketStatus(Context context, String ticketId, String status) {
        String[] ticket = loadTicket(context, ticketId);
        if (status.equals(ticket[TICKET_STATUS])) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);
        database(context).update(TABLE_TICKETS, values, COLUMN_ID + " = ?", new String[]{ticketId});
        notifyOwner(context, ticket[TICKET_USER_ID], ticketId, "Ticket #" + ticketId + " is now " + statusLabel(status));
    }

    public static void assignTicket(Context context, String ticketId, String workerId, String department) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ASSIGNED_TO, workerId);
        values.put(COLUMN_ASSIGNED_DEPT, department);
        database(context).update(TABLE_TICKETS, values, COLUMN_ID + " = ?", new String[]{ticketId});
        if (STATUS_SUBMITTED.equals(loadTicket(context, ticketId)[TICKET_STATUS])) {
            changeTicketStatus(context, ticketId, STATUS_UNDER_REVIEW);
        }
    }

    public static String[] loadTicket(Context context, String ticketId) {
        ArrayList<String[]> rows = queryRows(context, TABLE_TICKETS, COLUMN_ID + " = ?",
                new String[]{ticketId}, null);
        return rows.get(0);
    }

    public static String findHomeBarangay(Context context) {
        String barangay = "substr(" + COLUMN_LOCATION + ", 1, instr(" + COLUMN_LOCATION
                + ", '" + LOCATION_SEPARATOR + "') - 1)";
        String sql = "SELECT " + barangay + " AS barangay FROM " + TABLE_TICKETS
                + " WHERE " + COLUMN_USER_ID + " = ? GROUP BY barangay ORDER BY COUNT(*) DESC LIMIT 1";
        try (Cursor cursor = database(context).rawQuery(sql, new String[]{USER_ID})) {
            return cursor.moveToFirst() ? cursor.getString(0) : BARANGAYS[0];
        }
    }

    public static void createTicket(Context context, String category, String title, String description,
                                    String urgency, String location, String media) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, USER_ID);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_URGENCY, urgency);
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_MEDIA, media);
        values.put(COLUMN_STATUS, STATUS_SUBMITTED);
        values.put(COLUMN_CREATED_AT, today());
        values.put(COLUMN_UPVOTES, 0);
        database(context).insert(TABLE_TICKETS, null, values);
    }

    public static boolean hasUpvoted(Context context, String ticketId) {
        String where = COLUMN_TICKET_ID + " = ? AND " + COLUMN_USER_ID + " = ?";
        try (Cursor cursor = database(context).query(TABLE_UPVOTES, new String[]{COLUMN_ID}, where,
                new String[]{ticketId, USER_ID}, null, null, null)) {
            return cursor.moveToFirst();
        }
    }

    public static void toggleUpvote(Context context, String ticketId) {
        SQLiteDatabase db = database(context);
        String where = COLUMN_TICKET_ID + " = ? AND " + COLUMN_USER_ID + " = ?";
        if (hasUpvoted(context, ticketId)) {
            db.delete(TABLE_UPVOTES, where, new String[]{ticketId, USER_ID});
            adjustUpvoteCount(db, ticketId, -1);
            return;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_TICKET_ID, ticketId);
        values.put(COLUMN_USER_ID, USER_ID);
        db.insert(TABLE_UPVOTES, null, values);
        adjustUpvoteCount(db, ticketId, 1);
    }

    public static String categoryLabel(String category) {
        if (CAT_SANITATION.equals(category)) {
            return "Sanitation";
        }
        if (CAT_LIGHTING.equals(category)) {
            return "Lighting";
        }
        return "Roads";
    }

    public static String urgencyLabel(String urgency) {
        if (URGENCY_HIGH.equals(urgency)) {
            return "High";
        }
        if (URGENCY_MEDIUM.equals(urgency)) {
            return "Medium";
        }
        return "Low";
    }

    public static String today() {
        return new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date());
    }

    public static int drawableFor(Context context, String resourceName) {
        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }

    public static void applyStatusChip(Chip chip, String status) {
        int containerAttr = com.google.android.material.R.attr.colorSurfaceVariant;
        int contentAttr = com.google.android.material.R.attr.colorOnSurfaceVariant;
        if (STATUS_UNDER_REVIEW.equals(status)) {
            containerAttr = com.google.android.material.R.attr.colorSecondaryContainer;
            contentAttr = com.google.android.material.R.attr.colorOnSecondaryContainer;
        } else if (STATUS_IN_PROGRESS.equals(status)) {
            containerAttr = com.google.android.material.R.attr.colorTertiaryContainer;
            contentAttr = com.google.android.material.R.attr.colorOnTertiaryContainer;
        } else if (STATUS_RESOLVED.equals(status)) {
            containerAttr = com.google.android.material.R.attr.colorPrimaryContainer;
            contentAttr = com.google.android.material.R.attr.colorOnPrimaryContainer;
        }
        chip.setText(statusLabel(status));
        chip.setChipBackgroundColor(ColorStateList.valueOf(MaterialColors.getColor(chip, containerAttr)));
        chip.setTextColor(MaterialColors.getColor(chip, contentAttr));
    }

    public static void openHome(Context context) {
        Intent intent = new Intent(context, homeActivityFor(USER_ROLE));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void logout(Context context) {
        USER_ID = null;
        USER_EMAIL = null;
        USER_NAME = null;
        USER_ROLE = null;
        USER_PHONE = null;
        USER_NOTIFY_ENABLED = false;
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private static Class<?> homeActivityFor(String role) {
        if (ROLE_ADMIN.equals(role)) {
            return AdminHomeActivity.class;
        }
        if (ROLE_WORKER.equals(role)) {
            return WorkerHomeActivity.class;
        }
        return ResidentHomeActivity.class;
    }

    private static ArrayList<String[]> queryRows(Context context, String table, String where,
                                                 String[] whereArgs, String orderBy) {
        ArrayList<String[]> rows = new ArrayList<>();
        try (Cursor cursor = database(context).query(table, null, where, whereArgs, null, null, orderBy)) {
            while (cursor.moveToNext()) {
                String[] row = new String[cursor.getColumnCount()];
                for (int index = 0; index < row.length; index++) {
                    row[index] = cursor.getString(index);
                }
                rows.add(row);
            }
        }
        return rows;
    }

    private static void addFilter(ArrayList<String> conditions, ArrayList<String> args,
                                  String column, String value) {
        if (value == null) {
            return;
        }
        conditions.add(column + " = ?");
        args.add(value);
    }

    private static void notifyOwner(Context context, String ownerId, String ticketId, String message) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, ownerId);
        values.put(COLUMN_TICKET_ID, ticketId);
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_CREATED_AT, today());
        values.put(COLUMN_IS_READ, 0);
        database(context).insert(TABLE_NOTIFICATIONS, null, values);
        if (ownerId.equals(USER_ID) && USER_NOTIFY_ENABLED) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    private static void adjustUpvoteCount(SQLiteDatabase db, String ticketId, int delta) {
        db.execSQL("UPDATE " + TABLE_TICKETS + " SET " + COLUMN_UPVOTES + " = " + COLUMN_UPVOTES
                + " + (" + delta + ") WHERE " + COLUMN_ID + " = ?", new String[]{ticketId});
    }

    private static void copyUserFields(Cursor cursor) {
        USER_ID = readText(cursor, COLUMN_ID);
        USER_EMAIL = readText(cursor, COLUMN_EMAIL);
        USER_NAME = readText(cursor, COLUMN_NAME);
        USER_ROLE = readText(cursor, COLUMN_ROLE);
        USER_PHONE = readText(cursor, COLUMN_PHONE);
        USER_NOTIFY_ENABLED = "1".equals(readText(cursor, COLUMN_NOTIFY_ENABLED));
    }

    private static String readText(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndexOrThrow(column));
    }
}
