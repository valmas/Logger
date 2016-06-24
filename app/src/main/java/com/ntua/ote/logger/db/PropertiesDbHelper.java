package com.ntua.ote.logger.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PropertiesDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "logger.db";
    public static final String TAG = PropertiesDbHelper.class.getName();

    public PropertiesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PropertiesDbSchema.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PropertiesDbSchema.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int insert(String code, String value){
        String response = getValue(code);
        if(response == null) {
            SQLiteDatabase db = this.getWritableDatabase();
            long newRowId = -1;
            try {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(PropertiesDbSchema.PropertiesEntry.COLUMN_NAME_CODE, code);
            values.put(PropertiesDbSchema.PropertiesEntry.COLUMN_NAME_VALUE, value);

            // Insert the new row, returning the primary key value of the new row
            newRowId = db.insert(
                    PropertiesDbSchema.PropertiesEntry.TABLE_NAME,
                    null, values);

            }catch (Exception e){
                Log.d(TAG, "<insert>" + e.getMessage());
            } finally {
                db.close();
            }
            return newRowId == -1 ? -1 : 1;
        } else {
            return update(code, value);
        }

    }

    public String getValue(String code){
        SQLiteDatabase db = this.getReadableDatabase();
        String value = null;
        try {
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    PropertiesDbSchema.PropertiesEntry.COLUMN_NAME_VALUE};

            // How you want the results sorted in the resulting Cursor
            String selection = PropertiesDbSchema.PropertiesEntry.COLUMN_NAME_CODE + "= ?";

            Cursor c = db.query(
                    PropertiesDbSchema.PropertiesEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    new String[]{code},                       // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // The sort order
            );

            c.moveToFirst();

            if (!c.isAfterLast()) {
                value = c.getString(c.getColumnIndexOrThrow(PropertiesDbSchema.PropertiesEntry.COLUMN_NAME_VALUE));
            }
            c.close();
        }catch (Exception e){
            Log.d(TAG, "<getValue>" + e.getMessage());
        } finally {
            db.close();
        }

        return value;
    }

    public int update(String code, String value){
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        try {
            // New value for one column
            ContentValues values = new ContentValues();
            values.put(PropertiesDbSchema.PropertiesEntry.COLUMN_NAME_VALUE, value);

            // Which row to update, based on the ID
            String selection = PropertiesDbSchema.PropertiesEntry.COLUMN_NAME_CODE + " = ?";
            String[] selectionArgs = { code };

            count = db.update(
                    PropertiesDbSchema.PropertiesEntry.TABLE_NAME,
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
}
