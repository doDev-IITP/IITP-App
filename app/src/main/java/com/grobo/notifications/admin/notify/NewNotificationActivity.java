package com.grobo.notifications.admin.notify;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.grobo.notifications.R;

public class NewNotificationActivity extends AppCompatActivity {

    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notification);

        setBaseFragment();
    }

    private void setBaseFragment() {
        if (findViewById(R.id.frame_new_notification) != null) {
            SelectAudienceFragment firstFragment = new SelectAudienceFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.frame_new_notification, firstFragment).commit();
        }
    }



}
