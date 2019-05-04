package com.grobo.notifications.explore.services.lostandfound;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.grobo.notifications.R;

public class LostAndFoundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_and_found);

        setBaseFragment(savedInstanceState);
    }

    private void setBaseFragment(Bundle savedInstanceState) {

        if (findViewById(R.id.frame_lost_found) != null) {

            if (savedInstanceState != null) {
                return;
            }
            LostAndFoundFragment firstFragment = new LostAndFoundFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_lost_found, firstFragment).commit();
        }

    }
}
