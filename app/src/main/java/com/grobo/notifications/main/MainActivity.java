package com.grobo.notifications.main;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.transition.TransitionInflater;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.grobo.notifications.Mess.CancelMealAdapter;
import com.grobo.notifications.Mess.MessFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
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
import com.grobo.notifications.admin.XPortal;
import com.grobo.notifications.admin.clubevents.ClubEventDetailFragment;
import com.grobo.notifications.admin.clubevents.ClubEventRecyclerAdapter;
import com.grobo.notifications.clubs.PorAdapter;

import com.grobo.notifications.utils.KeyboardUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE;
import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;
import static com.grobo.notifications.utils.Constants.BASE_URL;
import static com.grobo.notifications.utils.Constants.IS_ADMIN;
import static com.grobo.notifications.utils.Constants.KEY_FORCE_UPDATE;
import static com.grobo.notifications.utils.Constants.LOGIN_STATUS;
import static com.grobo.notifications.utils.Constants.ROLL_NUMBER;
import static com.grobo.notifications.utils.Constants.USER_BRANCH;
import static com.grobo.notifications.utils.Constants.USER_NAME;
import static com.grobo.notifications.utils.Constants.USER_YEAR;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        PorAdapter.OnPORSelectedListener, ClubEventRecyclerAdapter.OnEventSelectedListener, CancelMealAdapter.OnCancelSelectedListener {

    private SharedPreferences prefs;
    private AppBarConfiguration appBarConfiguration;

    private ProgressDialog progressDialog;
    private FirebaseRemoteConfig remoteConfig;
    private AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog( this );
        progressDialog.setIndeterminate( true );
        progressDialog.setCanceledOnTouchOutside( false );
        progressDialog.setCancelable( false );
        prefs = PreferenceManager.getDefaultSharedPreferences( this );

        if (!prefs.getBoolean(LOGIN_STATUS, false)) {
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);

            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_explore, R.id.nav_calender, R.id.nav_feed,
                    R.id.navigation_today, R.id.navigation_mess, R.id.navigation_notifications)
                    .setDrawerLayout(drawer)
                    .build();

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            View v = navigationView.getHeaderView(0);
            ((TextView) v.findViewById(R.id.user_name_nav_header)).setText(prefs.getString(USER_NAME, "Guest"));
            ((TextView) v.findViewById(R.id.user_email_nav_header)).setText(prefs.getString(ROLL_NUMBER, ""));
            ImageView profileImage = v.findViewById(R.id.user_image_nav_header);
            Glide.with(this)
                    .load(BASE_URL + "img/" + prefs.getString(ROLL_NUMBER, ROLL_NUMBER).toLowerCase() + ".jpg")
                    .centerCrop()
                    .placeholder(R.drawable.profile_photo)
                    .into(profileImage);
            profileImage.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));

            remoteConfig = FirebaseRemoteConfig.getInstance();

            subscribeFcmTopics();
        }
        KeyboardUtils.hideSoftInput(this);
        Log.e("jsonString", prefs.getString("jsonString", "none"));
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
        } else {
            MenuItem menuItem = menu.findItem(R.id.action_profile);
            menuItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_admin) {
            startActivity(new Intent(MainActivity.this, XPortal.class));
            return true;
        } else if (id == R.id.action_profile) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
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

        updateApp();
    }

    private void updateApp() {

        Log.e(getClass().getSimpleName(), String.valueOf(BuildConfig.VERSION_CODE));

        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {

            int updateType = 2;
            if (appUpdateInfo.availableVersionCode() > BuildConfig.VERSION_CODE + 3 || remoteConfig.getBoolean(KEY_FORCE_UPDATE))
                updateType = 1;

            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            } else if (updateType == 1 && appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, IMMEDIATE, this, 10101);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else if (updateType == 1 && appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, IMMEDIATE, this, 10101);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else if (updateType == 2 && appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(FLEXIBLE)) {

                if (prefs.getLong("last_update_prompt_time", 0) < (System.currentTimeMillis() - 24 * 60 * 60 * 1000)) {

                    appUpdateManager.registerListener(updatedListener);
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, FLEXIBLE, this, 10101);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }

                    prefs.edit().putLong("last_update_prompt_time", System.currentTimeMillis()).apply();
                }
            }

        });

    }

    private InstallStateUpdatedListener updatedListener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate();
        }
    };

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout),
                "An update has just been downloaded.", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", view -> {
            appUpdateManager.unregisterListener(updatedListener);
            appUpdateManager.completeUpdate();
        });
        snackbar.setActionTextColor(Color.BLUE);
        snackbar.show();
    }

    private void subscribeFcmTopics() {
        FirebaseMessaging fcm = FirebaseMessaging.getInstance();

        fcm.subscribeToTopic("all");
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

    @Override
    public void onCancelSelected(String id) {
        Toast.makeText( this, "hello", Toast.LENGTH_SHORT ).show();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        progressDialog.setMessage( "Cancelling your meals" );
        progressDialog.show();
        db.collection( "mess" ).document( PreferenceManager.getDefaultSharedPreferences( this ).getString( Constants.WEBMAIL, "" ) ).collection("cancel" ).document( id ).get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot=task.getResult();
                    List<Timestamp> days=(ArrayList<Timestamp>)documentSnapshot.get( "days" );
                    Calendar calendar=Calendar.getInstance();
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.clear();
                    calendar1.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ) );
                    if(days.size()==1)
                    {
                        db.collection( "mess" ).document( PreferenceManager.getDefaultSharedPreferences( getApplicationContext() ).getString( Constants.WEBMAIL, "" ) ).collection("cancel" ).document( id ).delete().addOnCompleteListener( new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                            }
                        } );
                    }
                    else {
                        List<Timestamp> update=new ArrayList<>(  );
                        Map<String, Object> map = new HashMap<>();
                        map.put( "full", documentSnapshot.get( "full" ) );
                        map.put( "timestamp", FieldValue.serverTimestamp() );
                        calendar1.add( Calendar.DATE,3 );
                        for(int i=0;i<days.size();i++)
                        {
                            if(days.get( i ).toDate().compareTo( calendar1.getTime())<0)
                                update.add( days.get( i ) );

                        }
                        if(update.size()==0)
                        {
                            db.collection( "mess" ).document( PreferenceManager.getDefaultSharedPreferences( getApplicationContext() ).getString( Constants.WEBMAIL, "" ) ).collection("cancel" ).document( id ).delete().addOnCompleteListener( new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                }
                            } );

                        }
                        else {
                            map.put( "days",update );
                            db.collection( "mess" ).document( PreferenceManager.getDefaultSharedPreferences( getApplicationContext() ).getString( Constants.WEBMAIL, "" ) ).collection("cancel" ).document( id ).update( map ).addOnCompleteListener( new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                }
                            } );

                        }
                    }
                }
            }
        } );

    }
}
