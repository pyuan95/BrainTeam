package com.example.brainteam;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeleteTopicsActivity extends AppCompatActivity
{

    private static List<String> items = new ArrayList<String>();
    ListView myLV;
    ArrayAdapter myAdapter;
    DatabaseManager db;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_topics);

        EditText edt = (EditText) findViewById(R.id.addTopicToList);
        edt.addTextChangedListener(new TextWatcher()
        {


            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
            {
                // TODO Auto-generated method stub

            }



            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
            {
                // TODO Auto-generated method stub

            }



            @Override
            public void afterTextChanged(Editable arg0)
            {
                // TODO Auto-generated method stub
                DeleteTopicsActivity.this.myAdapter.getFilter().filter(arg0);
            }
        });

        items = new ArrayList<String>();
        myAdapter = new ArrayAdapter(this, R.layout.topic_list, R.id.topicName, items);
        myLV = (ListView) findViewById(R.id.topicList);
        myLV.setAdapter(myAdapter);

        new Thread()
        {
            public void run()
            {
                    db = new DatabaseManager(DeleteTopicsActivity.this, false);
                    String[][] topicNames = db.getTopicNames();
                    for (String[] topic : topicNames)
                    {
                        items.add(topic[0]);
                    }
            }
        }.start();
    }

    public void onDeleteTopicFromList(View view)
    {
        int position = myLV.getPositionForView((View) view.getParent());

        AlertDialog.Builder adb = new AlertDialog.Builder(DeleteTopicsActivity.this);
        adb.setTitle("Delete?");
        adb.setMessage("Are you sure you want to delete " + items.get(position) + "?");
        final int positionToRemove = position;
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                db.deleteTopic(items.get(positionToRemove));
                items.remove(positionToRemove);
                myAdapter.notifyDataSetChanged();
            }
        });
        adb.show();
    }
}
