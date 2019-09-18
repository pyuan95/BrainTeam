package com.example.brainteam;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.backend.ListAdapterLauncher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddedTopicLauncher extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

        /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_topic_launcher);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read_launcher, menu);
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


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class CategoriesFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public CategoriesFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static CategoriesFragment newInstance() {
            CategoriesFragment fragment = new CategoriesFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.topics, container, false);
            RecyclerView recyclerView = rootView.findViewById(R.id.categoriesRecycler);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()); // Change later to custom manager.
            recyclerView.setLayoutManager(layoutManager);
            List<String> items = Arrays.asList(getResources().getStringArray(R.array.tossupCategories));
            ListAdapterLauncher adapter = new ListAdapterLauncher(getContext(), items);
            recyclerView.setAdapter(adapter);
            return rootView;
        }

        public ArrayList<String> selectTopicsFromList(View view)
        {
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Choose some animals");

            final String[] animals = {"horse", "cow", "camel", "sheep", "goat"};
            final ArrayList<String> checkedItems = new ArrayList<>();
            builder.setMultiChoiceItems(animals, null, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked)
                {
                    if (isChecked)
                    {
                        checkedItems.add(animals[which]);
                    }
                    else
                    {
                        checkedItems.remove(animals[which]);
                    }
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println("Checked Items: " + checkedItems);
                }
            });
            builder.setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return checkedItems;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DifficultiesFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public DifficultiesFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static DifficultiesFragment newInstance() {
            DifficultiesFragment fragment = new DifficultiesFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.difficulties, container, false);
            RecyclerView recyclerView = rootView.findViewById(R.id.difficultiesRecycler);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()); // Change later to custom manager.
            recyclerView.setLayoutManager(layoutManager);
            List<String> items = Arrays.asList(getResources().getStringArray(R.array.tossupDifficulties));
            ListAdapterLauncher adapter = new ListAdapterLauncher(getContext(), items);
            recyclerView.setAdapter(adapter);
            return rootView;
        }
    }


    public static class LauncherSettingsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public LauncherSettingsFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static LauncherSettingsFragment newInstance() {
            LauncherSettingsFragment fragment = new LauncherSettingsFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.difficulties, container, false);
            RecyclerView recyclerView = rootView.findViewById(R.id.difficultiesRecycler);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()); // Change later to custom manager.
            recyclerView.setLayoutManager(layoutManager);
            List<String> items = Arrays.asList(getResources().getStringArray(R.array.tossupDifficulties));
            ListAdapterLauncher adapter = new ListAdapterLauncher(getContext(), items);
            recyclerView.setAdapter(adapter);
            return rootView;
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return CategoriesFragment.newInstance();
                case 1:
                    return DifficultiesFragment.newInstance();
                default:
                    return LauncherSettingsFragment.newInstance();

            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
