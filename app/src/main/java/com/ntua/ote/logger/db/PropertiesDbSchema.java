package com.ntua.ote.logger.db;

import android.provider.BaseColumns;

public final  class PropertiesDbSchema {

    public PropertiesDbSchema () {}

    public static abstract class PropertiesEntry implements BaseColumns {
        public static final String TABLE_NAME = "properties";
        public static final String COLUMN_NAME_CODE = "code";
        public static final String COLUMN_NAME_VALUE = "value";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PropertiesEntry.TABLE_NAME + " (" +
                    PropertiesEntry.COLUMN_NAME_CODE + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    PropertiesEntry.COLUMN_NAME_VALUE + TEXT_TYPE + ")";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PropertiesEntry.TABLE_NAME;

}
