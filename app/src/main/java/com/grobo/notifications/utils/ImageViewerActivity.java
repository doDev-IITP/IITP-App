package com.grobo.notifications.utils;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.grobo.notifications.R;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_menu);
        final ImageView messMenu = findViewById(R.id.messMenu);

        if (getIntent().hasExtra("image_url")) {
            String url = getIntent().getStringExtra("image_url");
            Glide.with(this).load(url).into(messMenu);
        } else {
            Toast.makeText(this, "Error loading the image", Toast.LENGTH_SHORT).show();
        }
    }
}
