package com.grobo.notifications.account;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import static com.grobo.notifications.utils.Constants.LOGIN_STATUS;
import static com.grobo.notifications.utils.Constants.USER_NAME;
import static com.grobo.notifications.utils.Constants.WEBMAIL;

public class SessionManager {

    private SharedPreferences preferences;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void createLoginSession(String name, String email) {
        preferences.edit().putBoolean(LOGIN_STATUS, true)
                .putString(USER_NAME, name)
                .putString(WEBMAIL, email)
                .apply();
    }

    public void setLoginStatus(Context context, boolean loggedIn) {
        preferences.edit().putBoolean(LOGIN_STATUS, loggedIn).apply();
    }

    public boolean getLoginStatus(Context context) {
        return preferences.getBoolean(LOGIN_STATUS, false);
    }
}