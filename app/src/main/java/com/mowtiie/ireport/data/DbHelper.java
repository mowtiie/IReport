package com.mowtiie.ireport.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ireport.db";
    private static final int DATABASE_VERSION = 1;

    private static final String[] TABLES = {
            Data.TABLE_USERS, Data.TABLE_TICKETS, Data.TABLE_TICKET_NOTES,
            Data.TABLE_UPVOTES, Data.TABLE_ANNOUNCEMENTS, Data.TABLE_NOTIFICATIONS
    };

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, "
                + "password TEXT, name TEXT, role TEXT, phone TEXT, notify_enabled INTEGER)");
        db.execSQL("CREATE TABLE tickets (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, "
                + "category TEXT, title TEXT, description TEXT, urgency TEXT, location TEXT, "
                + "media TEXT, status TEXT, assigned_to INTEGER, assigned_dept TEXT, "
                + "created_at TEXT, upvotes INTEGER, resolution_media TEXT)");
        db.execSQL("CREATE TABLE ticket_notes (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ticket_id INTEGER, author_id INTEGER, note TEXT, created_at TEXT)");
        db.execSQL("CREATE TABLE upvotes (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ticket_id INTEGER, user_id INTEGER)");
        db.execSQL("CREATE TABLE announcements (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT, body TEXT, created_at TEXT)");
        db.execSQL("CREATE TABLE notifications (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER, ticket_id INTEGER, message TEXT, created_at TEXT, is_read INTEGER)");
        seedDemo(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String table : TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
        onCreate(db);
    }

    private void seedDemo(SQLiteDatabase db) {
        long residentId = insertUser(db, Data.DEMO_RESIDENT_EMAIL, "Demo Resident", Data.ROLE_RESIDENT, "09171234567");
        long adminId = insertUser(db, Data.DEMO_ADMIN_EMAIL, "Demo Admin", Data.ROLE_ADMIN, "09181234567");
        long workerId = insertUser(db, Data.DEMO_WORKER_EMAIL, "Demo Worker", Data.ROLE_WORKER, "09191234567");
        long neighborId = insertUser(db, Data.DEMO_NEIGHBOR_EMAIL, "Maria Santos", Data.ROLE_RESIDENT, "09201234567");

        insertTicket(db, residentId, Data.CAT_ROADS, "Large pothole on Rizal Street",
                "A deep pothole is damaging tires and slowing down traffic.", Data.URGENCY_HIGH,
                "Poblacion, Rizal Street", "sample_pothole", Data.STATUS_SUBMITTED,
                null, null, "Oct 30, 2026", 0, null);
        long underReviewId = insertTicket(db, residentId, Data.CAT_SANITATION, "Overflowing bin near the market",
                "The bin has not been emptied for days and trash is spilling onto the road.", Data.URGENCY_MEDIUM,
                "Poblacion, Market Road", "sample_trash", Data.STATUS_UNDER_REVIEW,
                workerId, Data.DEPT_SANITATION_OFFICE, "Oct 27, 2026", 0, null);
        long inProgressId = insertTicket(db, residentId, Data.CAT_LIGHTING, "Broken streetlight at the corner",
                "The streetlight has been off for a week and the corner is very dark at night.", Data.URGENCY_HIGH,
                "Poblacion, Mabini Street", "sample_streetlight", Data.STATUS_IN_PROGRESS,
                workerId, Data.DEPT_ELECTRICAL_TEAM, "Oct 24, 2026", 0, null);
        long resolvedId = insertTicket(db, residentId, Data.CAT_ROADS, "Cracked road shoulder",
                "The road shoulder is crumbling near the purok entrance.", Data.URGENCY_LOW,
                "San Isidro, Purok 3", "sample_pothole", Data.STATUS_RESOLVED,
                workerId, Data.DEPT_PUBLIC_WORKS, "Oct 15, 2026", 0, "sample_after_road");
        long upvotedId = insertTicket(db, neighborId, Data.CAT_SANITATION, "Uncollected garbage pile",
                "Garbage has piled up beside the covered court since last week.", Data.URGENCY_HIGH,
                "Poblacion, Bonifacio Street", "sample_trash", Data.STATUS_SUBMITTED,
                null, null, "Oct 31, 2026", 1, null);
        insertTicket(db, neighborId, Data.CAT_LIGHTING, "Flickering lights at the plaza",
                "Several lights around the plaza keep flickering during the evening.", Data.URGENCY_MEDIUM,
                "Poblacion, Plaza", "sample_streetlight", Data.STATUS_IN_PROGRESS,
                workerId, Data.DEPT_ELECTRICAL_TEAM, "Oct 26, 2026", 0, null);
        insertTicket(db, neighborId, Data.CAT_ROADS, "Flooded road after rain",
                "Water stays on the road for hours after every rain.", Data.URGENCY_MEDIUM,
                "Santa Cruz, Purok 1", "sample_pothole", Data.STATUS_UNDER_REVIEW,
                null, null, "Oct 25, 2026", 0, null);
        insertTicket(db, neighborId, Data.CAT_SANITATION, "Clogged canal",
                "The canal is full of trash and smells bad.", Data.URGENCY_LOW,
                "Poblacion, Burgos Street", "sample_trash", Data.STATUS_RESOLVED,
                workerId, Data.DEPT_SANITATION_OFFICE, "Oct 12, 2026", 0, "sample_after_trash");

        insertUpvote(db, upvotedId, residentId);
        insertNote(db, underReviewId, adminId, "Sanitation crew is scheduled for tomorrow morning.", "Oct 28, 2026");

        insertAnnouncement(db, "Garbage collection schedule",
                "Collection in Poblacion moves to Tuesdays and Fridays starting next week.", "Oct 29, 2026");
        insertAnnouncement(db, "Road repair on Rizal Street",
                "Expect partial lane closures near the market while repairs are ongoing.", "Oct 27, 2026");
        insertAnnouncement(db, "Streetlight maintenance",
                "The Electrical Team will inspect streetlights in Poblacion this week.", "Oct 22, 2026");

        insertNotification(db, residentId, resolvedId, Data.STATUS_RESOLVED, "Oct 20, 2026", 1);
        insertNotification(db, residentId, inProgressId, Data.STATUS_IN_PROGRESS, "Oct 26, 2026", 0);
        insertNotification(db, residentId, underReviewId, Data.STATUS_UNDER_REVIEW, "Oct 28, 2026", 0);
    }

    private long insertUser(SQLiteDatabase db, String email, String name, String role, String phone) {
        ContentValues values = new ContentValues();
        values.put(Data.COLUMN_EMAIL, email);
        values.put(Data.COLUMN_PASSWORD, Data.DEMO_PASSWORD);
        values.put(Data.COLUMN_NAME, name);
        values.put(Data.COLUMN_ROLE, role);
        values.put(Data.COLUMN_PHONE, phone);
        values.put(Data.COLUMN_NOTIFY_ENABLED, 1);
        return db.insert(Data.TABLE_USERS, null, values);
    }

    private long insertTicket(SQLiteDatabase db, long userId, String category, String title,
                              String description, String urgency, String location, String media,
                              String status, Long assignedTo, String assignedDept, String createdAt,
                              int upvotes, String resolutionMedia) {
        ContentValues values = new ContentValues();
        values.put(Data.COLUMN_USER_ID, userId);
        values.put(Data.COLUMN_CATEGORY, category);
        values.put(Data.COLUMN_TITLE, title);
        values.put(Data.COLUMN_DESCRIPTION, description);
        values.put(Data.COLUMN_URGENCY, urgency);
        values.put(Data.COLUMN_LOCATION, location);
        values.put(Data.COLUMN_MEDIA, media);
        values.put(Data.COLUMN_STATUS, status);
        values.put(Data.COLUMN_ASSIGNED_TO, assignedTo);
        values.put(Data.COLUMN_ASSIGNED_DEPT, assignedDept);
        values.put(Data.COLUMN_CREATED_AT, createdAt);
        values.put(Data.COLUMN_UPVOTES, upvotes);
        values.put(Data.COLUMN_RESOLUTION_MEDIA, resolutionMedia);
        return db.insert(Data.TABLE_TICKETS, null, values);
    }

    private void insertUpvote(SQLiteDatabase db, long ticketId, long userId) {
        ContentValues values = new ContentValues();
        values.put(Data.COLUMN_TICKET_ID, ticketId);
        values.put(Data.COLUMN_USER_ID, userId);
        db.insert(Data.TABLE_UPVOTES, null, values);
    }

    private void insertNote(SQLiteDatabase db, long ticketId, long authorId, String note, String createdAt) {
        ContentValues values = new ContentValues();
        values.put(Data.COLUMN_TICKET_ID, ticketId);
        values.put(Data.COLUMN_AUTHOR_ID, authorId);
        values.put(Data.COLUMN_NOTE, note);
        values.put(Data.COLUMN_CREATED_AT, createdAt);
        db.insert(Data.TABLE_TICKET_NOTES, null, values);
    }

    private void insertAnnouncement(SQLiteDatabase db, String title, String body, String createdAt) {
        ContentValues values = new ContentValues();
        values.put(Data.COLUMN_TITLE, title);
        values.put(Data.COLUMN_BODY, body);
        values.put(Data.COLUMN_CREATED_AT, createdAt);
        db.insert(Data.TABLE_ANNOUNCEMENTS, null, values);
    }

    private void insertNotification(SQLiteDatabase db, long userId, long ticketId, String status,
                                    String createdAt, int isRead) {
        ContentValues values = new ContentValues();
        values.put(Data.COLUMN_USER_ID, userId);
        values.put(Data.COLUMN_TICKET_ID, ticketId);
        values.put(Data.COLUMN_MESSAGE, "Ticket #" + ticketId + " is now " + Data.statusLabel(status));
        values.put(Data.COLUMN_CREATED_AT, createdAt);
        values.put(Data.COLUMN_IS_READ, isRead);
        db.insert(Data.TABLE_NOTIFICATIONS, null, values);
    }
}
