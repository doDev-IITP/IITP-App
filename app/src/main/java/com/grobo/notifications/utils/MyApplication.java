package com.grobo.notifications.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.grobo.notifications.R;
import com.grobo.notifications.work.DeleteWorker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {

    SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        createNotificationChannel();

        scheduleTask();

        remoteConfig();
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
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void remoteConfig() {
        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        Map<String, Object> remoteConfigDefaults = new HashMap<>();
        remoteConfigDefaults.put(Constants.TIMETABLE_URL, "https://timetable-grobo.firebaseio.com/");
        remoteConfigDefaults.put(Constants.MESS_MENU_URL, "https://i.ytimg.com/vi/OjIXzZ25tjA/maxresdefault.jpg");
        remoteConfigDefaults.put(Constants.KEY_UPDATE_TYPE, 0);

        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults);

        firebaseRemoteConfig.fetch(60).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e("Application", "remote config is fetched.");
                    firebaseRemoteConfig.activate();
            } else {
                Log.e("Application", "remote config fetch failed.");
            }
        });
    }
}
