package com.example.brainteam;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ReplacementTransformationMethod;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class ReadActivity extends AppCompatActivity
{
    int sleepTime = 190; //in milliseconds
    DatabaseManager db;
    String[] tossup;
    boolean keepReading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        new Thread()
        {
            public void run()
            {
                try {
                    db = new DatabaseManager(ReadActivity.this, getIntent().getStringArrayListExtra(MainActivity.categories), getIntent().getStringArrayListExtra(MainActivity.difficulties));
                    tossup = db.getNextTossup();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tossup = db.getNextTossup();
            }
        }.start();

    }


    public void read(View view)
    {
        TextView answer = (TextView) findViewById(R.id.answer);
        answer.setText(Html.fromHtml(""));
        keepReading = true;
        if (tossup == null)
            return;
        else
            tossup = db.getNextTossup();
        new Thread()
        {

            String info = tossup[0] + "<br/> <br/>";
            String[] question = tossup[1].split(" ");
            final TextView reader = (TextView) findViewById(R.id.reader);

            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // reader.setTransformationMethod(WordBreakTransformationMethod.getInstance());
                        reader.setText(info);
                    }
                });
                for (int i = 0; i <= question.length; i++) {
                    final int j = i; //Have to do this because of some sort of wacky "Needs to be declared final" shit
                    if (!keepReading){
                        break;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reader.setText(Html.fromHtml(info + TextUtils.join(" ", Arrays.copyOfRange(question, 0, j))));
                        }
                    });
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void showAnswer(View view)
    {
        keepReading = false;
        TextView answer = (TextView) findViewById(R.id.answer);
        TextView reader = (TextView) findViewById(R.id.reader);
        reader.setText(Html.fromHtml(tossup[0] + "<br/> <br/>" + tossup[1]));
        answer.setText(Html.fromHtml("<b>Answer: </b>" + tossup[2]));
    }
}
