package com.example.brainteam;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.text.TextUtils;

import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class DatabaseManager extends SQLiteOpenHelper
{
    SQLiteDatabase allTossups = openOrCreateDatabase("allTossups.db",null);
    SQLiteDatabase topicData;
    ArrayList IDs = new ArrayList();

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
        String sql = "CREATE TABLE IF NOT EXISTS topicData(id INTEGER PRIMARY KEY, topic TEXT, ids TEXT, dateAdded DATETIME DEFAULT CURRENT_DATE, age INTEGER, numberTossups INTEGER)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String sql = "DROP TABLE IF EXISTS topicData";
        db.execSQL(sql);
        onCreate(db);
    }

    /**
     * Returns an ArrayList<Integer> of IDs of tossups from added topics in the last week.
     * Precondition: Assumes that the ages of all the topics is correct.
     * @return an int[] of tossups from added topics in the last week.
     */
    public ArrayList<Integer> lastWeek()
    {
        String sql = "SELECT topic, ids FROM topicData WHERE age <= 7";
        Cursor cursor = topicData.rawQuery(sql, null);
        ArrayList<Integer> IDs = new ArrayList<Integer>();
        while (cursor.moveToNext())
        {
            String[] stringIDs = cursor.getString(1).split(",");
            for (int i = 0; i < stringIDs.length; i++)
            {
                IDs.add(Integer.parseInt(stringIDs[i]));
            }
        }
        return IDs;
    }

    /**
     * Returns an ArrayList<ArrayList<Integer>> of ids from added topics in the last week, but *roughly equal* number of tossups per topic.
     * @return an int[] of tossups from added topics in the last week, but *roughly equal* number of tossups per topic.
     */
    public ArrayList<ArrayList<Integer>> lastWeekEqual()
    {
        String sql = "SELECT topic, ids FROM topicData WHERE age <= 7";
        Cursor cursor = topicData.rawQuery(sql, null);
        ArrayList<ArrayList<Integer>> IDs = new ArrayList<>();
        while (cursor.moveToNext())
        {
            String[] stringIDs = cursor.getString(1).split(",");
            ArrayList<Integer> intIDs = new ArrayList<>();
            for (int i = 0; i < stringIDs.length; i++)
            {
                intIDs.add(Integer.parseInt(stringIDs[i]));
            }
            IDs.add(intIDs);
        }
        return IDs;
    }

    /**
     * Returns an int[] of tossups from added topics in the last month.
     * @return an int[] of tossups from added topics in the last month.
     */
    public ArrayList<Integer> lastMonth()
    {
        String sql = "SELECT topic, ids FROM topicData WHERE age <= 7";
        Cursor cursor = topicData.rawQuery(sql, null);
        ArrayList<Integer> IDs = new ArrayList<Integer>();
        while (cursor.moveToNext())
        {
            String[] stringIDs = cursor.getString(1).split(",");
            for (int i = 0; i < stringIDs.length; i++)
            {
                IDs.add(Integer.parseInt(stringIDs[i]));
            }
        }
        return IDs;
    }

    /**
     * Returns an int[] of tossups from added topics in the last month, but *roughly equal* number of tossups per topic.
     * @return an int[] of tossups from added topics in the last month, but *roughly equal* number of tossups per topic.
     */
    public ArrayList<ArrayList<Integer>> lastMonthEqual()
    {
        String sql = "SELECT topic, ids FROM topicData WHERE age <= 7";
        Cursor cursor = topicData.rawQuery(sql, null);
        ArrayList<ArrayList<Integer>> IDs = new ArrayList<>();
        while (cursor.moveToNext())
        {
            String[] stringIDs = cursor.getString(1).split(",");
            ArrayList<Integer> intIDs = new ArrayList<>();
            for (int i = 0; i < stringIDs.length; i++)
            {
                intIDs.add(Integer.parseInt(stringIDs[i]));
            }
            IDs.add(intIDs);
        }
        return IDs;
    }

    /**
     * Returns an int[] of all tossups from added topics.
     * @return an int[] of all tossups from added topics.
     */
    public ArrayList<Integer> lastAll()
    {
        String sql = "SELECT topic, ids FROM topicData WHERE age <= 7";
        Cursor cursor = topicData.rawQuery(sql, null);
        ArrayList<Integer> IDs = new ArrayList<Integer>();
        while (cursor.moveToNext())
        {
            String[] stringIDs = cursor.getString(1).split(",");
            for (int i = 0; i < stringIDs.length; i++)
            {
                IDs.add(Integer.parseInt(stringIDs[i]));
            }
        }
        return IDs;
    }

    /**
     * Returns an int[] of *all* tossups from added topics, but *roughly equal* number of tossups per topic.
     * @return an int[] of *all* tossups from added topics, but *roughly equal* number of tossups per topic.
     */
    public ArrayList<ArrayList<Integer>> lastAllEqual()
    {
        String sql = "SELECT topic, ids FROM topicData WHERE age <= 7";
        Cursor cursor = topicData.rawQuery(sql, null);
        ArrayList<ArrayList<Integer>> IDs = new ArrayList<>();
        while (cursor.moveToNext())
        {
            String[] stringIDs = cursor.getString(1).split(",");
            ArrayList<Integer> intIDs = new ArrayList<>();
            for (int i = 0; i < stringIDs.length; i++)
            {
                intIDs.add(Integer.parseInt(stringIDs[i]));
            }
            IDs.add(intIDs);
        }
        return IDs;
    }

    /**
     * Returns an int[] of up to 100 tossups based on categories and difficulty.
     * Categories: All, Current Events, Fine Arts, Geography, History, Literature, Mythology, Philosophy, Religion, Science, Social Science, Trash.
     * Difficulties: All, Middle School, Easy High School, Regular High School, National High School, Easy College, Regular College, Hard College, Open.
     * @return the ids of the tossups
     */
    public ArrayList<Integer> selectRandomTossups(ArrayList<String> categories, ArrayList<String> difficulties)
    {
        String categoriesList = TextUtils.join(",", categories);
        String difficultiesList = TextUtils.join(",", difficulties);
        String sql = "SELECT * FROM allTossups WHERE ";

        if (categories.size() == 0 || difficulties.size() == 0)
        {
            sql += "1=1";
        }
        else if ((!categories.contains("All")) && difficulties.contains("All"))
        {
            sql += "category IN " + categoriesList;
        }
        else if (categories.contains("All") && (!difficulties.contains("All")))
        {
            sql += "difficulty IN " + difficultiesList;
        }
        else if ((!categories.contains("All")) && (!difficulties.contains("All")))
        {
            sql += "category IN " + categoriesList + " AND difficulty IN " + difficultiesList;
        }
        else
        {
            sql += "1=1";
        }

        Cursor cursor = allTossups.rawQuery(sql, null);
        
    }

    /**
     * Adds a topic to the database; does a query, adds all the name of the topic, the ids to that topic,
     * the date the topic is added, the age, and the number of tossups in that topic to a table
     * names topicData.
     *
     * topic: text
     * ids: text, with each id separated by a comma. EX: 3,4,5,6,7
     *
     */
    public boolean addTopic(String topic)
    {
        String sql = "SELECT tossupID, answer FROM allTossups";
        Cursor tossups = allTossups.rawQuery(sql, null);
        int tossupCount = 0;
        ArrayList<String> tossupIDs = new ArrayList<String>();

        while (tossups.moveToNext())
        {
            String answer = tossups.getString(1);
            if (inAnswer(topic, answer))
            {
                tossupCount++;
                tossupIDs.add(Integer.toString(tossups.getInt(0)));
            }
        }

        if (tossupCount == 0) return false;
        else
        {
            String ids = TextUtils.join(",",tossupIDs);
            String sql2 = "INSERT INTO topicData(topic, ids, age, numberTossups) VALUES (?,?,?,?,?)";
            topicData.execSQL(sql2, new Object[] {topic, ids, 0, tossupCount});
            return true;
        }
    }

    public boolean inAnswer(String query, String answer)
    {
        String[] parts = query.split(" ");
        boolean contains = true;
        for (String str : parts)
        {
            if (!answer.contains(str))
            {
                contains = false;
                break;
            }
        }
        return contains;
    }

    /**
     * Deletes a topic: deletes the topic from table "__topics__", and the table that has the name of the topic.
     */
    public void deleteTopic(String topic)
    {
        String sql = "DELETE FROM topicData WHERE topic = ?";
        topicData.execSQL(sql, new Object[] {topic});
        // Can't return boolean because if there is nothing to delete, nothing happens; no error thrown.
    }
}
