package com.grobo.notifications.admin;

import android.os.Bundle;
import android.transition.TransitionInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.grobo.notifications.R;

public class XPortal extends AppCompatActivity implements XPortalFragment.OnPORSelectedListener {

    FragmentManager manager;
    Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xportal);

        getSupportActionBar().setTitle("Admin Portal");

        manager = getSupportFragmentManager();

        setBaseFragment(savedInstanceState);
    }

    private void setBaseFragment(Bundle savedInstanceState) {
        if (findViewById(R.id.frame_layout_admin) != null) {

            if (savedInstanceState != null) {
                return;
            }
            XPortalFragment firstFragment = new XPortalFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_admin, firstFragment).commit();
        }
    }

    private void showFragmentWithTransition(Fragment current, Fragment newFragment) {
        current.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));
        newFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_bottom));

        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_admin, newFragment);
        fragmentTransaction.addToBackStack("later_fragment");
        fragmentTransaction.commit();
    }

    @Override
    public void onPORSelected(String PORId) {
        Fragment current = manager.findFragmentById(R.id.frame_layout_admin);
        Fragment next = new CoordinatorFragment();
        showFragmentWithTransition(current, next);
    }
}
