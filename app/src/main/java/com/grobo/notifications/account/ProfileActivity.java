package com.grobo.notifications.account;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;
import com.grobo.notifications.account.por.ClaimPORFragment;
import com.grobo.notifications.account.por.PORItem;
import com.grobo.notifications.account.por.PORRecyclerAdapter;
import com.grobo.notifications.admin.XPortal;

public class ProfileActivity extends AppCompatActivity implements ProfileFragment.OnLogoutCallback, PORRecyclerAdapter.OnPORSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setBaseFragment();

        getWindow().setStatusBarColor(Color.parseColor("#185a9d"));

        findViewById(R.id.back_button).setOnClickListener(v -> ProfileActivity.super.onBackPressed());
    }

    private void setBaseFragment() {

        if (findViewById(R.id.fragment_container) != null) {
            ProfileFragment firstFragment = new ProfileFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }

    }

    @Override
    public void onLogout() {
        finish();
    }

    @Override
    public void onAddPorSelected() {
        ClaimPORFragment fragment = new ClaimPORFragment();

        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (current != null)
            current.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_left));

        fragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_right));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).addToBackStack(fragment.getTag()).commit();
    }

    @Override
    public void onPORSelected(PORItem porItem) {
        if (porItem.getCode() != 0) {
            Intent i = new Intent(ProfileActivity.this, XPortal.class);
            i.putExtra("por", porItem);
            startActivity(i);
        } else {
            Toast.makeText(this, "POR not approved yet !!!", Toast.LENGTH_LONG).show();
        }
    }
}
