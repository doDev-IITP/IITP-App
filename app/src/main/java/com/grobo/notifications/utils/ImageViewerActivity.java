package com.grobo.notifications.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.grobo.notifications.R;

import java.util.concurrent.Future;

import static com.grobo.notifications.utils.Constants.MESS_MENU_URL;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_menu);
        final ImageView messMenu = findViewById(R.id.messMenu);

        String url = "";

        if (getIntent().hasExtra("image_url")) {
            url = getIntent().getStringExtra("image_url");
        } else {
            FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
            url = remoteConfig.getString(MESS_MENU_URL);
        }

        Log.e("url", url);

        new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... params) {
                final Future<Bitmap> futureTarget = Glide.with(ImageViewerActivity.this).asBitmap().load(params[0]).encodeQuality(80).submit();
                try {
                    return futureTarget.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap == null) {
                    Toast.makeText(ImageViewerActivity.this, "Unable to load image", Toast.LENGTH_LONG).show();
                    return;
                }
                messMenu.setImageBitmap(bitmap);
            }
        }.execute(url);
    }
}
