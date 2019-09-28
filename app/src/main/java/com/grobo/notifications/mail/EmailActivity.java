package com.grobo.notifications.mail;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;

public class EmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Webmail");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setBaseFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh_web) {

            Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_email);

            if (f instanceof EmailFragment) {
                ((EmailFragment) f).updateHandle();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setBaseFragment() {
        if (findViewById(R.id.frame_email) != null) {
            EmailFragment firstFragment = new EmailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_email, firstFragment).commit();
        }
    }
}
