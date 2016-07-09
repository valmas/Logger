package com.ntua.ote.logger.db;

import android.provider.BaseColumns;

public final  class CallLogDbSchema {

    public CallLogDbSchema() {}

    public static abstract class CallLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "callLog";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_EXT_NUM = "externalNumber";
        public static final String COLUMN_NAME_DATE = "dateTime";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_DIRECTION = "direction";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CallLogEntry.TABLE_NAME + " (" +
                    CallLogEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_EXT_NUM + TEXT_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_DATE  + " DATETIME" + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_DURATION + " INTEGER" + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_DIRECTION + " INTEGER" + COMMA_SEP +  // 0: incoming 1: outgoing
                    CallLogEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE + ")";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CallLogEntry.TABLE_NAME;

    public static final String[] WHOLE_PROJECTION = new String[] {
            CallLogEntry.COLUMN_NAME_ID,
            CallLogEntry.COLUMN_NAME_EXT_NUM,
            CallLogEntry.COLUMN_NAME_DATE,
            CallLogEntry.COLUMN_NAME_DURATION,
            CallLogEntry.COLUMN_NAME_DIRECTION,
            CallLogEntry.COLUMN_NAME_LATITUDE,
            CallLogEntry.COLUMN_NAME_LONGITUDE};

}
