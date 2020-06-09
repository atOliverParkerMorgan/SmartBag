package com.example.ontime.DataBaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.ontime.Adapter.Item;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FeedReaderDbHelperSubjects extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Subject.db";
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
                FeedEntry.COLUMN_NAME_SUNDAY + " TEXT)";

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


    protected static class FeedEntry implements BaseColumns {
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
    public static List<String> getContent(Context context, boolean getAll){
        FeedReaderDbHelperSubjects dbHelperForSubject = new FeedReaderDbHelperSubjects(context);
        SQLiteDatabase dbForSubject = dbHelperForSubject.getReadableDatabase();
        List<String> subjectNames = new ArrayList<>();

        if(!getAll) {
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            final String[] projectionSubject = {
                    BaseColumns._ID,
                    FeedEntry.COLUMN_NAME_TITLE,
                    FeedEntry.COLUMN_NAME_MONDAY,
                    FeedEntry.COLUMN_NAME_TUESDAY,
                    FeedEntry.COLUMN_NAME_WEDNESDAY,
                    FeedEntry.COLUMN_NAME_FRIDAY,
                    FeedEntry.COLUMN_NAME_SATURDAY,
                    FeedEntry.COLUMN_NAME_SUNDAY
            };

            // subset is initialized in switch statement
            String selectionSubject = null;
            final String[] selectionArgsSubject = {"true"};

            // get day of the week
            Calendar calendar = Calendar.getInstance();

            switch (calendar.getTime().toString().substring(0, 2)) {
                case "Mo":
                    selectionSubject = FeedEntry.COLUMN_NAME_MONDAY + " = ?";
                    break;
                case "Tu":
                    selectionSubject = FeedEntry.COLUMN_NAME_TUESDAY + " = ?";
                    break;
                case "We":
                    selectionSubject = FeedEntry.COLUMN_NAME_WEDNESDAY + " = ?";
                    break;
                case "Th":
                    selectionSubject = FeedEntry.COLUMN_NAME_THURSDAY + " = ?";
                    break;
                case "Fr":
                    selectionSubject = FeedEntry.COLUMN_NAME_FRIDAY + " = ?";
                    break;
                case "Sa":
                    selectionSubject = FeedEntry.COLUMN_NAME_SATURDAY + " = ?";
                    break;
                case "Su":
                    selectionSubject = FeedEntry.COLUMN_NAME_SUNDAY + " = ?";
                    break;
            }

            // How you want the results sorted in the resulting Cursor
            String sortOrderSubject =
                    FeedEntry.COLUMN_NAME_TITLE + " DESC";

            Cursor cursorSubject = dbForSubject.query(
                    FeedEntry.TABLE_NAME,   // The table to query
                    projectionSubject,             // The array of columns to return (pass null to get all)
                    selectionSubject,              // The columns for the WHERE clause
                    selectionArgsSubject,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrderSubject               // The sort order
            );

            // get all the subjects that have today marked as true
            while (cursorSubject.moveToNext()) {
                String subject = cursorSubject.getString(
                        cursorSubject.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
                subjectNames.add(subject);
            }
            cursorSubject.close();
        }else {
            String selectionItem = "select * from "+ FeedEntry.TABLE_NAME;

            Cursor  cursorItem = dbForSubject.rawQuery(selectionItem,null);
            while (cursorItem.moveToNext()) {
                String subject = cursorItem.getString(
                        cursorItem.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
                subjectNames.add(subject);
            }
            cursorItem.close();

        }

        return subjectNames;
    }

    public static boolean write(Context context, Intent intent, String subject){

        // adding to database
        // DataBase work
        FeedReaderDbHelperSubjects dbHelperForSubject = new FeedReaderDbHelperSubjects(context);

        // Gets the data repository in write mode
        SQLiteDatabase dbForSubject = dbHelperForSubject.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues valuesForSubject = new ContentValues();
        valuesForSubject.put(FeedEntry.COLUMN_NAME_TITLE, subject);
        valuesForSubject.put(FeedEntry.COLUMN_NAME_MONDAY, (String) intent.getSerializableExtra("Monday"));
        valuesForSubject.put(FeedEntry.COLUMN_NAME_TUESDAY, (String) intent.getSerializableExtra("Tuesday"));
        valuesForSubject.put(FeedEntry.COLUMN_NAME_WEDNESDAY, (String) intent.getSerializableExtra("Wednesday"));
        valuesForSubject.put(FeedEntry.COLUMN_NAME_THURSDAY, (String) intent.getSerializableExtra("Thursday"));
        valuesForSubject.put(FeedEntry.COLUMN_NAME_FRIDAY, (String) intent.getSerializableExtra("Friday"));
        valuesForSubject.put(FeedEntry.COLUMN_NAME_SATURDAY, (String) intent.getSerializableExtra("Saturday"));
        valuesForSubject.put(FeedEntry.COLUMN_NAME_SUNDAY, (String) intent.getSerializableExtra("Sunday"));


        // Insert the new row, returning the primary key value of the new row
       return dbForSubject.insert(FeedEntry.TABLE_NAME, null, valuesForSubject)>0;


    }

}
