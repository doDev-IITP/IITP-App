package com.grobo.notifications.utils;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.grobo.notifications.R;

public class ImageViewerActivity extends AppCompatActivity {

    private Uri myUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_menu);
        ImageView image = findViewById(R.id.messMenu);

        if (getIntent().hasExtra("transition_image"))
            ViewCompat.setTransitionName(image, getIntent().getStringExtra("transition_image"));

        if (getIntent().hasExtra("image_url"))
            Glide.with(this).load(getIntent().getStringExtra("image_url")).into(image);
        else if (getIntent().hasExtra("image_url_uri"))
            Glide.with(this).load(Uri.parse(getIntent().getStringExtra("image_url_uri"))).into(image);
        else Toast.makeText(this, "Error loading the image", Toast.LENGTH_SHORT).show();

    }
}
