package com.grobo.notifications.explore.services.lostandfound;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;

public class LostAndFoundActivity extends AppCompatActivity implements LostAndFoundRecyclerAdapter.OnItemSelectedListener {

    FloatingActionButton fab;
    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_and_found);

        manager = getSupportFragmentManager();

        fab = findViewById(R.id.new_lost_found_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment current = manager.findFragmentById(R.id.frame_lost_found);
                Fragment next = new NewLostAndFound();
                showFragmentWithTransition(current, next);
            }
        });

        setBaseFragment(savedInstanceState);
    }

    private void setBaseFragment(Bundle savedInstanceState) {
        if (findViewById(R.id.frame_lost_found) != null) {

            if (savedInstanceState != null) {
                return;
            }

            LostAndFoundFragment firstFragment = new LostAndFoundFragment();
            firstFragment.setArguments(getIntent().getExtras());
            manager.beginTransaction()
                    .add(R.id.frame_lost_found, firstFragment).commit();
        }
    }

    private void showFragmentWithTransition(Fragment current, Fragment newFragment) {
        current.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));
        newFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_bottom));

        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_lost_found, newFragment);
        fragmentTransaction.addToBackStack("later_fragment");
        fragmentTransaction.commit();
    }

    @Override
    public void onItemSelected(int id) {
        //TODO: start a new fragment and show entry data
    }
}