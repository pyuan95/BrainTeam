package com.example.brainteam;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddTopicsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topics);
    }

    public void onAddTopicToList(View view)
    {
        EditText addTopicToList = (EditText) findViewById(R.id.addTopicToList);
        String topic = addTopicToList.getText().toString();
        addTopicToList.setText("");
        LinearLayout topicList = (LinearLayout) findViewById(R.id.topicList);
        TextView newTopic = new TextView(AddTopicsActivity.this);
        newTopic.setText(topic);
        newTopic.setTextColor(Color.BLACK);
        newTopic.setTextSize(18);
        newTopic.setPadding(0,16, 0 ,16);
        topicList.addView(newTopic);
    }


    public void addTopics(View view)
    {
        new Thread()
        {
            DatabaseManager db = new DatabaseManager(AddTopicsActivity.this, true);
            LinearLayout topicList = (LinearLayout) findViewById(R.id.topicList);
            public void run()
            {
                int i = 0;
                while (i < topicList.getChildCount())
                {
                    String topic = (String) ((TextView) topicList.getChildAt(i)).getText();
                    if (db.addTopic(topic))
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                topicList.removeViewAt(0);
                            }
                        });
                    }
                    else
                    {
                        i++;
                    }
                }
            }


        };
    }
}
