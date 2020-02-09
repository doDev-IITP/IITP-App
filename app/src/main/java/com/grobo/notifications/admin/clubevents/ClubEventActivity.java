package com.grobo.notifications.admin.clubevents;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.grobo.notifications.R;

public class ClubEventActivity extends AppCompatActivity {

    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_event);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Events");

        manager = getSupportFragmentManager();

        setBaseFragment();
    }

    private void setBaseFragment() {
        if (findViewById(R.id.frame_club_event) != null) {
            ClubEventFragment firstFragment = new ClubEventFragment();
            firstFragment.setArguments(getIntent().getExtras());
            manager.beginTransaction().add(R.id.frame_club_event, firstFragment).commit();
        }
    }
}
