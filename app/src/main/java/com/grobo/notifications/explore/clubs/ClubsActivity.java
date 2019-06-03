package com.grobo.notifications.explore.clubs;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.grobo.notifications.R;

public class ClubsActivity extends AppCompatActivity implements ClubsRecyclerAdapter.OnClubSelectedListener {

    FragmentManager manager;
    Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubs);

        getSupportActionBar().setTitle("Explore");

        manager = getSupportFragmentManager();

        setBaseFragment(savedInstanceState);
    }

    private void setBaseFragment(Bundle savedInstanceState) {
        if (findViewById(R.id.frame_clubs) != null) {

            if (savedInstanceState != null) {
                return;
            }

            ClubsFragment firstFragment = new ClubsFragment();
            firstFragment.setArguments(getIntent().getExtras());
            manager.beginTransaction()
                    .add(R.id.frame_clubs, firstFragment).commit();
        }
    }

    private void showFragmentWithTransition(Fragment current, Fragment newFragment, View sharedView, String sharedElementName) {
        current.setSharedElementReturnTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));
        current.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));

        newFragment.setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));
        newFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_bottom));

        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_clubs, newFragment);
        fragmentTransaction.addToBackStack("later_fragment");
        fragmentTransaction.addSharedElement(sharedView, sharedElementName);
        fragmentTransaction.commit();
    }

    @Override
    public void onClubSelected(int id, View view, int position) {
        Fragment current = manager.findFragmentById(R.id.frame_clubs);

        Fragment newFragment = new ClubDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("transitionName", "transition" + position);
        bundle.putInt("clubId", id);
        newFragment.setArguments(bundle);

        showFragmentWithTransition(current, newFragment, view, "transition" + position);
    }
}
