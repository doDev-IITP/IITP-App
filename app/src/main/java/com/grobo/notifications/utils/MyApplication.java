package com.grobo.notifications.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.grobo.notifications.R;
import com.grobo.notifications.database.AppDatabase;
import com.grobo.notifications.work.DeleteWorker;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.grobo.notifications.utils.Constants.LOGIN_STATUS;
import static com.grobo.notifications.utils.Constants.ROLL_NUMBER;
import static com.grobo.notifications.utils.Constants.USER_BRANCH;
import static com.grobo.notifications.utils.Constants.USER_YEAR;

public class MyApplication extends Application {

    SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        createNotificationChannel();
        subscribeFcmTopics();

        scheduleTask();
        
        extractClub();
    }

    private void subscribeFcmTopics(){
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


    private void scheduleTask() {
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        PeriodicWorkRequest deleteRequest = new PeriodicWorkRequest.Builder(DeleteWorker.class, 1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("delete_feed", ExistingPeriodicWorkPolicy.KEEP, deleteRequest);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id), name, importance);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.setDescription(getString(R.string.default_notification_channel_description));
            channel.enableVibration(true);
            channel.setShowBadge(true);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void extractClub(){
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Log.e("package", String.valueOf(pInfo.versionName));

            if (!PreferenceManager.getDefaultSharedPreferences(this).getString("last_version", "0").equals(pInfo.versionName)) {

                String clubJson = utils.loadJSONFromAsset(this, "clubs.json");
                new utils.ExtractClubsJson(AppDatabase.getDatabase(this).clubDao(), clubJson).execute();

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
