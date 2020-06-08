package com.example.ontime.DataBaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.ontime.Adapter.Item;

import java.util.ArrayList;
import java.util.List;

public class FeedReaderDbHelperMyBag extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MyBag.db";
    private final static String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedEntry.COLUMN_SUBJECT_TITLE + " TEXT)";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public FeedReaderDbHelperMyBag(Context context) {
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
        public static final String TABLE_NAME = "myBag";
        public static final String COLUMN_NAME_TITLE = "name";
        public static final String COLUMN_SUBJECT_TITLE = "subject";


    }
    public static List<String[]> getContent(Context context){
        FeedReaderDbHelperMyBag dbHelperForItem = new FeedReaderDbHelperMyBag(context);
        SQLiteDatabase dbForItem = dbHelperForItem.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        final String[] projectionItem = {
                BaseColumns._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_SUBJECT_TITLE
        };
        // subset is initialized in switch statement
        String selectionItem = "select * from "+FeedEntry.TABLE_NAME;
        final String[] selectionArgsItem = {};

        // How you want the results sorted in the resulting Cursor
        String sortOrderItem =
                FeedEntry.COLUMN_NAME_TITLE + " DESC";

        Cursor  cursorItem = dbForItem.rawQuery(selectionItem,null);

        // get all the subjects that have today marked as true
        List<String[]> subjectNames = new ArrayList<>();
        while(cursorItem.moveToNext()) {
            String item = cursorItem.getString(
                    cursorItem.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
            String subject = cursorItem.getString(
                    cursorItem.getColumnIndexOrThrow(FeedEntry.COLUMN_SUBJECT_TITLE));
            subjectNames.add(new String[]{item,subject});
        }
        cursorItem.close();

        return subjectNames;
    }

    public static boolean write(Context context, final List<Item> defaultItemsDataItemsToAdd){
        // adding to database
        // DataBase work
        FeedReaderDbHelperMyBag dbHelperForItems = new FeedReaderDbHelperMyBag(context);
        // Gets the data repository in write mode
        SQLiteDatabase dbForItems = dbHelperForItems.getWritableDatabase();

        // Create a new map of values, where column names are the keys

        // adding data to table
        ContentValues valuesForItems = new ContentValues();
        for (Item item : defaultItemsDataItemsToAdd) {
            valuesForItems.put(FeedEntry.COLUMN_NAME_TITLE, item.getItemName());
            valuesForItems.put(FeedEntry.COLUMN_SUBJECT_TITLE, item.getSubjectName());
        }

        // Insert the new row, returning the primary key value of the new row
        return dbForItems.insert(FeedEntry.TABLE_NAME, null, valuesForItems) > 0;


    }
    public static boolean delete(Context context, Item item){
        // deleting from database
        // DataBase work
        FeedReaderDbHelperMyBag dbHelperForItems = new FeedReaderDbHelperMyBag(context);
        // Gets the data repository in write mode
        SQLiteDatabase dbForItems = dbHelperForItems.getWritableDatabase();

        //  delete
        return dbForItems.delete(FeedEntry.TABLE_NAME, FeedEntry.COLUMN_NAME_TITLE + " LIKE ? and "+ FeedEntry.COLUMN_SUBJECT_TITLE +  " LIKE ?", new String[]{item.getItemName(), item.getSubjectName()})>0;
    }

}
