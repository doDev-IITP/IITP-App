package com.grobo.notifications.services.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.TransitionInflater;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;
import com.grobo.notifications.utils.ImageViewerActivity;

public class LostAndFoundActivity extends AppCompatActivity implements LostAndFoundRecyclerAdapter.OnLostFoundSelectedListener {

    FloatingActionButton fab;
    FragmentManager manager;
    Fragment activeFragment;
    private int currentSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_and_found);

        getSupportActionBar().setTitle("Lost and Found");

        manager = getSupportFragmentManager();

        fab = findViewById(R.id.new_lost_found_fab);
        fab.setOnClickListener(v -> {
            showFragment(new NewLostAndFound());

//            int centerX = (v.getLeft() + v.getRight()) / 2;
//            int centerY = (v.getTop() + v.getBottom()) / 2;
//            float finalRadius = (float) Math.hypot((double) centerX, (double) centerY);
//
//            Animator animator = ViewAnimationUtils.createCircularReveal(findViewById(R.id.frame_lost_found), centerX, centerY, 0, finalRadius);
//            animator.setInterpolator(new AccelerateDecelerateInterpolator());
//            animator.setDuration(800);
//
//            animator.start();


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

    private void showFragment(Fragment newFragment) {

        Fragment current = manager.findFragmentById(R.id.frame_lost_found);
        if (current != null)
            current.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));
        newFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_bottom));

        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_lost_found, newFragment);
        fragmentTransaction.addToBackStack("later_fragment");
        fragmentTransaction.commit();
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        activeFragment = fragment;
        if (fragment instanceof NewLostAndFound) {
            fab.hide();
            activeFragment = fragment;
        }
    }

    @Override
    public void onBackPressed() {
        if (activeFragment instanceof NewLostAndFound) {
            if (fab.isOrWillBeHidden()) fab.show();
        }
        super.onBackPressed();
    }

    @Override
    public void onLostFoundSelected(String image, View view) {
        Intent i = new Intent(LostAndFoundActivity.this, ImageViewerActivity.class);
        i.putExtra("image_url", image);
        startActivity(i);
    }
}