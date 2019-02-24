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
    int[] IDs;
    int[][] equalIDs;
    boolean equal = false;
    // Equal selection from each topic will be implemented later.


    private static final String DATABASE_NAME = "topicData.db";
    private static final int DATABASE_VERSION = 1;


    DatabaseManager(Context context, ArrayList<String> categories, ArrayList<String> difficulties)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        topicData = getWritableDatabase();
        if (categories.contains(R.string.lastWeek)) { IDs = lastWeek(); }
        else if (categories.contains(R.string.lastMonth)) { IDs = lastMonth();}
        else if (categories.contains(R.string.lastAll)) {IDs = lastAll();}
        else {IDs = selectRandomTossups(categories, difficulties);}
    }

    /**
     * Gets the next tossup. HAS NOT implemented the "equal topic" feature yet.
     * @return A String array, containing: Tossup info, the tossup, and the answer.
     */
    public String[] getNextTossup()
    {
        if (!equal)
        {
            // The columnindex numbers used here reflect the db file where there is no "regular" text; only formattedText.

            int id = IDs[(int) (Math.random() * IDs.length)]; //Select a Random Tossup
            String sql = "SELECT * FROM allTossups WHERE tossupID=?";
            Cursor cursor = allTossups.rawQuery(sql, new String[] {Integer.toString(id)});
            // Should be only one row in the cursor.
            cursor.moveToFirst();
            String info = cursor.getString(4) //Tournament
                    + ", " + cursor.getString(5) //Difficulty
                    + ", " + cursor.getString(6); //Category
            String question = cursor.getString(2); // formattedText
            String answer = cursor.getString(3);
            return new String[] {info, question, answer};
        }
        else
        {
            return new String[] {"This is", "not implemented", "yet!"};
        }
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
    public int[] lastWeek()
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

        int[] IDsArray = new int[IDs.size()];
        for (int i = 0; i < IDs.size(); i++)
        {
            IDsArray[i] = IDs.get(i);
        }

        return IDsArray;
    }

    /**
     * Returns an ArrayList<ArrayList<Integer>> of ids from added topics in the last week, but *roughly equal* number of tossups per topic.
     * @return an int[] of tossups from added topics in the last week, but *roughly equal* number of tossups per topic.
     */
    public int[][] lastWeekEqual()
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
        int[][] IDsArray = new int[IDs.size()][];
        for (int i = 0; i < IDs.size(); i++)
        {
            for (int j = 0; j < IDs.get(i).size(); j++)
            {
                IDsArray[i][j] = IDs.get(i).get(j);
            }
        }

        return IDsArray;
    }

    /**
     * Returns an int[] of tossups from added topics in the last month.
     * @return an int[] of tossups from added topics in the last month.
     */
    public int[] lastMonth()
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
        int[] IDsArray = new int[IDs.size()];
        for (int i = 0; i < IDs.size(); i++)
        {
            IDsArray[i] = IDs.get(i);
        }

        return IDsArray;
    }

    /**
     * Returns an int[] of tossups from added topics in the last month, but *roughly equal* number of tossups per topic.
     * @return an int[] of tossups from added topics in the last month, but *roughly equal* number of tossups per topic.
     */
    public int[][] lastMonthEqual()
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
        int[][] IDsArray = new int[IDs.size()][];
        for (int i = 0; i < IDs.size(); i++)
        {
            for (int j = 0; j < IDs.get(i).size(); j++)
            {
                IDsArray[i][j] = IDs.get(i).get(j);
            }
        }

        return IDsArray;
    }

    /**
     * Returns an int[] of all tossups from added topics.
     * @return an int[] of all tossups from added topics.
     */
    public int[] lastAll()
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
        int[] IDsArray = new int[IDs.size()];
        for (int i = 0; i < IDs.size(); i++)
        {
            IDsArray[i] = IDs.get(i);
        }

        return IDsArray;
    }

    /**
     * Returns an int[] of *all* tossups from added topics, but *roughly equal* number of tossups per topic.
     * @return an int[] of *all* tossups from added topics, but *roughly equal* number of tossups per topic.
     */
    public int[][] lastAllEqual()
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
        int[][] IDsArray = new int[IDs.size()][];
        for (int i = 0; i < IDs.size(); i++)
        {
            for (int j = 0; j < IDs.get(i).size(); j++)
            {
                IDsArray[i][j] = IDs.get(i).get(j);
            }
        }

        return IDsArray;
    }

    /**
     * Returns an int[] of up to 100 tossups based on categories and difficulty.
     * Categories: All, Current Events, Fine Arts, Geography, History, Literature, Mythology, Philosophy, Religion, Science, Social Science, Trash.
     * Difficulties: All, Middle School, Easy High School, Regular High School, National High School, Easy College, Regular College, Hard College, Open.
     * @return the ids of the tossups
     */
    public int[] selectRandomTossups(ArrayList<String> categories, ArrayList<String> difficulties)
    {
        String categoriesList = TextUtils.join(",", categories);
        String difficultiesList = TextUtils.join(",", difficulties);
        String sql = "SELECT tossupID FROM allTossups WHERE ";
        if (categories.size() == 0 || difficulties.size() == 0)
        {
            sql += "1=1";
        }
        else if ((!categories.contains("All")) && difficulties.contains("All"))
        {
            sql += "category IN (" + categoriesList + ")";
        }
        else if (categories.contains("All") && (!difficulties.contains("All")))
        {
            sql += "difficulty IN (" + difficultiesList + ")";
        }
        else if ((!categories.contains("All")) && (!difficulties.contains("All")))
        {
            sql += "category IN (" + categoriesList + ")" + " AND difficulty IN (" + difficultiesList + ")";
        }
        else
        {
            sql += "1=1";
        }

        ArrayList<Integer> IDs = new ArrayList<>();
        Cursor cursor = allTossups.rawQuery(sql, null);
        while (cursor.moveToNext())
        {
            IDs.add(cursor.getInt(0));
        }

        int[] IDsArray = new int[IDs.size()];
        for (int i = 0; i < IDs.size(); i++)
        {
            IDsArray[i] = IDs.get(i);
        }

        return IDsArray;
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
