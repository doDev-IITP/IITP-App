package com.grobo.notifications.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.grobo.notifications.Mess.MessFragment;
import com.grobo.notifications.R;
import com.grobo.notifications.account.LoginActivity;
import com.grobo.notifications.account.ProfileActivity;
import com.grobo.notifications.admin.XPortal;
import com.grobo.notifications.admin.clubevents.ClubEventDetailFragment;
import com.grobo.notifications.admin.clubevents.ClubEventRecyclerAdapter;
import com.grobo.notifications.clubs.ClubDetailsFragment;
import com.grobo.notifications.clubs.ClubsFragment;
import com.grobo.notifications.clubs.ClubsRecyclerAdapter;
import com.grobo.notifications.clubs.PorAdapter;
import com.grobo.notifications.clubs.PorItem;
import com.grobo.notifications.feed.FeedDetailFragment;
import com.grobo.notifications.feed.FeedFragment;
import com.grobo.notifications.feed.FeedRecyclerAdapter;
import com.grobo.notifications.notifications.NotificationsFragment;
import com.grobo.notifications.services.ServicesFragment;
import com.grobo.notifications.setting.SettingFragment;
import com.grobo.notifications.timetable.TimetableActivity;
import com.grobo.notifications.utils.KeyboardUtils;
import com.grobo.notifications.utils.utils;

import static com.grobo.notifications.utils.Constants.BASE_URL;
import static com.grobo.notifications.utils.Constants.IS_ADMIN;
import static com.grobo.notifications.utils.Constants.KEY_CURRENT_VERSION;
import static com.grobo.notifications.utils.Constants.KEY_UPDATE_REQUIRED;
import static com.grobo.notifications.utils.Constants.LOGIN_STATUS;
import static com.grobo.notifications.utils.Constants.ROLL_NUMBER;
import static com.grobo.notifications.utils.Constants.USER_BRANCH;
import static com.grobo.notifications.utils.Constants.USER_NAME;
import static com.grobo.notifications.utils.Constants.USER_YEAR;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Preference.OnPreferenceChangeListener,
        FeedRecyclerAdapter.OnFeedSelectedListener, ClubsRecyclerAdapter.OnClubSelectedListener,
        PorAdapter.OnCategorySelectedListener, ClubEventRecyclerAdapter.OnEventSelectedListener {

    private FragmentManager manager;
    private SharedPreferences prefs;
    private NavigationView navigationView;
    private int currentFragment;
    private Handler handler;
    private Runnable runnable = null;
    private Runnable runnable2 = null;
    private int state = 0;

    private FirebaseRemoteConfig remoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.getBoolean(LOGIN_STATUS, false)) {
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {

            manager = getSupportFragmentManager();


            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);

            toggle.syncState();
            navigationView.setNavigationItemSelectedListener(this);
            View v = navigationView.getHeaderView(0);
            navigationView.setCheckedItem(R.id.nav_home);
            currentFragment = R.id.nav_home;

            setBaseFragment(savedInstanceState);

            ((TextView) v.findViewById(R.id.user_name_nav_header)).setText(prefs.getString(USER_NAME, "Guest"));
            ((TextView) v.findViewById(R.id.user_email_nav_header)).setText(prefs.getString(ROLL_NUMBER, ""));
            ImageView profileImage = v.findViewById(R.id.user_image_nav_header);
            Glide.with(this)
                    .load(BASE_URL + "img/" + prefs.getString(ROLL_NUMBER, ROLL_NUMBER).toLowerCase() + ".jpg")
                    .centerCrop()
                    .placeholder(R.drawable.profile_photo)
                    .into(profileImage);
            profileImage.setOnClickListener(view -> {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            });

            handler = new Handler();
            runnable = () -> startActivity(new Intent(getApplicationContext(), TimetableActivity.class));
            runnable2 = () -> navigationView.setCheckedItem(currentFragment);
            state = 1;

            remoteConfig = FirebaseRemoteConfig.getInstance();

            subscribeFcmTopics();
        }
        KeyboardUtils.hideSoftInput(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!prefs.getBoolean(LOGIN_STATUS, false)) {
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {
            updateApp();
        }

    }

    private void setBaseFragment(Bundle savedInstanceState) {
        if (findViewById(R.id.frame_layout_main) != null) {

            if (savedInstanceState != null) {
                return;
            }
            HomeFragment firstFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_main, firstFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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
        if (item.getItemId() == R.id.action_admin) {
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

    @Override
    public void onFeedSelected(String id, View view, int position) {
        Fragment current = manager.findFragmentById(R.id.frame_layout_main);

        Fragment newFragment = new FeedDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("transitionName", "transition" + position);
        bundle.putString("id", id);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.nav_home:
                currentFragment = R.id.nav_home;
                updateFragment(new HomeFragment());
                break;
            case R.id.nav_feed:
                currentFragment = R.id.nav_feed;
                updateFragment(new FeedFragment());
                break;
            case R.id.nav_notifications:
                currentFragment = R.id.nav_notifications;
                updateFragment(new NotificationsFragment());
                break;
            case R.id.nav_explore:
                currentFragment = R.id.nav_explore;
                updateFragment(new ClubsFragment());
                break;
            case R.id.nav_timetable:
                handler.postDelayed(runnable, 300);
                navigationView.setCheckedItem(R.id.nav_home);
                break;
            case R.id.nav_calender:
                updateFragment(new CalenderFragment());
                break;
            case R.id.nav_mess:
                currentFragment = R.id.nav_mess;
                updateFragment(new MessFragment());
                break;
            case R.id.nav_internship:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                //updateFragment(new InternshipFragment() );
                break;
            case R.id.nav_tech:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
//                updateFragment(new SettingFragment());
                break;
            case R.id.nav_exam:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
//                updateFragment( new ExamFragment() );
                break;
            case R.id.nav_links:
                currentFragment = R.id.nav_links;
                updateFragment(new LinksFragment());
                break;
            case R.id.nav_services:
                currentFragment = R.id.nav_services;
                updateFragment(new ServicesFragment());
                break;
            case R.id.nav_setting:
                currentFragment = R.id.nav_setting;
                updateFragment(new SettingFragment());
                break;
            case R.id.nav_fresher:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_virtual:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
        }
        handler.postDelayed(runnable2, 300);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClubSelected(String id, View view, int position) {
        Fragment current = manager.findFragmentById(R.id.frame_layout_main);

        Fragment newFragment = new ClubDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("transitionName", "transition" + position);
        bundle.putString("clubId", id);
        newFragment.setArguments(bundle);

        showFragmentWithTransition(current, newFragment, view, "transition" + position);
    }

    @Override
    protected void onDestroy() {
        if (state == 1) {
            handler.removeCallbacks(runnable);
            handler.removeCallbacks(runnable2);

        }
        super.onDestroy();
    }

    private void updateApp() {

        String currentVersion = remoteConfig.getString(KEY_CURRENT_VERSION);
        String appVersion = utils.getAppVersion(this);

        if (!TextUtils.equals(currentVersion, appVersion)) {
            final AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("New version available")
                    .setMessage("Please, update app to new version to continue using.\nCurrent Version: " + appVersion + "\nNew Version: " + currentVersion)
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            utils.openPlayStoreForApp(MainActivity.this);
                        }

                    }).setCancelable(false).create();

            alertDialog.show();
        }

//        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
//        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
//
//        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//
//                try {
//                    appUpdateManager.startUpdateFlowForResult(
//                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
//                            appUpdateInfo,
//                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
//                            AppUpdateType.IMMEDIATE,
//                            // The current activity making the update request.
//                            this,
//                            // Include a request code to later monitor this update request.
//                            10101);
//                } catch (IntentSender.SendIntentException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });

    }

//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 10101) {
//            if (resultCode != RESULT_OK) {
//                Log.e("log","Update flow failed! Result code: " + resultCode);
//                // If the update is cancelled or fails,
//                // you can request to start the update again.
//            }
//        }
//    }

    @Override
    public void onNameSelected(PorItem pos) {

    }


    private void subscribeFcmTopics() {
        FirebaseMessaging fcm = FirebaseMessaging.getInstance();

        fcm.subscribeToTopic("all");
        fcm.subscribeToTopic("dev");
        if (prefs.getBoolean(LOGIN_STATUS, false)) {
            fcm.subscribeToTopic(prefs.getString(USER_BRANCH, "junk"));
            fcm.subscribeToTopic(prefs.getString(USER_YEAR, "junk"));
            fcm.subscribeToTopic(prefs.getString(USER_YEAR, "junk") + prefs.getString(USER_BRANCH, ""));
            fcm.subscribeToTopic(prefs.getString(ROLL_NUMBER, "junk"));
        }
    }

    @Override
    public void onEventSelected(String eventId) {
        Fragment fragment = new ClubEventDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("clubId", eventId);
        fragment.setArguments(bundle);

        manager.beginTransaction()
                .replace(R.id.frame_layout_main, fragment)
                .addToBackStack("later_fragment")
                .commit();
    }
}
