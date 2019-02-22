package com.example.brainteam;
import android.content.Context;
import android.database.sqlite.*;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class DatabaseManager extends SQLiteOpenHelper
{
    SQLiteDatabase allTossups = openOrCreateDatabase("allTossups.db",null);
    
    private static final String DATABASE_NAME = "allTossups.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "allTossups";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DEPT = "department";
    private static final String COLUMN_JOIN_DATE = "joiningdate";
    private static final String COLUMN_SALARY = "salary";

    DatabaseManager(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
