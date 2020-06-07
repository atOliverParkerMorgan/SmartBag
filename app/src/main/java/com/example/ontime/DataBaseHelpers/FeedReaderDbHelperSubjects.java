package com.example.ontime.DataBaseHelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class FeedReaderDbHelperSubjects extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";
    private final static String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                FeedEntry._ID + " INTEGER PRIMARY KEY," +
                FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                FeedEntry.COLUMN_NAME_MONDAY + " TEXT,"+
                FeedEntry.COLUMN_NAME_TUESDAY + " TEXT,"+
                FeedEntry.COLUMN_NAME_WEDNESDAY + " TEXT,"+
                FeedEntry.COLUMN_NAME_THURSDAY + " TEXT,"+
                FeedEntry.COLUMN_NAME_FRIDAY + " TEXT,"+
                FeedEntry.COLUMN_NAME_SATURDAY + " TEXT,"+
                FeedEntry.COLUMN_NAME_SUNDAY + " TEXT)";;

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public FeedReaderDbHelperSubjects(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void delete(SQLiteDatabase db){
        db.execSQL(SQL_DELETE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "subjects";
        public static final String COLUMN_NAME_TITLE = "name";
        public static final String COLUMN_NAME_MONDAY = "monday";
        public static final String COLUMN_NAME_TUESDAY = "tuesday";
        public static final String COLUMN_NAME_WEDNESDAY = "wednesday";
        public static final String COLUMN_NAME_THURSDAY = "thursday";
        public static final String COLUMN_NAME_FRIDAY = "friday";
        public static final String COLUMN_NAME_SATURDAY = "saturday";
        public static final String COLUMN_NAME_SUNDAY = "sunday";

    }
}
