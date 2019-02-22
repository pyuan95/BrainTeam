package com.example.brainteam;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String buttonName = "com.example.brainteam.MainActivity.buttonName";
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeToReadActivity(View view)
    {
        if (((Button) view).getText().equals("Read random tossups"))
        {
            final ArrayList<String> selectedItems = new ArrayList<String>();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.selectRandomTossupsTitle).setMultiChoiceItems(R.array.tossupCategories, null, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (isChecked)
                    {
                        Resources res = getResources();
                        selectedItems.add(res.getStringArray(R.array.tossupCategories)[which]); // adds selected items
                    }
                    else
                    {
                        Resources res = getResources();
                        selectedItems.remove(res.getStringArray(R.array.tossupCategories)[which]); // deletes selected items
                    }
                }
            }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    changeToReadActivityHelper(selectedItems);
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // If the user clicks cancel, do nothing.
                }
            });
            builder.create().show();
        }
        else {
            ArrayList<String> arr = new ArrayList<String>();
            arr.add((String) ((Button) view).getText()); // Add the text of the button to the String array that will be passed to the Read Activity.
            changeToReadActivityHelper(arr);
        }
    }

    public void changeToReadActivityHelper(ArrayList<String> arr)
    {
        Intent intent = new Intent(this, ReadActivity.class);
        intent.putExtra(buttonName, arr);
        startActivity(intent);
    }

}
