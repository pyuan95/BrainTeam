package com.example.brainteam;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SearchView;

import com.example.backend.DatabaseManager;
import com.example.backend.TopicListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReadSelector extends AppCompatActivity
{
    public static final String categories = "com.example.brainteam.MainActivity.categories";
    public static final String difficulties = "com.example.brainteam.MainActivity.difficulties";

    private static List<String> items = new ArrayList<String>();
    private static List<String> extraInformation = new ArrayList<String>();
    private static List<Integer> topicsChecked = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    DatabaseManager db;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        topicsChecked = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_selector);

        items = new ArrayList<String>();
        new Thread()
        {
            public void run()
            {
                db = new DatabaseManager(ReadSelector.this, false);
                String[][] topicNames = db.getTopicNames();
                for (String[] topic : topicNames)
                {
                    items.add(topic[0]);
                    extraInformation.add(topic[1]);
                }
            }
        }.start();

        recyclerView = (RecyclerView) findViewById(R.id.topicList);
        layoutManager = new LinearLayoutManager(this); // Change later to custom manager.
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TopicListAdapter(this, items, extraInformation, false, false);
        recyclerView.setAdapter(adapter);
        SearchView searchView = findViewById(R.id.search);
    }

    public void onCheckboxChecked(View view)
    {
        CheckBox checkbox = (CheckBox) view;
        if (checkbox.isChecked())
        {
            topicsChecked.add(recyclerView.getChildAdapterPosition((View) checkbox.getParent()));
        }
        else
        {
            topicsChecked.remove((Integer) recyclerView.getChildAdapterPosition((View) checkbox.getParent()));
        }
        // System.out.println(recyclerView.getChildAdapterPosition((View) checkbox.getParent()));
        // System.out.println(checkbox.isChecked());
    }


    public void changeToRead(android.view.View view)
    {
        ArrayList<String> categories = new ArrayList<String>();
        ArrayList<String> topics = new ArrayList<String>();
        Resources res = this.getResources();
        categories.add(0, res.getString(R.string.multipleTopics)); //should be something like multiple topic, change later
        for (int i = 0; i < topicsChecked.size(); i++)
        {
            topics.add(items.get(topicsChecked.get(i)));
            System.out.println(items.get(topicsChecked.get(i)));
        }
        changeToReadActivityHelper(categories, topics);
    }


    public void changeToReadActivityHelper(ArrayList<String> categoriesArr, ArrayList<String> difficultiesArr)
    {
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra(categories, categoriesArr);
        intent.putExtra(difficulties, difficultiesArr);
        startActivity(intent);
    }
}

