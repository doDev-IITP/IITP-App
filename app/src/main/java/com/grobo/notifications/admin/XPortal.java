package com.grobo.notifications.admin;

import android.os.Bundle;
import android.transition.TransitionInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.grobo.notifications.R;

import static com.grobo.notifications.utils.Constants.COORDINATOR;
import static com.grobo.notifications.utils.Constants.CR;
import static com.grobo.notifications.utils.Constants.LEAD;
import static com.grobo.notifications.utils.Constants.SECRETARY;
import static com.grobo.notifications.utils.Constants.SUB_COORDINATOR;
import static com.grobo.notifications.utils.Constants.VP;

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
        activeFragment = newFragment;
        current.setExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));
        newFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_bottom));

        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_admin, newFragment);
        fragmentTransaction.addToBackStack("later_fragment");
        fragmentTransaction.commit();
    }

    @Override
    public void onPORSelected(String club, String power) {
        Fragment current = manager.findFragmentById(R.id.frame_layout_admin);
        Fragment next;
        switch (power) {
            case SUB_COORDINATOR:
                next = new CoordinatorFragment();
                break;
            case LEAD:
                next = new CoordinatorFragment();
                break;
            case COORDINATOR:
                next = new CoordinatorFragment();
                break;
            case SECRETARY:
                next = new SecretaryFragment();
                break;
            case CR:
                next = new CRFragment();
                break;
            case VP:
                next = new VPFragment();
                break;
            default:
                next = new CoordinatorFragment();
        }
        showFragmentWithTransition(current, next);
    }
}
