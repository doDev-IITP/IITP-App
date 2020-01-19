package com.grobo.notifications.main;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.grobo.notifications.BuildConfig;
import com.grobo.notifications.R;
import com.grobo.notifications.account.LoginActivity;
import com.grobo.notifications.account.ProfileActivity;
import com.grobo.notifications.admin.clubevents.ClubEventDetailFragment;
import com.grobo.notifications.admin.clubevents.ClubEventRecyclerAdapter;
import com.grobo.notifications.clubs.PorAdapter;
import com.grobo.notifications.services.agenda.AgendaActivity;
import com.grobo.notifications.utils.KeyboardUtils;

import static com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE;
import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;
import static com.grobo.notifications.utils.Constants.BASE_URL;
import static com.grobo.notifications.utils.Constants.KEY_FORCE_UPDATE;
import static com.grobo.notifications.utils.Constants.LOGIN_STATUS;
import static com.grobo.notifications.utils.Constants.ROLL_NUMBER;
import static com.grobo.notifications.utils.Constants.USER_BRANCH;
import static com.grobo.notifications.utils.Constants.USER_NAME;
import static com.grobo.notifications.utils.Constants.USER_YEAR;

public class MainActivity extends AppCompatActivity implements PorAdapter.OnPORSelectedListener,
        ClubEventRecyclerAdapter.OnEventSelectedListener {

    private final String LOG_TAG = getClass().getSimpleName();

    private SharedPreferences prefs;
    private AppBarConfiguration appBarConfiguration;

    private FirebaseRemoteConfig remoteConfig;
    private AppUpdateManager appUpdateManager;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.getBoolean(LOGIN_STATUS, false)) {
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);

            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_explore, R.id.nav_calender, R.id.nav_feed, R.id.nav_notification,
                    R.id.nav_timetable, R.id.nav_links, R.id.nav_services, R.id.nav_setting)
                    .setDrawerLayout(drawer)
                    .build();

            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            View v = navigationView.getHeaderView(0);
            new Handler().postDelayed(() -> populateHeaderView(v), 200);

            remoteConfig = FirebaseRemoteConfig.getInstance();

            new Handler().postDelayed(this::subscribeFcmTopics, 1000);

            handleIntent(getIntent());
        }
        KeyboardUtils.hideSoftInput(this);
    }

    private void handleIntent(Intent appLinkIntent) {
        String appLinkAction = appLinkIntent.getAction();
        String appLinkData = appLinkIntent.getDataString();

        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {

            if (appLinkData.contains("/feed/")) {
                String feedId = appLinkData.substring(appLinkData.lastIndexOf("/") + 1);

                Bundle bundle = new Bundle();
                bundle.putString("id", feedId);
                navController.navigate(R.id.nav_feed_detail, bundle);
            } else if (appLinkData.contains("/club/")) {
                String id = appLinkData.substring(appLinkData.lastIndexOf("/") + 1);

                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                navController.navigate(R.id.nav_club_detail, bundle);
            } else if (appLinkData.contains("/notification/")) {
                String time = appLinkData.substring(appLinkData.lastIndexOf("/") + 1);

                Bundle bundle = new Bundle();
                bundle.putString("time", time);

                navController.navigate(R.id.nav_notification, bundle);
            } else if (appLinkData.contains("/agenda/")) {
                String id = appLinkData.substring(appLinkData.lastIndexOf("/") + 1);

                Intent i = new Intent(MainActivity.this, AgendaActivity.class);
                i.putExtra("agendaId", id);
                startActivity(i);
            }
        }
    }

    private void populateHeaderView(View v) {
        if (v != null) {
            ((TextView) v.findViewById(R.id.user_name_nav_header)).setText(prefs.getString(USER_NAME, "Guest"));
            ((TextView) v.findViewById(R.id.user_email_nav_header)).setText(prefs.getString(ROLL_NUMBER, ""));
            ImageView profileImage = v.findViewById(R.id.user_image_nav_header);
            Glide.with(this)
                    .load(BASE_URL + "img/" + prefs.getString(ROLL_NUMBER, ROLL_NUMBER).toLowerCase() + ".jpg")
                    .centerCrop()
                    .placeholder(R.drawable.profile_photo)
                    .into(profileImage);
            profileImage.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            return true;
        } else if (id == R.id.action_mail) {
            new CustomTabsIntent.Builder().enableUrlBarHiding().build()
                    .launchUrl(MainActivity.this, Uri.parse("https://mail.iitp.ac.in"));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    protected void onResume() {
        super.onResume();
        if (!prefs.getBoolean(LOGIN_STATUS, false)) {
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        new Handler().postDelayed(this::updateApp, 1000);
    }

    private void updateApp() {
        Log.e(getClass().getSimpleName(), String.valueOf(BuildConfig.VERSION_CODE));

        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {

            int updateType = 2;
            if (appUpdateInfo.availableVersionCode() > BuildConfig.VERSION_CODE + 3
                    || remoteConfig.getBoolean(KEY_FORCE_UPDATE)) updateType = 1;

            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            } else if (updateType == 1 && appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, IMMEDIATE, this, 1011);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else if (updateType == 1 && appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, IMMEDIATE, this, 1011);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else if (updateType == 2 && appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(FLEXIBLE)) {

                if (prefs.getLong("last_update_prompt_time", 0) < (System.currentTimeMillis() - 24 * 60 * 60 * 1000)) {

                    appUpdateManager.registerListener(updatedListener);
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, FLEXIBLE, this, 1011);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }

                    prefs.edit().putLong("last_update_prompt_time", System.currentTimeMillis()).apply();
                }
            }
        });
    }

    private InstallStateUpdatedListener updatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState installState) {
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            } else if (installState.installStatus() == InstallStatus.INSTALLED) {
                if (appUpdateManager != null) {
                    appUpdateManager.unregisterListener(updatedListener);
                }
            } else {
                Log.i(getClass().getSimpleName(), "InstallStateUpdatedListener: state: " + installState.installStatus());
            }
        }
    };

    private void popupSnackbarForCompleteUpdate() {
        if (findViewById(R.id.drawer_layout) != null) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout),
                    "An update has just been downloaded.", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("INSTALL", view -> {
                if (appUpdateManager != null)
                    appUpdateManager.completeUpdate();
            });
            snackbar.setActionTextColor(Color.CYAN);
            snackbar.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1011) {
            if (resultCode != RESULT_OK) {
                Log.e(getClass().getSimpleName(), "onActivityResult: app download failed");
            }
        }
    }

    private void subscribeFcmTopics() {
        FirebaseMessaging fcm = FirebaseMessaging.getInstance();

        fcm.subscribeToTopic("all");
        if (prefs != null && prefs.getBoolean(LOGIN_STATUS, false)) {
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
    }

    @Override
    public void onPORSelected(String userId) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
