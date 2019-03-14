package com.example.brainteam;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.*;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class DatabaseManager extends SQLiteOpenHelper
{
    private SQLiteDatabase allTossups;
    private SQLiteDatabase topicData;
    int[] IDs;
    int[][] equalIDs;
    boolean equal = false;
    // Equal selection from each topic will be implemented later.


    private static final String DATABASE_NAME = "topicData.db";
    private static final int DATABASE_VERSION = 1;


    DatabaseManager(Context context, ArrayList<String> categories, ArrayList<String> difficulties) throws IOException {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        if (isFirstTime(context) && false) { //This code is deprecated. Moving database responsibility moved to BrainTeam.java, AKA Startup.
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

        allTossups = new AllTossups(context).getReadableDatabase();
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
        if (categories.contains(res.getString(R.string.lastWeek))) { IDs = lastWeek(); }
        else if (categories.contains(res.getString(R.string.lastMonth))) { IDs = lastMonth();}
        else if (categories.contains(res.getString(R.string.lastAll))) {IDs = lastAll();}
        else {IDs = selectRandomTossups(categories, difficulties);}
        c.close();
    }

    DatabaseManager(Context context, boolean AddOrDelete)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        allTossups = new AllTossups(context).getReadableDatabase();
        //allTossups = openOrCreateDatabase(new File(context.getDatabasePath(DATABASE_NAME).getParent(), "allTossups.db").getAbsolutePath(), null, null);
        topicData = getWritableDatabase();
        IDs = new int[] {100000}; //To prevent any random bugs
    }


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
        cursor.close();
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

        cursor.close();
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
        cursor.close();
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
        cursor.close();
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
        cursor.close();
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
        cursor.close();
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

    /**
     * Deletes a topic: deletes the topic from table "__topics__", and the table that has the name of the topic.
     */
    public void deleteTopic(String topic)
    {
        String sql = "DELETE FROM topicData WHERE topic = ?";
        topicData.execSQL(sql, new Object[] {topic});
        // Can't return boolean because if there is nothing to delete, nothing happens; no error thrown.
    }

    public String[][] getTopicNames()
    {
        String sql = "SELECT topic, numberTossups FROM topicData";
        Cursor cursor = topicData.rawQuery(sql, null);
        ArrayList<String[]> topicNames = new ArrayList<>();
        while (cursor.moveToNext())
        {
            topicNames.add(new String[] {cursor.getString(0), cursor.getString(1)});
        }
        cursor.close();
        return Arrays.copyOf(topicNames.toArray(), topicNames.size(), String[][].class);
    }

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
}
