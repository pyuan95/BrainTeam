package com.example.brainteam;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddTopicsActivity extends AppCompatActivity {


    private static List<String> items = new ArrayList<String>();
    ListView myLV;
    ArrayAdapter myAdapter;
    boolean currentlyAddingTopics = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topics);
        myAdapter = new ArrayAdapter(this, R.layout.topic_list, R.id.topicName, items);
        myLV = (ListView) findViewById(R.id.topicList);
        myLV.setAdapter(myAdapter);

    }

    public void onAddTopicToList(View view)
    {
        EditText addTopicToList = (EditText) findViewById(R.id.addTopicToList);
        String topic = addTopicToList.getText().toString();
        addTopicToList.setText("");
        items.add(topic);
        myAdapter.notifyDataSetChanged();


//        TextView newTopic = new TextView(AddTopicsActivity.this);
//        newTopic.setText(topic);
//        newTopic.setTextColor(Color.BLACK);
//        newTopic.setTextSize(18);
//        newTopic.setPadding(0,16, 0 ,16);
//        topicList.addView(newTopic);
    }



    public void onAddTopic(View view)
    {
        if (this.currentlyAddingTopics) {
            return;
        }
        else {
            this.currentlyAddingTopics = true;
        }
        new Thread()
        {
            DatabaseManager db = new DatabaseManager(AddTopicsActivity.this, true);
            public void run()
            {
                int i = 0;
                while (i < items.size())
                {
                    String topic = items.get(i);
                    db.addTopic(topic);
                    items.remove(i);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.notifyDataSetChanged();
                        }
                    });
                }
                AddTopicsActivity.this.currentlyAddingTopics = false;
            }
        }.start();
    }

    public void onDeleteTopicFromList(View view)
    {
        int position = myLV.getPositionForView((View) view.getParent());
        items.remove(position);
        myAdapter.notifyDataSetChanged();
    }
}

