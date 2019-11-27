package com.example.backend;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.*;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.util.Log;

import com.example.brainteam.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;


public class DatabaseManager extends SQLiteOpenHelper
{
    private SQLiteDatabase allTossups;
    private SQLiteDatabase topicData;
    public Context context;
    public int[] IDs;
    public HashMap<String, ArrayList<Integer>> equalIDs;
    public boolean equal = false;
    public int lastXDays;
    public int lastXAdditions;
    public ArrayList<String> categories = new ArrayList<>();
    public ArrayList<String> difficulties = new ArrayList<>();
    public ArrayList<String> topics = new ArrayList<>();
    public String mode;
    public String currentTopic;
    public int currentID;
    // Equal selection from each topic will be implemented later.

    private static final String DATABASE_NAME = "topicData.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseManager(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.loadTopicData();
        Resources res = context.getResources();

        // Setting values to the default, AKA all possible.
        setDifficulties((ArrayList<String>) Arrays.asList(res.getStringArray(R.array.tossupDifficulties)));
        setCategories((ArrayList<String>) Arrays.asList(res.getStringArray(R.array.tossupCategories)));
        setTopics(getTopicNames());
        lastXDays = -1;
        lastXAdditions = -1;
        currentID = 0;
        currentTopic = null;
        //This code is deprecated. Moving database responsibility moved to BrainTeam.java, AKA Startup.
        /*
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        if (isFirstTime(context) && false) {
            try {
                in = assetManager.open("allTossups.db");
                File outFile = new File(context.getDatabasePath(DATABASE_NAME).getParent(), "allTossups.db");
                outFile.createNewFile();
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file.", e);
                e.printStackTrace();
            }
        }
        */

// commented this out. new backend will use specific methods to do stuff

/*        allTossups = new AllTossups(context).getReadableDatabase();
        //allTossups = openOrCreateDatabase(new File(context.getDatabasePath(DATABASE_NAME).getParent(), "allTossups.db").getAbsolutePath(), null, null);
        topicData = getWritableDatabase();

        // for testing
        Cursor c = allTossups.rawQuery("SELECT name FROM sqlite_master", null);
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                System.out.println(c.getString(0));
                c.moveToNext();
            }
        }

        Resources res = context.getResources();
        IDs = selectRandomTossups(categories, difficulties);
        c.close();*/
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String sql = "CREATE TABLE IF NOT EXISTS topicData(id INTEGER PRIMARY KEY, topic TEXT, ids TEXT, dateAdded DATETIME DEFAULT CURRENT_DATE, age INTEGER, numberTossups INTEGER, categpry TEXT, timesCorrect INTEGER, timesIncorrect INTEGER, timesSeen INTEGER)";
        String sql2 = "CREATE TABLE IF NOT EXISTS tossupData(id INTEGER PRIMARY KEY, tossupID INTEGER, timesCorrect INTEGER, timesIncorrect INTEGER, timesSeen INTEGER)";
        db.execSQL(sql);
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String sql = "DROP TABLE IF EXISTS topicData";
        db.execSQL(sql);
        onCreate(db);
    }

    // LOADS TOSSUPS AND TOPIC DATA ----------------------------------------------------------------

    public void loadAllTossups()
    {
        this.allTossups = new AllTossups(context).getReadableDatabase();
    }

    public void loadTopicData()
    {
        topicData = getWritableDatabase();
    }

    // GETTERS AND SETTERS -------------------------------------------------------------------------

    public void setTossupIDs(int[] ids)
    {
        this.IDs = ids;
    }

    public void setTopics(ArrayList<String> d)
    {
        this.topics = d;
    }

    public void setEqual(boolean b)
    {
        equal = b;
    }

    public void setLastXDays(int d)
    {
        lastXDays = d;
    }

    public void setLastXAdditions(int d)
    {
        lastXAdditions = d;
    }

    public void setCategories(ArrayList<String> d)
    {
        categories = d;
    }

    public void setDifficulties(ArrayList<String> d)
    {
        difficulties = d;
    }

    public boolean getEqual()
    {
        return equal;
    }

    public int getLastXDays()
    {
        return lastXDays;
    }

    public int getLastXAdditions()
    {
        return lastXAdditions;
    }

    public ArrayList<String> getCategories()
    {
        return categories;
    }

    public ArrayList<String> getDifficulties()
    {
        return difficulties;
    }

    public ArrayList<String> getTopicNames()
    {
        String sql = "SELECT topic FROM topicData";
        Cursor cursor = topicData.rawQuery(sql, null);
        ArrayList<String> topicNames = new ArrayList<>();
        while (cursor.moveToNext())
        {
            topicNames.add(cursor.getString(0));
        }
        cursor.close();
        return topicNames;
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
            if (IDs.length == 0) return new String[] {"Please add tossups before playing.", "bruh", "lmao"};
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
            cursor.close();

            currentID = id;
            currentTopic = null;
            return new String[] {info, question, answer};
        }
        else
        {
            List<String> keysAsArray = new ArrayList<String>(equalIDs.keySet());
            Random r = new Random();
            String topic = keysAsArray.get(r.nextInt(keysAsArray.size()));
            ArrayList<Integer> ids = equalIDs.get(topic);
            int id = ids.get(r.nextInt(ids.size()));
            String sql = "SELECT * FROM allTossups WHERE tossupID=?";
            Cursor cursor = allTossups.rawQuery(sql, new String[] {Integer.toString(id)});
            cursor.moveToFirst();
            String info = cursor.getString(4) //Tournament
                    + ", " + cursor.getString(5) //Difficulty
                    + ", " + cursor.getString(6); //Category
            String question = cursor.getString(2); // formattedText
            String answer = cursor.getString(3);
            cursor.close();

            currentID = id;
            currentTopic = topic;
            return new String[] {info, question, answer};
        }
    }


    // METHODS THAT SET IDs OF TOSSUPS -------------------------------------------------------------

    /**
     * Returns an int[] of up to 100 tossups based on categories and difficulty.
     * Categories: All, Current Events, Fine Arts, Geography, History, Literature, Mythology, Philosophy, Religion, Science, Social Science, Trash.
     * Difficulties: All, Middle School, Easy High School, Regular High School, National High School, Easy College, Regular College, Hard College, Open.
     * @return the ids of the tossups
     */
    public void selectRandomTossups()
    {
        for (int i = 0; i < categories.size(); i++)
        {
            categories.set(i, "'" + categories.get(i) + "'");
        }

        for (int i = 0; i < difficulties.size(); i++)
        {
            difficulties.set(i, "'" + difficulties.get(i) + "'");
        }

        String categoriesList = TextUtils.join(",", categories);
        String difficultiesList = TextUtils.join(",", difficulties);
        String sql = "SELECT tossupID FROM allTossups WHERE ";
        if (categories.size() == 0 || difficulties.size() == 0)
        {
            sql += "1=1";
        }
        else if ((!categories.contains("'All'")) && difficulties.contains("'All'"))
        {
            sql += "category IN (" + categoriesList + ")";
        }
        else if (categories.contains("'All'") && (!difficulties.contains("'All'")))
        {
            sql += "difficulty IN (" + difficultiesList + ")";
        }
        else if ((!categories.contains("'All'")) && (!difficulties.contains("'All'")))
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
        cursor.close();
        this.IDs = IDsArray;
    }

    /**
     * Considers:
     * Category
     * Difficulty
     * topics
     * lastXDays
     * lastXAdditions
     *
     * CREATE TABLE IF NOT EXISTS topicData(id INTEGER PRIMARY KEY, topic TEXT, ids TEXT,
     * dateAdded DATETIME DEFAULT CURRENT_DATE, age INTEGER, numberTossups INTEGER, categpry TEXT, timesCorrect INTEGER, timesIncorrect INTEGER, timesTotal INTEGER)
     */
    public void selectTossupsFromTopics()
    {
        if (equal)
        {
            if (lastXAdditions > 0)
            {
                this.topics = getTopicNames();
                this.topics = (ArrayList<String>) this.topics.subList(this.topics.size() - lastXAdditions, this.topics.size());
            }

            ArrayList<String> temp_topics = (ArrayList<String>) topics.clone();
            for (int i = 0; i < temp_topics.size(); i++)
            {
                temp_topics.set(i, "'" + temp_topics.get(i) + "'");
            }


            for (String topic: this.topics) {
                String sql = "SELECT topic, ids, age FROM topicData WHERE topic=" + topic;
                if (this.lastXDays > 0) {
                    sql += " AND age <= " + this.lastXDays;
                }
                Cursor cursor = topicData.rawQuery(sql, null);
                ArrayList<Integer> IDs = new ArrayList<Integer>();
                while (cursor.moveToNext()) {
                    String[] stringIDs = cursor.getString(1).split(",");
                    for (int i = 0; i < stringIDs.length; i++) {
                        int id = Integer.parseInt(stringIDs[i]);
                        IDs.add(id);
                    }
                    sql = "SELECT id, category, difficulty FROM allTossups WHERE tossupID in " + TextUtils.join(",", stringIDs) + ")";
                    cursor = allTossups.rawQuery(sql, null);
                    while (cursor.moveToNext())
                    {
                        if (!(this.categories.contains(cursor.getString(1)) && this.difficulties.contains(cursor.getString(2)))) // if the tossups is not in either the difficulties or categories
                        {
                            int id = cursor.getInt(0);
                            IDs.remove(new Integer(id));
                        }
                    }
                }
                if (IDs.size() != 0)
                    equalIDs.put(topic, IDs);
            }

//            sql = "SELECT id, category, difficulty FROM allTossups WHERE tossupID in " + TextUtils.join(",", IDs) + ")";
//            cursor= allTossups.rawQuery(sql, null);
//            while (cursor.moveToNext())
//            {
//                if (!(this.categories.contains(cursor.getString(1)) && this.difficulties.contains(cursor.getString(2)))) // if the tossups is not in either the difficulties or categories
//                {
//                    int id = cursor.getInt(0);
//                    IDs.remove(new Integer(id));
//                }
//            }
        }
        else
        {
            ArrayList<String> temp_topics = (ArrayList<String>) topics.clone();

            if (lastXAdditions > 0)
            {
                this.topics = getTopicNames();
                this.topics = (ArrayList<String>) this.topics.subList(this.topics.size() - lastXAdditions, this.topics.size());
            }


            for (int i = 0; i < temp_topics.size(); i++)
            {
                temp_topics.set(i, "'" + temp_topics.get(i) + "'");
            }

            String topicString = "(" + TextUtils.join(",", temp_topics) + ")";
            String sql = "SELECT topic, ids, age FROM topicData WHERE topic in " + topicString;
            if (this.lastXDays > 0)
            {
                sql += " AND age <= " + this.lastXDays;
            }
            Cursor cursor = topicData.rawQuery(sql, null);
            ArrayList<Integer> IDs = new ArrayList<Integer>();
            while (cursor.moveToNext())
            {
                String[] stringIDs = cursor.getString(1).split(",");
                for (int i = 0; i < stringIDs.length; i++)
                {
                    int id = Integer.parseInt(stringIDs[i]);
                    IDs.add(id);
                }
            }

            sql = "SELECT id, category, difficulty FROM allTossups WHERE tossupID in " + TextUtils.join(",", IDs) + ")";
            cursor= allTossups.rawQuery(sql, null);
            while (cursor.moveToNext())
            {
                if (!(this.categories.contains(cursor.getString(1)) && this.difficulties.contains(cursor.getString(2)))) // if the tossups is not in either the difficulties or categories
                {
                    int id = cursor.getInt(0);
                    IDs.remove(new Integer(id));
                }
            }

            int[] IDsArray = new int[IDs.size()];
            for (int i = 0; i < IDs.size(); i++)
            {
                IDsArray[i] = IDs.get(i);
            }
            cursor.close();
            this.IDs = IDsArray;
        }
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

        tossups.close();

        if (tossupCount == 0)
            return false;

        sql = "SELECT topic FROM topicData";
        Cursor topics = topicData.rawQuery(sql, null);
        while (topics.moveToNext())
        {
            if (topics.getString(0).toLowerCase().equals(topic.toLowerCase()))
                return false;
        }
        topics.close();

        String ids = TextUtils.join(",",tossupIDs);
        String sql2 = "INSERT INTO topicData(topic, ids, age, numberTossups) VALUES (?,?,?,?)";
        topicData.execSQL(sql2, new Object[] {topic.toLowerCase(), ids, 0, tossupCount}); //Topic is always lower case. Consider changing.
        return true;
    }

    public void markTossupCorrect()
    {
        String sql = "UPDATE tossupData SET timesCorrect = timesCorrect + 1, timesSeen = timesSeen + 1 WHERE tossupID=" + currentID;
        topicData.execSQL(sql);
    }

    public void markTossupIncorrect()
    {
        String sql = "UPDATE tossupData SET timesIncorrect = timesIncorrect + 1, timesSeen = timesSeen + 1 WHERE tossupID=" + currentID;
        topicData.execSQL(sql);
    }

    public void markTopicCorrect()
    {
        String sql = "UPDATE topicData SET timesCorrect = timesCorrect + 1, timesSeen = timesSeen + 1 WHERE topic=" + currentTopic;
        topicData.execSQL(sql);
    }

    public void markTopicIncorrect()
    {
        String sql = "UPDATE topicData SET timesIncorrect = timesIncorrect + 1, timesSeen = timesSeen + 1 WHERE topic=" + currentTopic;
        topicData.execSQL(sql);
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

    public boolean inAnswer(String query, String answer)
    {
        query = query.toLowerCase();
        answer = answer.toLowerCase();
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


    // Helper Methods

    public void updateDate()
    {
        String sql = "SELECT topic, dateAdded FROM topicData";
        Cursor cursor = topicData.rawQuery(sql, null);
        while (cursor.moveToNext())
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d1 = null;
            try {
                d1 = sdf.parse(cursor.getString(1)); //Parse the string of the current date into a date object.
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Date d2 = new Date(); //Current Date

            int age = (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
            System.out.println("First Date" + d1);
            System.out.println("Second Date" + d2);
            System.out.println("Age" + age);
            System.out.println();
            System.out.println();

            String updateDateSQL = "UPDATE topicData SET age=? WHERE topic=?";
            topicData.execSQL(updateDateSQL, new Object[] {age, cursor.getString(0)});
        }
    }


    // ***********NOT USED ANYMORE********
    /*
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private boolean isFirstTime(Context context)
    {
        boolean firstTime = false;
        SharedPreferences mPreferences = context.getSharedPreferences("first_time", Context.MODE_PRIVATE);
        firstTime = mPreferences.getBoolean("firstTime", true);
        if (firstTime) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();
        }

        return firstTime;
    }
    */

}
