package com.ntua.ote.logger.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ntua.ote.logger.CallLogService;
import com.ntua.ote.logger.db.CallLogDbSchema.CallLogEntry;
import com.ntua.ote.logger.models.LogDetails;
import com.ntua.ote.logger.utils.CommonUtils;
import com.ntua.ote.logger.utils.Constants;
import com.ntua.ote.logger.utils.Direction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CallLogDbHelper extends SQLiteOpenHelper {

    private static CallLogDbHelper sInstance;
    private CallLogService service;

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "logger.db";
    public static final String TAG = CallLogDbHelper.class.getName();

    public static String selection = CallLogEntry.COLUMN_NAME_ID + " = ?";

    public static synchronized CallLogDbHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new CallLogDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private CallLogDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CallLogDbSchema.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CallLogDbSchema.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long insert(LogDetails logDetails){
        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = -1;
        try {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(CallLogEntry.COLUMN_NAME_EXT_NUM, logDetails.getExternalNumber());
            DateFormat df = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
            String strDate = df.format(logDetails.getDateTime());
            values.put(CallLogEntry.COLUMN_NAME_DATE, strDate);
            values.put(CallLogEntry.COLUMN_NAME_DIRECTION, logDetails.getDirection().code);

            // Insert the new row, returning the primary key value of the new row
            newRowId = db.insert(
                    CallLogEntry.TABLE_NAME,
                    null, values);

            service.dbUpdated(1);
        }catch (Exception e){
            Log.e(TAG, "<insert>" + e.getMessage());
        } finally {
            db.close();
        }
        return newRowId;
    }

    public long getCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        long count = 0;
        try {
            count = DatabaseUtils.queryNumEntries(db, CallLogEntry.TABLE_NAME);
        }catch (Exception e){
            Log.d(TAG, "<getCount>" + e.getMessage());
        } finally {
            db.close();
        }
        return count;
    }

    public Cursor getDataForList(String[] projection, String selection){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(
                CallLogEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                null,                       // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                CallLogEntry.COLUMN_NAME_DATE + " DESC"                                      // The sort order
        );

        return c;
    }

    public LogDetails getAllData(Long id){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = { id + "" };
        Cursor c = db.query(
                CallLogEntry.TABLE_NAME,
                CallLogDbSchema.WHOLE_PROJECTION,
                selection,
                selectionArgs,                       // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );
        c.moveToFirst();
        return convertCursorToModel(c);
    }

    public int update(int duration, long id){
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        try {
            // New value for one column
            ContentValues values = new ContentValues();
            values.put(CallLogEntry.COLUMN_NAME_DURATION, duration);

            // Which row to update, based on the ID
            String[] selectionArgs = { id + "" };

            count = db.update(
                    CallLogEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }catch (Exception e){
            Log.d(TAG, "<update>" + e.getMessage());
        } finally {
            db.close();
        }
        return count == 0 ? -1 : 1;
    }

    public int update(double latitude, double longitude, long id){
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        try {
            // New value for one column
            ContentValues values = new ContentValues();
            values.put(CallLogEntry.COLUMN_NAME_LATITUDE, latitude);
            values.put(CallLogEntry.COLUMN_NAME_LONGITUDE, longitude);

            // Which row to update, based on the ID

            String[] selectionArgs = { id + "" };

            count = db.update(
                    CallLogEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }catch (Exception e){
            Log.d(TAG, "<update>" + e.getMessage());
        } finally {
            db.close();
        }
        return count == 0 ? -1 : 1;
    }

    private LogDetails convertCursorToModel(Cursor cursor){
        if (!cursor.isAfterLast()) {
            Date date = CommonUtils.stringToDate(cursor.getString(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_DATE)));
            Direction direction = Direction.parseCode(cursor.getInt(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_DIRECTION)));
            return new LogDetails(
                    cursor.getString(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_EXT_NUM)),
                    date,
                    cursor.getInt(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_DURATION)),
                    direction,
                    cursor.getDouble(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_LONGITUDE))
            );
        }
        return null;
    }


    public void setService(CallLogService service) {
        this.service = service;
    }
}
