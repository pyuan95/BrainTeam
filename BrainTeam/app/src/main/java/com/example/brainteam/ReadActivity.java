package com.example.brainteam;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;



import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class ReadActivity extends AppCompatActivity
{
    int sleepTime = 150; //in milliseconds
    DatabaseManager db;
    String[] tossup;
    Thread readThread;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        try {
            db = new DatabaseManager
                    (this, getIntent().getStringArrayListExtra(MainActivity.categories), getIntent().getStringArrayListExtra(MainActivity.difficulties));
        } catch (IOException e) {
            e.printStackTrace();
        }
        tossup = db.getNextTossup();
    }


    public void read(View view)
    {
        tossup = db.getNextTossup();
        readThread = new Thread()
        {
            String info = tossup[0] + "\n";
            String[] question = tossup[1].split(" ");
            final TextView reader = (TextView) findViewById(R.id.reader);
            public void run() {
                reader.setText(info);
                for (int i = 0; i < question.length; i++) {
                    final int j = i; //Have to do this because of some sort of wacky "Needs to be declared final" shit
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reader.setText(reader.getText() + question[j]);
                        }
                    });
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        readThread.start();
    }

    public void showAnswer(View view)
    {
        TextView answer = (TextView) findViewById(R.id.answer);
        readThread.interrupt();
        TextView reader = (TextView) findViewById(R.id.reader);
        reader.setText(tossup[0] + tossup[1]);
        answer.setText(tossup[2]);
    }
}