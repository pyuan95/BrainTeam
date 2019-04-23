package com.example.backend;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import com.example.backend.DatabaseManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BrainTeam extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        AssetManager assetManager = this.getAssets();
        InputStream in = null;
        OutputStream out = null;
        if (isFirstTime(this)) {
            try {
                openOrCreateDatabase("topicData.db", 0, null);
                in = assetManager.open("allTossups.db");
                File outFile = new File(this.getDatabasePath("topicData.db").getParent(), "allTossups.db");
                outFile.createNewFile();
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file.", e);
                e.printStackTrace();
            }
        }
        // Your methods here...
        DatabaseManager db = new DatabaseManager(this, "AddOrDelete");
        db.updateDate();
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

}
