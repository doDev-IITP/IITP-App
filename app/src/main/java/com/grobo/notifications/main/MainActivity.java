package com.grobo.notifications.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.grobo.notifications.R;
import com.grobo.notifications.account.LoginActivity;
import com.grobo.notifications.account.ProfileActivity;
import com.grobo.notifications.admin.XPortal;
import com.grobo.notifications.explore.ExploreFragment;
import com.grobo.notifications.explore.clubs.ClubDetailsFragment;
import com.grobo.notifications.explore.clubs.ClubsRecyclerAdapter;
import com.grobo.notifications.feed.FeedDetailFragment;
import com.grobo.notifications.feed.FeedFragment;
import com.grobo.notifications.feed.FeedRecyclerAdapter;
import com.grobo.notifications.notifications.NotificationsFragment;
import com.grobo.notifications.setting.SettingFragment;

import static com.grobo.notifications.utils.Constants.IS_ADMIN;
import static com.grobo.notifications.utils.Constants.LOGIN_STATUS;
import static com.grobo.notifications.utils.Constants.ROLL_NUMBER;
import static com.grobo.notifications.utils.Constants.USER_NAME;

public class MainActivity extends AppCompatActivity
        implements Preference.OnPreferenceChangeListener, FeedRecyclerAdapter.OnFeedSelectedListener, ClubsRecyclerAdapter.OnClubSelectedListener {

    private FragmentManager manager;
    private SharedPreferences prefs;
    private BottomNavigationView navigation;
    private int selectedNavItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.getBoolean(LOGIN_STATUS, false)) {
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            String title = prefs.getString(USER_NAME, "Notifications") + " (" + prefs.getString(ROLL_NUMBER, "IITP").toUpperCase() + ")";
            getSupportActionBar().setTitle(title);
        }

        manager = getSupportFragmentManager();

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setItemHorizontalTranslationEnabled(true);
        navigation.setSelectedItemId(prefs.getInt("item_id", R.id.nav_home));

        mainActivityRef = this;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //TODO: remove the exclamation after implementing PORs
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (prefs.getBoolean(IS_ADMIN, false)) {
            MenuItem menuItem = menu.findItem(R.id.action_admin);
            menuItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            return true;
        } else if (id == R.id.action_admin) {
            startActivity(new Intent(MainActivity.this, XPortal.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateFragment(Fragment fragment) {

        manager.popBackStackImmediate("later_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_layout_main, fragment);
        transaction.commit();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(LOGIN_STATUS)) {
            recreate();
        }
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    public static Activity mainActivityRef;

    @Override
    public void onFeedSelected(int id, View view, int position) {
        Fragment current = manager.findFragmentById(R.id.frame_layout_main);

        Fragment newFragment = new FeedDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("transitionName", "transition" + position);
        bundle.putInt("feedId", id);
        newFragment.setArguments(bundle);

        showFragmentWithTransition(current, newFragment, view, "transition" + position);
    }

    @Override
    public void onClubSelected(int id, View view, int position) {
        Fragment current = manager.findFragmentById(R.id.frame_layout_main);

        Fragment newFragment = new ClubDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("transitionName", "transition" + position);
        bundle.putInt("clubId", id);
        newFragment.setArguments(bundle);

        showFragmentWithTransition(current, newFragment, view, "transition" + position);
    }

    private void showFragmentWithTransition(Fragment current, Fragment newFragment, View sharedView, String sharedElementName) {

        current.setSharedElementReturnTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));
        current.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));

        newFragment.setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));
        newFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_bottom));

        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_main, newFragment);
        fragmentTransaction.addToBackStack("later_fragment");
        fragmentTransaction.addSharedElement(sharedView, sharedElementName);
        fragmentTransaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            selectedNavItemId = item.getItemId();
            Log.e("selectednav", String.valueOf(item.getItemId()));
            switch (item.getItemId()) {
                case R.id.nav_home:
                    updateFragment(new HomeFragment());
                    return true;
                case R.id.nav_feed:
                    updateFragment(new FeedFragment());
                    return true;
                case R.id.nav_notifications:
                    updateFragment(new NotificationsFragment());
                    return true;
                case R.id.nav_explore:
                    updateFragment(new ExploreFragment());
                    return true;
                case R.id.nav_setting:
                    updateFragment(new SettingFragment());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onDestroy() {
        prefs.edit().putInt("item_id", selectedNavItemId).apply();
        Log.e("savedinstance", String.valueOf(selectedNavItemId));
        super.onDestroy();
    }
}
