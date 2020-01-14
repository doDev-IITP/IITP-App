package com.grobo.notifications.account;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.grobo.notifications.R;
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
    public void onPORSelected(PORItem porItem) {
        Intent i = new Intent(ProfileActivity.this, XPortal.class);
        i.putExtra("data", porItem);
        startActivity(i);
    }
}
