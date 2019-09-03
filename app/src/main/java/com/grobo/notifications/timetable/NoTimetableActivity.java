package com.grobo.notifications.timetable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;
import com.grobo.notifications.utils.ImageViewerActivity;
import com.grobo.notifications.utils.utils;

import org.commonmark.node.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import static com.grobo.notifications.utils.Constants.IS_QR_DOWNLOADED;
import static com.grobo.notifications.utils.Constants.IS_TT_DOWNLOADED;
import static com.grobo.notifications.utils.Constants.LOGIN_STATUS;

public class NoTimetableActivity extends AppCompatActivity {

    private ImageView imageView;
    SharedPreferences prefs;
    private Uri selectedImage;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        getSupportActionBar().setTitle( "TimeTable" );
        setContentView( R.layout.activity_no_timetable );
        imageView = findViewById( R.id.no_timetable );
        prefs = PreferenceManager.getDefaultSharedPreferences( this );
        FloatingActionButton button = findViewById( R.id.changefab );
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType( "image/*" );
                intent.setAction( Intent.ACTION_GET_CONTENT );
                startActivityForResult( Intent.createChooser( intent, "Select Picture" ), 1 );
            }
        } );
        Bitmap bitmap = null;

        if (prefs.getBoolean( IS_TT_DOWNLOADED, false )) {
            prefs.edit().putInt( "c", 1 ).apply();
            bitmap = getQRBitmap();
            downloadAndSave();
        } else {
            downloadAndSave();
        }

        if (bitmap != null) {
            imageView.setImageBitmap( bitmap );
        } else {

            Glide.with( this ).load( "http://www.sohrabdaver.com/images/upload-qr.jpg" ).centerCrop().into( imageView );
        }
    }

    private Bitmap getQRBitmap() {
        File qrDir = new File( this.getFilesDir(), "timetable" );
        if (!qrDir.exists()) {
            qrDir.mkdirs();
        }

        String fileName = "timetable.png";
        File file = new File( qrDir, fileName );
        try {
            FileInputStream stream = new FileInputStream( file );
            return BitmapFactory.decodeStream( stream );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            downloadAndSave();
        }

        return null;
    }

    private void downloadAndSave() {
            imageView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent( getApplicationContext(), ImageViewerActivity.class );
                    i.putExtra( "image_url_uri", prefs.getString( "path", "http://www.sohrabdaver.com/images/upload-qr.jpg" ) );
                    startActivity( i );
                }
            } );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                selectedImage = null;
                if (data != null) {
                    selectedImage = data.getData();
                    prefs.edit().putString( "path", selectedImage.toString() ).apply();
                }
                prefs.edit().putInt( "c", 1 ).apply();
                Glide.with( this ).load( selectedImage ).centerCrop().into( imageView );
                final InputStream imageStream;
                try {
                    if (selectedImage != null) {
                        imageStream = Objects.requireNonNull( this ).getContentResolver().openInputStream( selectedImage );
                        final Bitmap bmp = BitmapFactory.decodeStream( imageStream );
                        boolean ret = utils.saveImage( this, bmp, "timetable", "timetable.png" );
                        if (ret) {
                            prefs.edit().putBoolean( IS_TT_DOWNLOADED, true ).apply();
                        }
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
        super.onActivityResult( requestCode, resultCode, data );
    }
}
