package com.ntua.ote.logger.db;

import android.provider.BaseColumns;

public final  class CallLogDbSchema {

    public CallLogDbSchema() {}

    public static abstract class CallLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "log";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_EXT_NUM = "externalNumber";
        public static final String COLUMN_NAME_DATE = "dateTime";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_DIRECTION = "direction";
        public static final String COLUMN_NAME_SMS_CONTENT = "smsContent";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_CELL_ID = "cellId";
        public static final String COLUMN_NAME_LAC = "lac";
        public static final String COLUMN_NAME_RAT = "rat";
        public static final String COLUMN_NAME_MNC = "mnc";
        public static final String COLUMN_NAME_MCC = "mcc";
        public static final String COLUMN_NAME_RSSI = "rssi";
        public static final String COLUMN_NAME_LTE_RSRP = "rsrp";
        public static final String COLUMN_NAME_LTE_RSRQ = "rsrq";
        public static final String COLUMN_NAME_LTE_RSSNR = "rssnr";
        public static final String COLUMN_NAME_LTE_CQI = "cqi";
        public static final String COLUMN_NAME_REMOTE_ID = "remoteId";

    }

    public static abstract class PendingRequestEntry implements BaseColumns {
        public static final String TABLE_NAME = "pendingRequests";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_INITIAL = "initial";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_DURATION = "duration";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CallLogEntry.TABLE_NAME + " (" +
                    CallLogEntry.COLUMN_NAME_ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_TYPE + INTEGER_TYPE + COMMA_SEP +      // 0: calls 1: sms
                    CallLogEntry.COLUMN_NAME_EXT_NUM + TEXT_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_DATE  + " DATETIME" + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_DURATION + INTEGER_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_DIRECTION + INTEGER_TYPE + COMMA_SEP +  // 0: incoming 1: outgoing
                    CallLogEntry.COLUMN_NAME_SMS_CONTENT + TEXT_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_CELL_ID + INTEGER_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_LAC + INTEGER_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_RAT + TEXT_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_MNC + INTEGER_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_MCC + INTEGER_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_RSSI + INTEGER_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_LTE_RSRP + TEXT_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_LTE_RSRQ + TEXT_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_LTE_RSSNR + TEXT_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_LTE_CQI + TEXT_TYPE + COMMA_SEP +
                    CallLogEntry.COLUMN_NAME_REMOTE_ID + INTEGER_TYPE +")";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CallLogEntry.TABLE_NAME;

    public static final String SQL_CREATE_PENDING_ENTRIES =
            "CREATE TABLE " + PendingRequestEntry.TABLE_NAME + " (" +
                    PendingRequestEntry.COLUMN_NAME_ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    PendingRequestEntry.COLUMN_NAME_INITIAL + TEXT_TYPE + COMMA_SEP +
                    PendingRequestEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    PendingRequestEntry.COLUMN_NAME_DURATION + TEXT_TYPE + ")";

    public static final String SQL_DELETE_PENDING_ENTRIES =
            "DROP TABLE IF EXISTS " + PendingRequestEntry.TABLE_NAME;

    public static final String[] WHOLE_PROJECTION = new String[] {
            CallLogEntry.COLUMN_NAME_ID,
            CallLogEntry.COLUMN_NAME_TYPE,
            CallLogEntry.COLUMN_NAME_EXT_NUM,
            CallLogEntry.COLUMN_NAME_DATE,
            CallLogEntry.COLUMN_NAME_DURATION,
            CallLogEntry.COLUMN_NAME_DIRECTION,
            CallLogEntry.COLUMN_NAME_SMS_CONTENT,
            CallLogEntry.COLUMN_NAME_LATITUDE,
            CallLogEntry.COLUMN_NAME_LONGITUDE,
            CallLogEntry.COLUMN_NAME_CELL_ID,
            CallLogEntry.COLUMN_NAME_LAC,
            CallLogEntry.COLUMN_NAME_RAT,
            CallLogEntry.COLUMN_NAME_MNC,
            CallLogEntry.COLUMN_NAME_MCC,
            CallLogEntry.COLUMN_NAME_RSSI,
            CallLogEntry.COLUMN_NAME_LTE_RSRP,
            CallLogEntry.COLUMN_NAME_LTE_RSRQ,
            CallLogEntry.COLUMN_NAME_LTE_RSSNR,
            CallLogEntry.COLUMN_NAME_LTE_CQI};
    
}
