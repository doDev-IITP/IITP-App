package com.grobo.notifications.mail;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.grobo.notifications.R;

public class EmailActivity extends AppCompatActivity {

    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Webmail");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressbar = findViewById(R.id.progressbar_web);

        setBaseFragment();
    }

    private void setBaseFragment() {
        if (findViewById(R.id.frame_email) != null) {
            EmailFragment firstFragment = new EmailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_email, firstFragment).commit();
        }
    }
}
