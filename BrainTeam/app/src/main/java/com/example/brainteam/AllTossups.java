package com.example.brainteam;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class AllTossups extends SQLiteOpenHelper
{


    private static final int DATABASE_VERSION = 1;

    AllTossups(Context context)
    {
        super(context, new File(context.getDatabasePath("topicData.db").getParent(), "allTossups.db").getPath(), null, DATABASE_VERSION);
        System.out.println("Size of db: " + new File(context.getDatabasePath("topicData.db").getParent(), "allTossups.db").length());
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
