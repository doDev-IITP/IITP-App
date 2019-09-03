package com.grobo.notifications.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.grobo.notifications.R;
import com.grobo.notifications.account.LoginActivity;
import com.grobo.notifications.account.ProfileActivity;
import com.grobo.notifications.admin.XPortal;
import com.grobo.notifications.admin.clubevents.ClubEventDetailFragment;
import com.grobo.notifications.admin.clubevents.ClubEventRecyclerAdapter;
import com.grobo.notifications.clubs.PorAdapter;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        PorAdapter.OnPORSelectedListener, ClubEventRecyclerAdapter.OnEventSelectedListener {

    private SharedPreferences prefs;
    private NavigationView navigationView;
    AppBarConfiguration appBarConfiguration;

    private FirebaseRemoteConfig remoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        prefs = PreferenceManager.getDefaultSharedPreferences( this );

        if (!prefs.getBoolean( LOGIN_STATUS, false )) {
            finish();
            startActivity( new Intent( MainActivity.this, LoginActivity.class ) );
        } else {

            Toolbar toolbar = findViewById( R.id.toolbar );
            setSupportActionBar( toolbar );

            DrawerLayout drawer = findViewById( R.id.drawer_layout );
            navigationView = findViewById( R.id.nav_view );

            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_explore, R.id.nav_calender, R.id.nav_feed,
                    R.id.navigation_today, R.id.navigation_mess, R.id.navigation_notifications )
                    .setDrawerLayout( drawer )
                    .build();

            NavController navController = Navigation.findNavController( this, R.id.nav_host_fragment );
            NavigationUI.setupActionBarWithNavController( this, navController, appBarConfiguration );
            NavigationUI.setupWithNavController( navigationView, navController );

//            navigationView.setNavigationItemSelectedListener(this);

            View v = navigationView.getHeaderView( 0 );
            ((TextView) v.findViewById( R.id.user_name_nav_header )).setText( prefs.getString( USER_NAME, "Guest" ) );
            ((TextView) v.findViewById( R.id.user_email_nav_header )).setText( prefs.getString( ROLL_NUMBER, "" ) );
            ImageView profileImage = v.findViewById( R.id.user_image_nav_header );
            Glide.with( this )
                    .load( BASE_URL + "img/" + prefs.getString( ROLL_NUMBER, ROLL_NUMBER ).toLowerCase() + ".jpg" )
                    .centerCrop()
                    .placeholder( R.drawable.profile_photo )
                    .into( profileImage );
            profileImage.setOnClickListener( view -> {
                startActivity( new Intent( MainActivity.this, ProfileActivity.class ) );
            } );

            remoteConfig = FirebaseRemoteConfig.getInstance();

            subscribeFcmTopics();
        }
        KeyboardUtils.hideSoftInput( this );
        Log.e("jsonString", prefs.getString("jsonString", "none"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!prefs.getBoolean( LOGIN_STATUS, false )) {
            finish();
            startActivity( new Intent( MainActivity.this, LoginActivity.class ) );
        }

        if (remoteConfig.getBoolean( KEY_UPDATE_REQUIRED )) {
            updateApp();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu( menu );
        if (prefs.getBoolean( IS_ADMIN, false )) {
            MenuItem menuItem = menu.findItem( R.id.action_admin );
            menuItem.setVisible( true );
        } else {
            MenuItem menuItem = menu.findItem( R.id.action_profile );
            menuItem.setVisible( true );
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_admin) {
            startActivity( new Intent( MainActivity.this, XPortal.class ) );
            return true;
        } else if (id == R.id.action_profile) {
            startActivity( new Intent( MainActivity.this, ProfileActivity.class ) );
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    private void showFragmentWithTransition(Fragment current, Fragment newFragment, View sharedView, String sharedElementName) {

//        current.setSharedElementReturnTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));
//        current.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));
//
//        newFragment.setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));
//        newFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_bottom));
//
//        FragmentTransaction fragmentTransaction = manager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout_main, newFragment);
//        fragmentTransaction.addToBackStack("later_fragment");
//        fragmentTransaction.addSharedElement(sharedView, sharedElementName);
//        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_timetable) {
            new Handler().postDelayed( () -> {
                    startActivity( new Intent( MainActivity.this, TimetableActivity.class ) );
            }, 300 );
            navigationView.setCheckedItem( R.id.nav_home );
        }
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    private void updateApp() {

        String currentVersion = remoteConfig.getString( KEY_CURRENT_VERSION );
        String appVersion = utils.getAppVersion( this );

        if (!TextUtils.equals( currentVersion, appVersion )) {
            final AlertDialog alertDialog = new AlertDialog.Builder( this )
                    .setTitle( "New version available" )
                    .setMessage( "Please, update app to new version to continue using.\nCurrent Version: " + appVersion + "\nNew Version: " + currentVersion )
                    .setPositiveButton( "Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            utils.openPlayStoreForApp( MainActivity.this );
                        }

                    } ).setCancelable( false ).create();

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


    private void subscribeFcmTopics() {
        FirebaseMessaging fcm = FirebaseMessaging.getInstance();

        fcm.subscribeToTopic( "all" );
        fcm.subscribeToTopic( "dev" );
        if (prefs.getBoolean( LOGIN_STATUS, false )) {
            fcm.subscribeToTopic( prefs.getString( USER_BRANCH, "junk" ) );
            fcm.subscribeToTopic( prefs.getString( USER_YEAR, "junk" ) );
            fcm.subscribeToTopic( prefs.getString( USER_YEAR, "junk" ) + prefs.getString( USER_BRANCH, "" ) );
            fcm.subscribeToTopic( prefs.getString( ROLL_NUMBER, "junk" ) );
        }
    }

    @Override
    public void onEventSelected(String eventId) {
        Fragment fragment = new ClubEventDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString( "clubId", eventId );
        fragment.setArguments( bundle );

//        manager.beginTransaction()
//                .replace(R.id.frame_layout_main, fragment)
//                .addToBackStack("later_fragment")
//                .commit();
    }

    @Override
    public void onPORSelected(String userId) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController( this, R.id.nav_host_fragment );
        return NavigationUI.navigateUp( navController, appBarConfiguration )
                || super.onSupportNavigateUp();
    }
}
