package com.grobo.notifications.services.complaints;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;

public class ComplaintsActivity extends AppCompatActivity implements ComplaintsRecyclerAdapter.OnComplaintSelectedListener {

    FloatingActionButton fab;
    FragmentManager manager;
    Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);

        getSupportActionBar().setTitle("Complaints");

        manager = getSupportFragmentManager();

        fab = findViewById(R.id.new_complain_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment current = manager.findFragmentById(R.id.frame_complaints);
                Fragment next = new NewComplainFragment();
                showFragmentWithTransition(current, next);
            }
        });

        setBaseFragment();
    }

    private void setBaseFragment() {
        if (findViewById(R.id.frame_complaints) != null) {

            ComplaintsFragment firstFragment = new ComplaintsFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_complaints, firstFragment).commit();
        }
    }

    private void showFragmentWithTransition(Fragment current, Fragment newFragment) {
        current.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));
        newFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_bottom));

        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_complaints, newFragment);
        fragmentTransaction.addToBackStack("later_fragment");
        fragmentTransaction.commit();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        activeFragment = fragment;
        if (fragment instanceof NewComplainFragment){
            fab.hide();
        }
    }

    @Override
    public void onBackPressed() {
        if (activeFragment instanceof NewComplainFragment){
            if (fab.isOrWillBeHidden()) fab.show();
        }
        super.onBackPressed();
    }

    @Override
    public void onComplaintSelected(String id) {

    }
}
