package com.example.ontime.DataBaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_FRIDAY;
import static com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_MONDAY;
import static com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_SATURDAY;
import static com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_SUNDAY;
import static com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_THURSDAY;
import static com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_TITLE;
import static com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_TUESDAY;
import static com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_WEDNESDAY;
import static com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects.FeedEntry.TABLE_NAME;

public class FeedReaderDbHelperSubjects extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Subject.db";
    private final static String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                FeedEntry._ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_TITLE + " TEXT," +
                FeedEntry.COLUMN_NAME_MONDAY + " TEXT,"+
                FeedEntry.COLUMN_NAME_TUESDAY + " TEXT,"+
                FeedEntry.COLUMN_NAME_WEDNESDAY + " TEXT,"+
                FeedEntry.COLUMN_NAME_THURSDAY + " TEXT,"+
                FeedEntry.COLUMN_NAME_FRIDAY + " TEXT,"+
                FeedEntry.COLUMN_NAME_SATURDAY + " TEXT,"+
                FeedEntry.COLUMN_NAME_SUNDAY + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public FeedReaderDbHelperSubjects(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public static void deleteSubject(String subjectName, Context context) throws android.database.sqlite.SQLiteException{
        FeedReaderDbHelperSubjects dbHelperForSubject = new FeedReaderDbHelperSubjects(context);

        // Gets the data repository in write mode
        SQLiteDatabase dbForSubject = dbHelperForSubject.getWritableDatabase();

        String querySubject =
                "DELETE FROM "+TABLE_NAME+ " WHERE "+ COLUMN_NAME_TITLE+" = "+"'"+subjectName+"'";
        FeedReaderDbHelperItems.deleteSubject(context, subjectName); // delete all items of this subject

        dbForSubject.execSQL(querySubject);

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
    public static String[] getDaysOfSubjects(Context context, String subjectName){
        FeedReaderDbHelperSubjects dbHelperForSubject = new FeedReaderDbHelperSubjects(context);
        SQLiteDatabase dbForSubject = dbHelperForSubject.getReadableDatabase();

        final String[] projectionSubject = {
                FeedEntry.COLUMN_NAME_MONDAY,
                FeedEntry.COLUMN_NAME_TUESDAY,
                FeedEntry.COLUMN_NAME_WEDNESDAY,
                FeedEntry.COLUMN_NAME_THURSDAY,
                FeedEntry.COLUMN_NAME_FRIDAY,
                FeedEntry.COLUMN_NAME_SATURDAY,
                FeedEntry.COLUMN_NAME_SUNDAY
        };
        // subset is initialized in switch statement
        String selectionSubject =  COLUMN_NAME_TITLE + " = ?";
        final String[] selectionArgsSubject = {subjectName};

        // How you want the results sorted in the resulting Cursor

        Cursor cursorSubject = dbForSubject.query(
                TABLE_NAME,   // The table to query
                projectionSubject,             // The array of columns to return (pass null to get all)
                selectionSubject,              // The columns for the WHERE clause
                selectionArgsSubject,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        String[] daysValue = new String[]{};
        if (cursorSubject.moveToFirst()) {
            daysValue = new String[]{(cursorSubject.getString(cursorSubject.getColumnIndex(FeedEntry.COLUMN_NAME_MONDAY))),
                    (cursorSubject.getString(cursorSubject.getColumnIndex(FeedEntry.COLUMN_NAME_TUESDAY))),
                    (cursorSubject.getString(cursorSubject.getColumnIndex(FeedEntry.COLUMN_NAME_WEDNESDAY))),
                    (cursorSubject.getString(cursorSubject.getColumnIndex(FeedEntry.COLUMN_NAME_THURSDAY))),
                    (cursorSubject.getString(cursorSubject.getColumnIndex(FeedEntry.COLUMN_NAME_FRIDAY))),
                    (cursorSubject.getString(cursorSubject.getColumnIndex(FeedEntry.COLUMN_NAME_SATURDAY))),
                    (cursorSubject.getString(cursorSubject.getColumnIndex(FeedEntry.COLUMN_NAME_SUNDAY)))
            };

        }

        return daysValue;
    }


    public static List<List<String>> getContent(Context context, boolean getAll){
        FeedReaderDbHelperSubjects dbHelperForSubject = new FeedReaderDbHelperSubjects(context);
        SQLiteDatabase dbForSubject = dbHelperForSubject.getReadableDatabase();
        List<List<String>> subjectNames = new ArrayList<>();

        if(!getAll) {
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            final String[] projectionSubject = {
                    BaseColumns._ID,
                    COLUMN_NAME_TITLE,
                    FeedEntry.COLUMN_NAME_MONDAY,
                    FeedEntry.COLUMN_NAME_TUESDAY,
                    FeedEntry.COLUMN_NAME_WEDNESDAY,
                    FeedEntry.COLUMN_NAME_THURSDAY,
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
                    COLUMN_NAME_TITLE + " DESC";

            Cursor cursorSubject = dbForSubject.query(
                    TABLE_NAME,   // The table to query
                    projectionSubject,             // The array of columns to return (pass null to get all)
                    selectionSubject,              // The columns for the WHERE clause
                    selectionArgsSubject,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrderSubject               // The sort order
            );

            // get all the subjects that have today marked as true
            while (cursorSubject.moveToNext()) {
                List<String> subject = new ArrayList<>();
                subject.add(cursorSubject.getString(cursorSubject.getColumnIndexOrThrow(COLUMN_NAME_TITLE)));
                subject.add(cursorSubject.getString(cursorSubject.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_MONDAY)));
                subject.add(cursorSubject.getString(cursorSubject.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TUESDAY)));
                subject.add(cursorSubject.getString(cursorSubject.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_WEDNESDAY)));
                subject.add(cursorSubject.getString(cursorSubject.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_THURSDAY)));
                subject.add(cursorSubject.getString(cursorSubject.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_FRIDAY)));
                subject.add(cursorSubject.getString(cursorSubject.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SATURDAY)));
                subject.add(cursorSubject.getString(cursorSubject.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUNDAY)));

                subjectNames.add(subject);
            }
            cursorSubject.close();
        }else {
            String selectionItem = "select * from "+ TABLE_NAME;

            Cursor  cursorItem = dbForSubject.rawQuery(selectionItem,null);
            while (cursorItem.moveToNext()) {
                List<String> subject = new ArrayList<>();
               subject.add(cursorItem.getString(cursorItem.getColumnIndexOrThrow(COLUMN_NAME_TITLE)));
                subject.add(cursorItem.getString(cursorItem.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_MONDAY)));
                subject.add(cursorItem.getString(cursorItem.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TUESDAY)));
                subject.add(cursorItem.getString(cursorItem.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_WEDNESDAY)));
                subject.add(cursorItem.getString(cursorItem.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_THURSDAY)));
                subject.add(cursorItem.getString(cursorItem.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_FRIDAY)));
                subject.add(cursorItem.getString(cursorItem.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SATURDAY)));
                subject.add(cursorItem.getString(cursorItem.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUNDAY)));

                subjectNames.add(subject);
            }
            cursorItem.close();

        }

        return subjectNames;
    }

    public static List<List<String>> getContent(Context context, int calendar){ // overloading method fo getting subjects for specific days
        FeedReaderDbHelperSubjects dbHelperForSubject = new FeedReaderDbHelperSubjects(context);
        SQLiteDatabase dbForSubject = dbHelperForSubject.getReadableDatabase();
        List<List<String>> subjectNames = new ArrayList<>();


            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            final String[] projectionSubject = {
                    BaseColumns._ID,
                    COLUMN_NAME_TITLE,
                    FeedEntry.COLUMN_NAME_MONDAY,
                    FeedEntry.COLUMN_NAME_TUESDAY,
                    FeedEntry.COLUMN_NAME_WEDNESDAY,
                    FeedEntry.COLUMN_NAME_THURSDAY,
                    FeedEntry.COLUMN_NAME_FRIDAY,
                    FeedEntry.COLUMN_NAME_SATURDAY,
                    FeedEntry.COLUMN_NAME_SUNDAY
            };

            // subset is initialized in switch statement
            String selectionSubject = null;
            final String[] selectionArgsSubject = {"true"};


            if(calendar==-1){ // -1 is tomorrow
                Calendar calendarDay = Calendar.getInstance();

                switch (calendarDay.getTime().toString().substring(0, 2)) {
                    case "Mo":
                        selectionSubject = FeedEntry.COLUMN_NAME_TUESDAY + " = ?";
                        break;
                    case "Tu":
                        selectionSubject = FeedEntry.COLUMN_NAME_WEDNESDAY + " = ?";
                        break;
                    case "We":
                        selectionSubject = FeedEntry.COLUMN_NAME_THURSDAY + " = ?";
                        break;
                    case "Th":
                        selectionSubject = FeedEntry.COLUMN_NAME_FRIDAY + " = ?";
                        break;
                    case "Fr":
                        selectionSubject = FeedEntry.COLUMN_NAME_SATURDAY + " = ?";
                        break;
                    case "Sa":
                        selectionSubject = FeedEntry.COLUMN_NAME_SUNDAY + " = ?";
                        break;
                    case "Su":
                        selectionSubject = FeedEntry.COLUMN_NAME_MONDAY + " = ?";
                        break;
                }
            }
            else {
                switch (calendar) {
                    case 2:
                        selectionSubject = FeedEntry.COLUMN_NAME_MONDAY + " = ?";
                        break;
                    case 3:
                        selectionSubject = FeedEntry.COLUMN_NAME_TUESDAY + " = ?";
                        break;
                    case 4:
                        selectionSubject = FeedEntry.COLUMN_NAME_WEDNESDAY + " = ?";
                        break;
                    case 5:
                        selectionSubject = FeedEntry.COLUMN_NAME_THURSDAY + " = ?";
                        break;
                    case 6:
                        selectionSubject = FeedEntry.COLUMN_NAME_FRIDAY + " = ?";
                        break;
                    case 7:
                        selectionSubject = FeedEntry.COLUMN_NAME_SATURDAY + " = ?";
                        break;
                    case 8:
                        selectionSubject = FeedEntry.COLUMN_NAME_SUNDAY + " = ?";
                        break;
                }
            }

            // How you want the results sorted in the resulting Cursor
            String sortOrderSubject =
                    COLUMN_NAME_TITLE + " DESC";

            Cursor cursorSubject = dbForSubject.query(
                    TABLE_NAME,   // The table to query
                    projectionSubject,             // The array of columns to return (pass null to get all)
                    selectionSubject,              // The columns for the WHERE clause
                    selectionArgsSubject,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrderSubject               // The sort order
            );

            // get all the subjects that have today marked as true
            while (cursorSubject.moveToNext()) {
                List<String> subject = new ArrayList<>();
                subject.add(cursorSubject.getString(cursorSubject.getColumnIndexOrThrow(COLUMN_NAME_TITLE)));
                subject.add(cursorSubject.getString(cursorSubject.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_MONDAY)));
                subject.add(cursorSubject.getString(cursorSubject.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TUESDAY)));
                subject.add(cursorSubject.getString(cursorSubject.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_WEDNESDAY)));
                subject.add(cursorSubject.getString(cursorSubject.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_THURSDAY)));
                subject.add(cursorSubject.getString(cursorSubject.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_FRIDAY)));
                subject.add(cursorSubject.getString(cursorSubject.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SATURDAY)));
                subject.add(cursorSubject.getString(cursorSubject.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUNDAY)));

                subjectNames.add(subject);
            }
            cursorSubject.close();


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
        valuesForSubject.put(COLUMN_NAME_TITLE, subject);
        valuesForSubject.put(FeedEntry.COLUMN_NAME_MONDAY, (String) intent.getSerializableExtra("Monday"));
        valuesForSubject.put(FeedEntry.COLUMN_NAME_TUESDAY, (String) intent.getSerializableExtra("Tuesday"));
        valuesForSubject.put(FeedEntry.COLUMN_NAME_WEDNESDAY, (String) intent.getSerializableExtra("Wednesday"));
        valuesForSubject.put(FeedEntry.COLUMN_NAME_THURSDAY, (String) intent.getSerializableExtra("Thursday"));
        valuesForSubject.put(FeedEntry.COLUMN_NAME_FRIDAY, (String) intent.getSerializableExtra("Friday"));
        valuesForSubject.put(FeedEntry.COLUMN_NAME_SATURDAY, (String) intent.getSerializableExtra("Saturday"));
        valuesForSubject.put(FeedEntry.COLUMN_NAME_SUNDAY, (String) intent.getSerializableExtra("Sunday"));


        // Insert the new row, returning the primary key value of the new row
       return dbForSubject.insert(TABLE_NAME, null, valuesForSubject)>0;


    }

    public static void edit(Context context, String oldSubjectName, String newSubjectName,
                            boolean Mon, boolean Tue, boolean Wed, boolean Thu, boolean Fri,
                            boolean Sat, boolean Sun) throws android.database.sqlite.SQLiteException{

        // adding to database
        // DataBase work
        FeedReaderDbHelperSubjects dbHelperForSubject = new FeedReaderDbHelperSubjects(context);

        // Gets the data repository in write mode
        SQLiteDatabase dbForSubject = dbHelperForSubject.getWritableDatabase();

        String query =
                "UPDATE "+TABLE_NAME+" SET "+
                COLUMN_NAME_TITLE+" = "+"'"+newSubjectName+"'"+", "+
                COLUMN_NAME_MONDAY+" = "+"'"+Mon +"'"+", "+
                COLUMN_NAME_TUESDAY+" = "+"'"+Tue+"'"+", "+
                COLUMN_NAME_WEDNESDAY+" = "+"'"+Wed+"'"+", "+
                COLUMN_NAME_THURSDAY+" = "+"'"+Thu+"'"+", "+
                COLUMN_NAME_FRIDAY+" = "+"'"+Fri+"'"+", "+
                COLUMN_NAME_SUNDAY+" = "+"'"+Sun+"'"+", "+
                COLUMN_NAME_SATURDAY+" = "+"'"+Sat+"'"+
                " WHERE "+ COLUMN_NAME_TITLE+" = "+"'"+oldSubjectName+"'";


        FeedReaderDbHelperItems.edit(context, newSubjectName, oldSubjectName); // edit all items of this subject
        dbForSubject.execSQL(query);
    }

    public static void edit(Context context, String oldSubjectName, String newSubjectName) throws android.database.sqlite.SQLiteException{

        // adding to database
        // DataBase work
        FeedReaderDbHelperSubjects dbHelperForSubject = new FeedReaderDbHelperSubjects(context);

        // Gets the data repository in write mode
        SQLiteDatabase dbForSubject = dbHelperForSubject.getWritableDatabase();

        String query =
                "UPDATE "+TABLE_NAME+" SET "+
                        COLUMN_NAME_TITLE+" = "+"'"+newSubjectName+"'"+
                        " WHERE "+ COLUMN_NAME_TITLE+" = "+"'"+oldSubjectName+"'";


        FeedReaderDbHelperItems.edit(context, newSubjectName, oldSubjectName); // edit all items of this subject
        dbForSubject.execSQL(query);

    }
}