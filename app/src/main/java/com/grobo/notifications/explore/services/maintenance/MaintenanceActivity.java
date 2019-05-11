package com.grobo.notifications.explore.services.maintenance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.grobo.notifications.R;

public class MaintenanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        setBaseFragment(savedInstanceState);
    }

    private void setBaseFragment(Bundle savedInstanceState) {

        if (findViewById(R.id.frame_maintenance) != null) {

            if (savedInstanceState != null) {
                return;
            }
            NewMaintenanceFragment firstFragment = new NewMaintenanceFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_maintenance, firstFragment).commit();
        }

    }
}
