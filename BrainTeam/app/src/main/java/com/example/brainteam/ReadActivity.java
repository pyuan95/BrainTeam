package com.example.brainteam;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;



import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class ReadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
    }

    public void read(View view)
    {
        Intent intent = this.getIntent();
        final ArrayList<String> categories = intent.getStringArrayListExtra(MainActivity.categories);

        new Thread() {
            public void run() {
                for (int i = 0; i < 100; i++) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView reader = (TextView) findViewById(R.id.reader);
                            reader.setText(reader.getText() + categories.toString());
                            ;
                        }
                    });
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void showAnswer(View view)
    {
        TextView answer = (TextView) findViewById(R.id.answer);
        answer.setText("Answer goes here.");
    }
}