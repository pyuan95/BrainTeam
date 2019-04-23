package com.example.brainteam;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String categories = "com.example.brainteam.MainActivity.categories";
    public static final String difficulties = "com.example.brainteam.MainActivity.difficulties";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String title = (String) item.getTitle();
        if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.addTopics) {
            Intent intent = new Intent(this, AddTopicsActivity.class);
            startActivity(intent);
        } else if (id == R.id.deleteTopics) {
            Intent intent = new Intent(this, DeleteTopicsActivity.class);
            startActivity(intent);
        } else if (id == R.id.statistics) {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.readLastXDays)
        {
            changeToReadActivity(title);
        }
        else if (id == R.id.readLastXTopics)
        {
            changeToReadActivity(title);
        }
        else if (id == R.id.readSelectedTopics)
        {
            changeToReadActivity(title);
        }
        else if (id == R.id.readTossups)
        {
            changeToReadActivity(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * This is some complicated shit. Basically, we have a Checklist in a Checklist. Checklistception. Don't worry about it.
     * Hopefully you will never have to debug this stuff.
     */
    public void changeToReadActivity(String activityToChangeTo)
    {
        Resources res = getResources();
        if (activityToChangeTo.equals(res.getString(R.string.readTossups)))
        {
            final ArrayList<String> categoriesArr = new ArrayList<String>();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.selectRandomTossupsTitleCategories).setMultiChoiceItems(R.array.tossupCategories, null, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (isChecked)
                    {
                        Resources res = getResources();
                        categoriesArr.add(res.getStringArray(R.array.tossupCategories)[which]); // adds selected items
                    }
                    else
                    {
                        Resources res = getResources();
                        categoriesArr.remove(res.getStringArray(R.array.tossupCategories)[which]); // deletes selected items
                    }
                }
            }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    final ArrayList<String> difficultiesArr = new ArrayList<String>();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setTitle(R.string.selectRandomTossupsTitleDifficulties).setMultiChoiceItems(R.array.tossupDifficulties, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (isChecked)
                            {
                                Resources res = getResources();
                                difficultiesArr.add(res.getStringArray(R.array.tossupDifficulties)[which]); // adds selected items
                            }
                            else
                            {
                                Resources res = getResources();
                                difficultiesArr.remove(res.getStringArray(R.array.tossupDifficulties)[which]); // deletes selected items
                            }
                        }
                    }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            changeToReadActivityHelper(categoriesArr, difficultiesArr);
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // If the user clicks cancel, do nothing.
                        }
                    });
                    builder1.create().show();

                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // If the user clicks cancel, do nothing.
                }
            });
            builder.create().show();
        }
        else if (activityToChangeTo.equals(res.getString(R.string.readLastXDays)))
        {
            final ArrayList<String> categoriesArr = new ArrayList<String>();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.selectLastXDays).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {

                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // If the user clicks cancel, do nothing.
                }
            });
            builder.create().show();
        }
    }

    public void changeToReadActivityHelper(ArrayList<String> categoriesArr, ArrayList<String> difficultiesArr)
    {
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra(categories, categoriesArr);
        intent.putExtra(difficulties, difficultiesArr);
        startActivity(intent);
    }

    public void changeToReadActivityHelper(String whatDo){

    }
}
