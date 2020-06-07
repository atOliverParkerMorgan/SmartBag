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

public class FeedReaderDbHelperItems extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Items.db";
    private final static String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedReaderDbHelperItems.FeedEntry.TABLE_NAME + " (" +
                    FeedReaderDbHelperItems.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderDbHelperItems.FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedReaderDbHelperItems.FeedEntry.COLUMN_SUBJECT_TITLE + " TEXT)";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderDbHelperSubjects.FeedEntry.TABLE_NAME;

    public FeedReaderDbHelperItems(Context context) {
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
        public static final String TABLE_NAME = "items";
        public static final String COLUMN_NAME_TITLE = "name";
        public static final String COLUMN_SUBJECT_TITLE = "subject";


    }
    public static List<String> getContent(Context context, final String subjectName){
        FeedReaderDbHelperItems dbHelperForItem = new FeedReaderDbHelperItems(context);
        SQLiteDatabase dbForItem = dbHelperForItem.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        final String[] projectionItem = {
                BaseColumns._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_SUBJECT_TITLE
        };
        // subset is initialized in switch statement
        String selectionItem = FeedEntry.COLUMN_SUBJECT_TITLE + " = ?";
        final String[] selectionArgsItem = {subjectName};

        // How you want the results sorted in the resulting Cursor
        String sortOrderItem =
                FeedEntry.COLUMN_NAME_TITLE + " DESC";

        Cursor cursorItem = dbForItem.query(
                FeedEntry.TABLE_NAME,   // The table to query
                projectionItem,             // The array of columns to return (pass null to get all)
                selectionItem,              // The columns for the WHERE clause
                selectionArgsItem,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrderItem               // The sort order
        );

        // get all the subjects that have today marked as true
        List<String> subjectNames = new ArrayList<>();
        while(cursorItem.moveToNext()) {
            String subject = cursorItem.getString(
                    cursorItem.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
            subjectNames.add(subject);
        }
        cursorItem.close();

        return subjectNames;
    }

    public static long write(Context context, final List<Item> defaultItemsDataItemsToAdd, String subject){
        // adding to database
        // DataBase work
        FeedReaderDbHelperItems dbHelperForItems = new FeedReaderDbHelperItems(context);
        // Gets the data repository in write mode
        SQLiteDatabase dbForItems = dbHelperForItems.getWritableDatabase();

        // Create a new map of values, where column names are the keys

        // adding data to table
        ContentValues valuesForItems = new ContentValues();
        for (Item item : defaultItemsDataItemsToAdd) {
            valuesForItems.put(FeedEntry.COLUMN_NAME_TITLE, item.getItemName());
            valuesForItems.put(FeedReaderDbHelperItems.FeedEntry.COLUMN_SUBJECT_TITLE, subject);
        }

        // Insert the new row, returning the primary key value of the new row
        return dbForItems.insert(FeedReaderDbHelperItems.FeedEntry.TABLE_NAME, null, valuesForItems);


    }
}
