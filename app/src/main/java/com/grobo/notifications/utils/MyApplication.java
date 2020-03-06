package com.grobo.notifications.utils;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {

    private final String LOG_TAG = getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        new Handler().postDelayed(this::fetchRemoteConfig, 500);
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
