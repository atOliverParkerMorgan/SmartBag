package com.olivermorgan.ontime.main.DataBaseHelpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


import com.olivermorgan.ontime.main.Activities.MainActivity;
import com.olivermorgan.ontime.main.Adapter.Item;
import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperItems.FeedEntry.COLUMN_SUBJECT_TITLE;
import static com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperItems.FeedEntry.DATE;
import static com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperItems.FeedEntry.IS_IN_BAG;
import static com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperItems.FeedEntry.TABLE_NAME;
import static com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_TITLE;


public class FeedReaderDbHelperItems extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "items_smartbag.db";
    private final static String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedEntry.COLUMN_SUBJECT_TITLE + " TEXT,"+
                    FeedEntry.IS_IN_BAG + " TEXT,"+
                    FeedEntry.DATE + " TEXT)";

    public FeedReaderDbHelperItems(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
       // db.execSQL("PRAGMA schema.user_version = "+ MainActivity.userId);
        db.execSQL(SQL_CREATE_ENTRIES);
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
        public static final String IS_IN_BAG = "inBag";
        public static final String DATE = "date";



    }
    public static List<String> getContent(Context context, final String subjectName){
        FeedReaderDbHelperItems dbHelperForItem = new FeedReaderDbHelperItems(context);
        SQLiteDatabase dbForItem = dbHelperForItem.getReadableDatabase();

        try {
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
                    TABLE_NAME,   // The table to query
                    projectionItem,             // The array of columns to return (pass null to get all)
                    selectionItem,              // The columns for the WHERE clause
                    selectionArgsItem,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrderItem               // The sort order
            );
            List<String> subjectNames = new ArrayList<>();
            while (cursorItem.moveToNext()) {
                String subject = cursorItem.getString(
                        cursorItem.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
                subjectNames.add(subject);
            }
            cursorItem.close();

            return subjectNames;
        }catch (Exception e){
            System.err.println("1");
            System.err.println(e.toString());
            MainActivity.showAlert(context,context.getResources().getString(R.string.ERROR),context.getResources().getString(R.string.databaseError));
            return new ArrayList<>();
        }finally {
            dbForItem.close();
            dbHelperForItem.close();
        }
    }

    public static boolean write(Context context, Intent intent, final List<Item> defaultItemsDataItemsToAdd){

        // adding to database
        // DataBase work
        FeedReaderDbHelperItems dbHelperForItems = new FeedReaderDbHelperItems(context);
        // Gets the data repository in write mode
        SQLiteDatabase dbForItems = dbHelperForItems.getWritableDatabase();
        // date for snacks
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        // Create a new map of values, where column names are the keys
        try {
            // adding data to table
            for (Item item : defaultItemsDataItemsToAdd) {
                ContentValues valuesForItems = new ContentValues();
                valuesForItems.put(FeedEntry.COLUMN_NAME_TITLE, item.getItemName());
                valuesForItems.put(FeedEntry.COLUMN_SUBJECT_TITLE, item.getSubjectName());
                if ("null".equals(String.valueOf(intent.getSerializableExtra("putInToBag")))) {
                    valuesForItems.put(FeedEntry.IS_IN_BAG, String.valueOf(item.isInBag()));
                } else {
                    valuesForItems.put(FeedEntry.IS_IN_BAG, String.valueOf(intent.getSerializableExtra("putInToBag")));
                }
                if(item.getSubjectName().equals("snack")) {
                    valuesForItems.put(FeedEntry.DATE, dateFormat.format(date));
                }else {
                    valuesForItems.put(FeedEntry.DATE, "-");
                }
                // Insert the new row, returning the primary key value of the new row
                if (dbForItems.insert(TABLE_NAME, null, valuesForItems) < 0) {
                    dbHelperForItems.close();
                    dbForItems.close();
                    return true;
                }
            }
            return false;
        }catch (Exception e){

            System.err.println(e.toString());
                return true;
        }finally {
            dbHelperForItems.close();
            dbForItems.close();
        }
        // the method was successful


    } public static boolean write(Context context, Intent intent, final List<Item> defaultItemsDataItemsToAdd, String subjectName){

        // adding to database
        // DataBase work
        FeedReaderDbHelperItems dbHelperForItems = new FeedReaderDbHelperItems(context);
        // Gets the data repository in write mode
        SQLiteDatabase dbForItems = dbHelperForItems.getWritableDatabase();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        try{
        // Create a new map of values, where column names are the keys

        // adding data to table

        for (Item item : defaultItemsDataItemsToAdd) {
            item.setSubjectName(subjectName);
            ContentValues valuesForItems = new ContentValues();
            valuesForItems.put(FeedEntry.COLUMN_NAME_TITLE, item.getItemName());
            valuesForItems.put(FeedEntry.COLUMN_SUBJECT_TITLE, item.getSubjectName());
            if("null".equals(String.valueOf(intent.getSerializableExtra("putInToBag")))) {
                valuesForItems.put(FeedEntry.IS_IN_BAG, String.valueOf(item.isInBag()));
            }else{
                valuesForItems.put(FeedEntry.IS_IN_BAG, String.valueOf(intent.getSerializableExtra("putInToBag")));
            }
            if(item.getSubjectName().equals("snack")) {
                valuesForItems.put(FeedEntry.DATE, dateFormat.format(date));
            }else {
                valuesForItems.put(FeedEntry.DATE, "-");
            }
            // Insert the new row, returning the primary key value of the new row
            if (dbForItems.insert(TABLE_NAME, null, valuesForItems)<0){
                return true;
            }
        }
        // the method was successful
        return false;
        }catch (Exception e){
            System.err.println(e.toString());
            return true;
        }finally {
            dbHelperForItems.close();
            dbForItems.close();
        }

    }

    public static boolean isInBag(Context context, String itemName, String subject, String type){
        FeedReaderDbHelperItems dbHelperForItem = new FeedReaderDbHelperItems(context);
        SQLiteDatabase dbForItem = dbHelperForItem.getReadableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where " + IS_IN_BAG + " = " + "'true'"+ " AND "+ COLUMN_NAME_TITLE + " = " + "'"+itemName+"'";
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        if(type.equals("snack")){
            SQLiteDatabase dbForItems = dbHelperForItem.getWritableDatabase();
            String queryItems =
                    "UPDATE "+ TABLE_NAME+" SET "+
                            DATE+" = "+"'"+ dateFormat.format(date) + SharedPrefs.getInt(context, SharedPrefs.SPINNER) +"'"+
                            " WHERE "+ FeedEntry.COLUMN_NAME_TITLE+" = "+"'"+itemName+"' AND "+
                            FeedEntry.COLUMN_SUBJECT_TITLE+" = "+"'"+subject+"'";

            dbForItems.execSQL(queryItems);
        }
        Cursor cursor = dbForItem.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }

    public static List<String[]> getItemsInBag(Context context){
        FeedReaderDbHelperItems dbHelperForItem = new FeedReaderDbHelperItems(context);
        SQLiteDatabase dbForItem = dbHelperForItem.getReadableDatabase();
        try {
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            // subset is initialized in switch statement
            String selectionItem = "SELECT * FROM " + FeedEntry.TABLE_NAME + " WHERE "
                    + FeedReaderDbHelperItems.FeedEntry.IS_IN_BAG + " = " + "'true'";

            Cursor cursorItem = dbForItem.rawQuery(selectionItem, null);

            // get all the subjects that have today marked as true
            List<String[]> subjectNames = new ArrayList<>();
            while (cursorItem.moveToNext()) {
                String item = cursorItem.getString(
                        cursorItem.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
                String subject = cursorItem.getString(
                        cursorItem.getColumnIndexOrThrow(FeedEntry.COLUMN_SUBJECT_TITLE));

                if(FeedReaderDbHelperSubjects.getType(context,subject).equals("snack")&&!getDate(context, subject, item).equals(dateFormat.format(date) + SharedPrefs.getInt(context, SharedPrefs.SPINNER))){
                    editBag(context,new Item(item,subject,true,"snack", context),false);
                }

                subjectNames.add(new String[]{item, subject});


            }
            cursorItem.close();
            return subjectNames;

        }catch (Exception e){
            MainActivity.showAlert(context,context.getResources().getString(R.string.ERROR),context.getResources().getString(R.string.databaseError));
            return new ArrayList<>();
        }finally {
            dbHelperForItem.close();
            dbForItem.close();
        }
    }

    public static void editBag(Context context, Item item, boolean add){
        FeedReaderDbHelperItems dbHelperItems = new FeedReaderDbHelperItems(context);
        SQLiteDatabase dbForItems = dbHelperItems.getWritableDatabase();
        try {

            String queryItems =
                    "UPDATE "+ TABLE_NAME+" SET "+
                            FeedReaderDbHelperItems.FeedEntry.IS_IN_BAG+" = "+"'"+add+"'"+
                            " WHERE "+ FeedEntry.COLUMN_NAME_TITLE+" = "+"'"+item.getItemName()+"' AND "+
                            FeedEntry.COLUMN_SUBJECT_TITLE+" = "+"'"+item.getSubjectName()+"'";

            dbForItems.execSQL(queryItems);

            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            queryItems =
                    "UPDATE "+ TABLE_NAME+" SET "+
                            DATE +" = "+"'"+dateFormat.format(date)+SharedPrefs.getInt(context, SharedPrefs.SPINNER)+"'"+
                            " WHERE "+ FeedEntry.COLUMN_NAME_TITLE+" = "+"'"+item.getItemName()+"' AND "+
                            FeedEntry.COLUMN_SUBJECT_TITLE+" = "+"'"+item.getSubjectName()+"'";

            dbForItems.execSQL(queryItems);
        }catch (Exception e){
            MainActivity.showAlert(context,context.getResources().getString(R.string.ERROR),context.getResources().getString(R.string.databaseError));
        }finally {
            dbHelperItems.close();
            dbForItems.close();
        }

    }





    public static void deleteSubject(Context context, String subjectName) throws android.database.sqlite.SQLiteException{
        FeedReaderDbHelperItems dbHelperItems = new FeedReaderDbHelperItems(context);
        SQLiteDatabase dbForItems = dbHelperItems.getWritableDatabase();

        try {
            String queryItems =
                    " DELETE FROM " + TABLE_NAME + " WHERE " + FeedReaderDbHelperItems.FeedEntry.COLUMN_SUBJECT_TITLE + " = " + "'" + subjectName + "'";

            dbForItems.execSQL(queryItems);

            dbHelperItems.close();
            dbForItems.close();
        }catch (Exception e){
            MainActivity.showAlert(context,context.getResources().getString(R.string.ERROR),context.getResources().getString(R.string.databaseError));
        }finally {
            dbHelperItems.close();
            dbForItems.close();
        }

    }

    public static void deleteItem(Context context, Item itemName){
        FeedReaderDbHelperItems dbHelperItems = new FeedReaderDbHelperItems(context);
        SQLiteDatabase dbForItems = dbHelperItems.getWritableDatabase();

        try {

        String queryItems =
                " DELETE FROM "+ TABLE_NAME + " WHERE "+ FeedEntry.COLUMN_NAME_TITLE +" = "+"'"+itemName.getItemName()+"'" + " AND " +
                    FeedEntry.COLUMN_SUBJECT_TITLE + " = " + "'" + itemName.getSubjectName() + "'";

        dbForItems.execSQL(queryItems);

        }catch (Exception e){
            System.err.println(e.toString());
            MainActivity.showAlert(context,context.getResources().getString(R.string.ERROR),context.getResources().getString(R.string.databaseError));
        }finally {
            dbHelperItems.close();
            dbForItems.close();
        }

    }

    public static void edit(Context context, String subjectName, String oldSubjectName) throws android.database.sqlite.SQLiteException{
        FeedReaderDbHelperItems dbHelperItems = new FeedReaderDbHelperItems(context);
        SQLiteDatabase dbForItems = dbHelperItems.getWritableDatabase();

        try {
            String queryItems =
                    "UPDATE " + TABLE_NAME + " SET " +
                            FeedReaderDbHelperItems.FeedEntry.COLUMN_SUBJECT_TITLE + " = " + "'" + subjectName + "'" +
                            " WHERE " + FeedReaderDbHelperItems.FeedEntry.COLUMN_SUBJECT_TITLE + " = " + "'" + oldSubjectName + "'";

            dbForItems.execSQL(queryItems);
        }catch (Exception e){
            System.err.println(e.toString());
            MainActivity.showAlert(context,context.getResources().getString(R.string.ERROR),context.getResources().getString(R.string.databaseError));
        }finally {
            dbHelperItems.close();
            dbForItems.close();
        }

    }
    public static boolean subjectExists(Context context, String subjectName){
        FeedReaderDbHelperItems dbHelperForItem = new FeedReaderDbHelperItems(context);
        SQLiteDatabase dbForItem = dbHelperForItem.getReadableDatabase();
        String Query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_SUBJECT_TITLE + " = " + "'"+subjectName+"'";
        try(Cursor cursor = dbForItem.rawQuery(Query, null)) {
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
            cursor.close();

            return true;
        }catch (Exception e){
            System.err.println(e.toString());
            MainActivity.showAlert(context,context.getResources().getString(R.string.ERROR),context.getResources().getString(R.string.databaseError));
        }finally {
            dbHelperForItem.close();
            dbHelperForItem.close();
        }
        return false;
    }

    public static String getDate(Context context, String subject, String item) {
        FeedReaderDbHelperItems dbHelperForItem = new FeedReaderDbHelperItems(context);
        SQLiteDatabase dbForItem = dbHelperForItem.getReadableDatabase();
        String Query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME_TITLE + " = " + "'"+item+"'" + " AND " + COLUMN_SUBJECT_TITLE + " = "+ "'"+subject+"'" ;
        try (Cursor cursor = dbForItem.rawQuery(Query, null)) {
            cursor.moveToNext();

            return cursor.getString(cursor.getColumnIndexOrThrow(DATE));


        } catch (Exception e) {
            System.err.println(e.toString());
            return "-";
        }finally {
            dbHelperForItem.close();
            dbHelperForItem.close();
        }
    }

}
