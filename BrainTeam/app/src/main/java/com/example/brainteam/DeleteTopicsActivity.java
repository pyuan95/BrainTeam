package com.example.brainteam;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SearchView;


import com.example.backend.DatabaseManager;
import com.example.backend.TopicListAdapter;

import java.util.ArrayList;
import java.util.List;

public class DeleteTopicsActivity extends AppCompatActivity
{
    private static List<String> items = new ArrayList<String>();
    private static List<String> extraInformation = new ArrayList<String>();
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    DatabaseManager db;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_topics);

        items = new ArrayList<String>();
        new Thread()
        {
            public void run()
            {
                db = new DatabaseManager(DeleteTopicsActivity.this, "AddOrDelete");
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
        adapter = new TopicListAdapter(this, items, extraInformation, true, true);
        recyclerView.setAdapter(adapter);
        SearchView searchView = findViewById(R.id.search);
    }

    public void onDeleteTopicFromList(View view)
    {
        View parent = (View) view.getParent();
        final int pos = recyclerView.getChildAdapterPosition(parent);
        String topicName = items.get(pos);
        AlertDialog.Builder adb = new AlertDialog.Builder(DeleteTopicsActivity.this);
        adb.setTitle("Delete?");
        adb.setMessage("Are you sure you want to delete " + topicName + "?");
        final String itemToRemove = topicName;
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                db.deleteTopic(itemToRemove);
                items.remove(pos);
                adapter.notifyDataSetChanged();
            }
        });
        adb.show();
    }

}

