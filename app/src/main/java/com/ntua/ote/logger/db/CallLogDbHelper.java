package com.ntua.ote.logger.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ntua.ote.logger.LogService;
import com.ntua.ote.logger.db.CallLogDbSchema.CallLogEntry;
import com.ntua.ote.logger.db.CallLogDbSchema.PendingRequestEntry;
import com.ntua.ote.logger.models.LogDetails;
import com.ntua.ote.logger.utils.CommonUtils;
import com.ntua.ote.logger.utils.Constants;
import com.ntua.ote.logger.utils.Direction;
import com.ntua.ote.logger.utils.LogType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CallLogDbHelper extends SQLiteOpenHelper {

    private static CallLogDbHelper sInstance;
    private LogService service;

    public static final int DATABASE_VERSION = 14;
    public static final String DATABASE_NAME = "logger.db";
    public static final String TAG = CallLogDbHelper.class.getName();

    public static String selection = CallLogEntry.COLUMN_NAME_ID + " = ?";
    public static String selectionByType = CallLogEntry.COLUMN_NAME_TYPE + " = ?";

    public static final String[] LIST_PROJECTION = new String[] {
            CallLogDbSchema.CallLogEntry.COLUMN_NAME_ID,
            CallLogDbSchema.CallLogEntry.COLUMN_NAME_EXT_NUM,
            CallLogDbSchema.CallLogEntry.COLUMN_NAME_DATE,
            CallLogDbSchema.CallLogEntry.COLUMN_NAME_DURATION,
            CallLogDbSchema.CallLogEntry.COLUMN_NAME_DIRECTION};

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
        db.execSQL(CallLogDbSchema.SQL_CREATE_PENDING_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CallLogDbSchema.SQL_DELETE_ENTRIES);
        db.execSQL(CallLogDbSchema.SQL_DELETE_PENDING_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long insert(LogDetails logDetails){
        long newRowId = -1;
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(CallLogEntry.COLUMN_NAME_EXT_NUM, logDetails.getExternalNumber());
            DateFormat df = new SimpleDateFormat(Constants.DATE_TIME_FORMAT, Locale.US);
            String strDate = df.format(logDetails.getDateTime());
            values.put(CallLogEntry.COLUMN_NAME_DATE, strDate);
            values.put(CallLogEntry.COLUMN_NAME_DIRECTION, logDetails.getDirection().code);
            values.put(CallLogEntry.COLUMN_NAME_TYPE, logDetails.getType().code);
            values.put(CallLogEntry.COLUMN_NAME_SMS_CONTENT, logDetails.getSmsContent());
            values.put(CallLogEntry.COLUMN_NAME_CELL_ID, logDetails.getCellId());
            values.put(CallLogEntry.COLUMN_NAME_LAC, logDetails.getLac());
            values.put(CallLogEntry.COLUMN_NAME_RAT, logDetails.getRat());
            values.put(CallLogEntry.COLUMN_NAME_MNC, logDetails.getMnc());
            values.put(CallLogEntry.COLUMN_NAME_MCC, logDetails.getMcc());
            values.put(CallLogEntry.COLUMN_NAME_RSSI, logDetails.getRssi());
            values.put(CallLogEntry.COLUMN_NAME_LTE_RSRP, logDetails.getLTE_rsrp());
            values.put(CallLogEntry.COLUMN_NAME_LTE_RSRQ, logDetails.getLTE_rsrq());
            values.put(CallLogEntry.COLUMN_NAME_LTE_RSSNR, logDetails.getLTE_rssnr());
            values.put(CallLogEntry.COLUMN_NAME_LTE_CQI, logDetails.getLTE_cqi());

            // Insert the new row, returning the primary key value of the new row
            newRowId = db.insert(CallLogEntry.TABLE_NAME, null, values);

            service.dbUpdated(1);
        } catch (Exception e) {
            Log.e(TAG, "<insert>" + e.getMessage());
        }
        return newRowId;
    }

    public long getCallCount(){
        long count = 0;
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            count = DatabaseUtils.queryNumEntries(db, CallLogEntry.TABLE_NAME, selectionByType, new String[]{"0"});
        } catch (Exception e) {
            Log.d(TAG, "<getCount>" + e.getMessage());
        }
        return count;
    }

    public long getSmsCount(){
        long count = 0;
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            count = DatabaseUtils.queryNumEntries(db, CallLogEntry.TABLE_NAME, selectionByType, new String[]{"1"});
        } catch (Exception e) {
            Log.d(TAG, "<getCount>" + e.getMessage());
        }
        return count;
    }

    public Cursor getDataForList(String[] projection, String selection, String[] args){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            return db.query(CallLogEntry.TABLE_NAME, projection, selection, args, null, null,
                CallLogEntry.COLUMN_NAME_DATE + " DESC");
        } catch (Exception e) {
            Log.d(TAG, "<getDataForList>" + e.getMessage());
        }
        return null;
    }

    public LogDetails getAllData(Long id){
        LogDetails logDetails = null;
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String[] selectionArgs = { id + "" };
            Cursor c = db.query(CallLogEntry.TABLE_NAME, CallLogDbSchema.WHOLE_PROJECTION, selection,
                    selectionArgs, null, null, null);
            c.moveToFirst();
            logDetails = convertCursorToModel(c);
            c.close();
        } catch (Exception e) {
            Log.d(TAG, "<getAllData>" + e.getMessage());
        }
        return logDetails;
    }

    public int update(int duration, long id){
        int count = 0;
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(CallLogEntry.COLUMN_NAME_DURATION, duration);
            String[] selectionArgs = {id + ""};
            count = db.update(CallLogEntry.TABLE_NAME, values, selection, selectionArgs);
        } catch (Exception e) {
            Log.d(TAG, "<update>" + e.getMessage());
        }
        return count == 0 ? -1 : 1;
    }

    public int update(double latitude, double longitude, long id){
        int count = 0;
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(CallLogEntry.COLUMN_NAME_LATITUDE, latitude);
            values.put(CallLogEntry.COLUMN_NAME_LONGITUDE, longitude);
            String[] selectionArgs = {id + ""};
            count = db.update(CallLogEntry.TABLE_NAME, values, selection, selectionArgs);
        } catch (Exception e) {
            Log.d(TAG, "<update>" + e.getMessage());
        }
        return count == 0 ? -1 : 1;
    }

    private LogDetails convertCursorToModel(Cursor cursor){
        if (!cursor.isAfterLast()) {
            Date date = CommonUtils.stringToDate(cursor.getString(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_DATE)));
            Direction direction = Direction.parseCode(cursor.getInt(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_DIRECTION)));
            LogType type = LogType.parseCode(cursor.getInt(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_TYPE)));
            return new LogDetails(
                    type,
                    cursor.getString(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_EXT_NUM)),
                    date,
                    cursor.getInt(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_DURATION)),
                    cursor.getString(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_SMS_CONTENT)),
                    direction,
                    cursor.getDouble(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_LONGITUDE)),
                    cursor.getInt(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_CELL_ID)),
                    cursor.getInt(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_LAC)),
                    cursor.getString(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_RAT)),
                    cursor.getInt(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_MNC)),
                    cursor.getInt(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_MCC)),
                    cursor.getInt(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_RSSI)),
                    cursor.getString(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_LTE_RSRP)),
                    cursor.getString(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_LTE_RSRQ)),
                    cursor.getString(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_LTE_RSSNR)),
                    cursor.getString(cursor.getColumnIndex(CallLogEntry.COLUMN_NAME_LTE_CQI))
            );
        }
        return null;
    }

    public long getRemoteId(long id){
        String[] selectionArgs = { id + "" };
        long remoteId = -1;
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            Cursor c = db.query(CallLogEntry.TABLE_NAME, new String[]{CallLogEntry.COLUMN_NAME_REMOTE_ID},
                    selection, selectionArgs, null, null, null);
            c.moveToFirst();
            remoteId = c.isAfterLast() ? -1 : c.getLong(c.getColumnIndex(CallLogEntry.COLUMN_NAME_REMOTE_ID));
            c.close();
        } catch (Exception e) {
            Log.d(TAG, "<getCount>" + e.getMessage());
        }
        return remoteId;
    }

    public long setRemoteId(long id, long remoteId){
        int count = 0;
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(CallLogEntry.COLUMN_NAME_REMOTE_ID, remoteId);
            String[] selectionArgs = {id + ""};
            count = db.update(CallLogEntry.TABLE_NAME, values, selection, selectionArgs);
        } catch (Exception e) {
            Log.d(TAG, "<update>" + e.getMessage());
        }
        return count == 0 ? -1 : 1;
    }

    public long insertPending(String initial, String location, String duration){
        long newRowId = -1;
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(PendingRequestEntry.COLUMN_NAME_INITIAL, initial);
            values.put(PendingRequestEntry.COLUMN_NAME_LOCATION, location);
            values.put(PendingRequestEntry.COLUMN_NAME_DURATION, duration);
            newRowId = db.insert(PendingRequestEntry.TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e(TAG, "<insert pending>" + e.getMessage());
        }
        return newRowId;
    }

    public String getPending(String requestEntry){
        String pending = null;
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            Cursor c = db.query(PendingRequestEntry.TABLE_NAME, new String[]{requestEntry},
                    null, null, null, null, null);
            c.moveToFirst();
            pending = c.isAfterLast() ? null : c.getString(c.getColumnIndex(requestEntry));
            c.close();
        } catch (Exception e) {
            Log.d(TAG, "<getInitial>" + e.getMessage());
        }
        return pending;
    }

    public long deletePending(){
        long newRowId = -1;
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            newRowId = db.delete(PendingRequestEntry.TABLE_NAME, null, null);
        } catch (Exception e) {
            Log.e(TAG, "<delete pending>" + e.getMessage());
        }
        return newRowId;
    }

    public void setService(LogService service) {
        this.service = service;
    }
}
