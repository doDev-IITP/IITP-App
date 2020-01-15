package com.grobo.notifications.utils;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.grobo.notifications.work.DeleteWorker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {

    private final String LOG_TAG = getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        new Handler().postDelayed(() -> {
            scheduleTask();
            fetchRemoteConfig();
        }, 500);
    }

    private void scheduleTask() {
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        PeriodicWorkRequest deleteRequest = new PeriodicWorkRequest.Builder(DeleteWorker.class, 1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.HOURS)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("delete_feed", ExistingPeriodicWorkPolicy.KEEP, deleteRequest);
    }

    private void fetchRemoteConfig() {
        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        Map<String, Object> remoteConfigDefaults = new HashMap<>();
        remoteConfigDefaults.put(Constants.TIMETABLE_URL, "https://timetable-grobo.firebaseio.com/");
        remoteConfigDefaults.put(Constants.MESS_MENU_URL, "https://i.ytimg.com/vi/OjIXzZ25tjA/maxresdefault.jpg");
        remoteConfigDefaults.put(Constants.KEY_FORCE_UPDATE, false);

        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults);

        firebaseRemoteConfig.fetch(60).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i(LOG_TAG, "remote config is fetched.");
                    firebaseRemoteConfig.activate();
            } else {
                Log.e(LOG_TAG, "remote config fetch failed.");
            }
        });
    }
}
