package com.grobo.notifications.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.grobo.notifications.R;

import static com.grobo.notifications.utils.Constants.BASE_URL;
import static com.grobo.notifications.utils.Constants.IS_ADMIN;
import static com.grobo.notifications.utils.Constants.IS_QR_DOWNLOADED;
import static com.grobo.notifications.utils.Constants.LOGIN_STATUS;
import static com.grobo.notifications.utils.Constants.PHONE_NUMBER;
import static com.grobo.notifications.utils.Constants.ROLL_NUMBER;
import static com.grobo.notifications.utils.Constants.USER_BRANCH;
import static com.grobo.notifications.utils.Constants.USER_NAME;
import static com.grobo.notifications.utils.Constants.USER_YEAR;
import static com.grobo.notifications.utils.Constants.WEBMAIL;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {}

    private SharedPreferences prefs;
    private OnLogoutCallback callback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView email = view.findViewById(R.id.tv_profile_email);
        email.setText(prefs.getString(WEBMAIL, WEBMAIL));

        TextView name = view.findViewById(R.id.tv_profile_name);
        name.setText(prefs.getString(USER_NAME, USER_NAME));

        ImageView profilePic = view.findViewById(R.id.iv_profile_dp);
        Glide.with(this)
                .load(BASE_URL + "img/" + prefs.getString(ROLL_NUMBER, ROLL_NUMBER).toLowerCase() + ".jpg")
                .centerCrop()
                .placeholder(R.drawable.profile_photo)
                .into(profilePic);

        TextView phone = view.findViewById(R.id.tv_profile_phone);
        phone.setText(prefs.getString(PHONE_NUMBER, PHONE_NUMBER));

        Button button = view.findViewById(R.id.profile_logout_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return view;
    }

    private void logout(){

        FirebaseMessaging fcm = FirebaseMessaging.getInstance();
        fcm.unsubscribeFromTopic(prefs.getString(USER_BRANCH, "junk"));
        fcm.unsubscribeFromTopic(prefs.getString(USER_YEAR, "junk"));
        fcm.unsubscribeFromTopic(prefs.getString(USER_YEAR, "junk") + prefs.getString(USER_BRANCH, ""));
        fcm.unsubscribeFromTopic(prefs.getString(ROLL_NUMBER, "junk"));

        PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                .putString(WEBMAIL, "")
                .putString(ROLL_NUMBER, "")
                .putBoolean(LOGIN_STATUS, false)
                .putString(USER_NAME, "")
                .putString(PHONE_NUMBER, "")
                .putString("jsonString", "")
                .putBoolean(IS_QR_DOWNLOADED, false)
                .putBoolean(IS_ADMIN, false)
                .apply();

        callback.onLogout();
    }

    interface OnLogoutCallback {
        void onLogout();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLogoutCallback) {
            callback = (OnLogoutCallback) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }
}
