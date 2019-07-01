package com.grobo.notifications.services.maintenance;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;

public class MaintenanceActivity extends AppCompatActivity implements MaintenanceRecyclerAdapter.OnItemSelectedListener {

    FloatingActionButton fab;
    FragmentManager manager;
    Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        getSupportActionBar().setTitle("Maintenance");

        manager = getSupportFragmentManager();

        fab = findViewById(R.id.new_maintenance_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment current = manager.findFragmentById(R.id.frame_maintenance);
                Fragment next = new NewMaintenanceFragment();
                showFragmentWithTransition(current, next);
            }
        });

        setBaseFragment(savedInstanceState);
    }

    private void setBaseFragment(Bundle savedInstanceState) {
        if (findViewById(R.id.frame_maintenance) != null) {

            if (savedInstanceState != null) {
                return;
            }
            MaintenanceFragment firstFragment = new MaintenanceFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_maintenance, firstFragment).commit();
        }
    }

    private void showFragmentWithTransition(Fragment current, Fragment newFragment) {
        current.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));
        newFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_bottom));

        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_maintenance, newFragment);
        fragmentTransaction.addToBackStack("later_fragment");
        fragmentTransaction.commit();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        activeFragment = fragment;
        if (fragment instanceof NewMaintenanceFragment){
            fab.hide();
            activeFragment = fragment;
        }
    }

    @Override
    public void onBackPressed() {
        if (activeFragment instanceof NewMaintenanceFragment){
            if (fab.isOrWillBeHidden()) fab.show();
        }
        super.onBackPressed();
    }

    @Override
    public void onItemSelected(int id) {

    }
}
