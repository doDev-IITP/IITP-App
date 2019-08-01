package com.grobo.notifications.timetable;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.grobo.notifications.R;

import java.util.Objects;

import static com.grobo.notifications.utils.Constants.USER_BRANCH;
import static com.grobo.notifications.utils.Constants.USER_YEAR;

public class TimetableActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final String TIMETABLE_URL = "https://timetable-grobo.firebaseio.com/";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_timetable );
        progressDialog = new ProgressDialog( this );
        progressDialog.setIndeterminate( true );
        progressDialog.setCanceledOnTouchOutside( false );

        Objects.requireNonNull( getSupportActionBar() ).setElevation( 0 );

        TimetableFragmentAdapter timetableFragmentAdapter = new TimetableFragmentAdapter( getSupportFragmentManager() );

        ViewPager mViewPager = findViewById( R.id.tt_container );
        mViewPager.setAdapter( timetableFragmentAdapter );

        TabLayout tabLayout = findViewById( R.id.tt_tab_layout );
        tabLayout.setupWithViewPager( mViewPager );

        if (getIntent().hasExtra( "day" )) {
            mViewPager.setCurrentItem( getIntent().getIntExtra( "day", 2 ) - 2 );
        }

        if(PreferenceManager.getDefaultSharedPreferences( this ).getString( "jsonString" , "").equals( "" )){
            getLoaderManager().initLoader( 1,null,this );
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_timetable, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.update_timetable) {
            getLoaderManager().restartLoader(  1, null, this );
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String>( this ) {

            String mUrl = TIMETABLE_URL + PreferenceManager.getDefaultSharedPreferences( getApplicationContext() ).getString( USER_YEAR, "" ) + "/" + PreferenceManager.getDefaultSharedPreferences( getApplicationContext() ).getString( USER_BRANCH, "" ).toLowerCase() + "/.json/";


            @Override
            protected void onStartLoading() {
                progressDialog.setMessage( "Updating..." );
                progressDialog.show();
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                String jsonResponse = TimetableUtility.doEverything( mUrl );

                return jsonResponse;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String jsonResponse) {
        progressDialog.dismiss();
        if (jsonResponse != null && !jsonResponse.isEmpty()) {
            PreferenceManager.getDefaultSharedPreferences( this ).edit().putString( "jsonString", jsonResponse ).apply();
            recreate();

        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }


    public class TimetableFragmentAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 5;
        private String tabTitles[] = new String[]{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};

        TimetableFragmentAdapter(FragmentManager fm) {
            super( fm );
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            return DayFragment.newInstance( position + 2 );
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

}
