package com.example.brainteam;
import android.content.Context;
import android.database.sqlite.*;

import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class DatabaseManager extends SQLiteOpenHelper
{
    SQLiteDatabase allTossups = openOrCreateDatabase("allTossups.db",null);
    SQLiteDatabase topicData;


    private static final String DATABASE_NAME = "topicData.db";
    private static final int DATABASE_VERSION = 1;


    DatabaseManager(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        topicData = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String sql = "CREATE TABLE IF NOT EXISTS topicData(id INTEGER PRIMARY KEY, topic TEXT, dateAdded TEXT, age INTEGER, numberTossups INTEGER);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String sql = "DROP TABLE IF EXISTS topicData;";
        db.execSQL(sql);
        onCreate(db);
    }

    /**
     * Returns an int[] of tossups from added topics in the last week.
     * @return an int[] of tossups from added topics in the last week.
     */
    public int[] lastWeek()
    {
        return new int[5];
    }

    /**
     * Returns an int[] of tossups from added topics in the last week, but *roughly equal* number of tossups per topic.
     * @return an int[] of tossups from added topics in the last week, but *roughly equal* number of tossups per topic.
     */
    public int[] lastWeekEqual()
    {
        return new int[5];
    }

    /**
     * Returns an int[] of tossups from added topics in the last month.
     * @return an int[] of tossups from added topics in the last month.
     */
    public int[] lastMonth()
    {
        return new int[5];
    }

    /**
     * Returns an int[] of tossups from added topics in the last month, but *roughly equal* number of tossups per topic.
     * @return an int[] of tossups from added topics in the last month, but *roughly equal* number of tossups per topic.
     */
    public int[] lastMonthEqual()
    {
        return new int[5];
    }

    /**
     * Returns an int[] of all tossups from added topics.
     * @return an int[] of all tossups from added topics.
     */
    public int[] lastAll()
    {
        return new int[5];
    }

    /**
     * Returns an int[] of *all* tossups from added topics, but *roughly equal* number of tossups per topic.
     * @return an int[] of *all* tossups from added topics, but *roughly equal* number of tossups per topic.
     */
    public int[] lastAllEqual()
    {
        return new int[5];
    }

    /**
     * Returns an int[] of up to 100 tossups based on categories and difficulty.
     * Categories: lastWeek, lastMonth, lastAll, All, Current Events, Fine Arts, Geography, History, Literature, Mythology, Philosophy, Religion, Science, Social Science, Trash.
     * Difficulties: All, Middle School, Easy High School, Regular High School, National High School, Easy College, Regular College, Hard College, Open.
     * @return the ids of the tossups
     */
    public int[] selectTossups(ArrayList<String> categories, ArrayList<String> difficulties)
    {
        return new int[5];
    }

    /**
     * Adds a topic to the database; does a query, makes a new table with the name of the topic,
     * adds the quizDB IDs of all tossups from the query (in that topic) to that table,
     * adds the topic and relevant data (date added, number of tossups) to table "__topics__"
     */
    public void addTopic()
    {

    }

    /**
     * Deletes a topic: deletes the topic from table "__topics__", and the table that has the name of the topic.
     */
    public void deleteTopic()
    {

    }
}
