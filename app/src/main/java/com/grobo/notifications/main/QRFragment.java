package com.grobo.notifications.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;
import com.grobo.notifications.utils.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.grobo.notifications.utils.Constants.IS_QR_DOWNLOADED;
import static com.grobo.notifications.utils.Constants.LOGIN_STATUS;
import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;

public class QRFragment extends Fragment {


    public QRFragment() {
    }

    private ImageView imageView;
    SharedPreferences prefs;
    private FloatingActionButton changeqr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        prefs = PreferenceManager.getDefaultSharedPreferences( getContext() );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_qr, container, false );

        imageView = view.findViewById( R.id.qr_fragment_qr );
        changeqr = view.findViewById( R.id.changefab );

        Bitmap bitmap = null;
        if (prefs.getBoolean( LOGIN_STATUS, false )) {
            if (prefs.getBoolean( IS_QR_DOWNLOADED, false )) {
                bitmap = getQRBitmap();
            } else {
                downloadAndSave();
            }
        }

        if (bitmap != null) {
            imageView.setImageBitmap( bitmap );
        } else {

            Glide.with( this ).load( "http://www.sohrabdaver.com/images/upload-qr.jpg" ).centerCrop().into( imageView );
        }
        changeqr.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType( "image/*" );
                intent.setAction( Intent.ACTION_GET_CONTENT );
                startActivityForResult( Intent.createChooser( intent, "Select Picture" ), 1 );
            }
        } );


        return view;
    }

    private Bitmap getQRBitmap() {
        File qrDir = new File( getContext().getFilesDir(), "qr" );
        if (!qrDir.exists()) {
            qrDir.mkdirs();
        }

        String fileName = "qr.png";
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

//        utils.ImageDownloader task = new utils.ImageDownloader();
//
//        try {
//            Bitmap bitmap = task.execute("https://api.qrserver.com/v1/create-qr-code/?data=" + PreferenceManager.getDefaultSharedPreferences(getContext()).getString(USER_MONGO_ID, null) + "&amp;size=100x100").get();
//            if (bitmap != null) {
//                boolean ret = utils.saveImage(getContext(), bitmap, "qr", "qr.png");
//
//                if (ret) {
//                    prefs.edit().putBoolean(IS_QR_DOWNLOADED, true).apply();
//                }
//            }
//
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        imageView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType( "image/*" );
                intent.setAction( Intent.ACTION_GET_CONTENT );
                startActivityForResult( Intent.createChooser( intent, "Select Picture" ), 1 );
                imageView.setClickable( false );
            }
        } );


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == getActivity().RESULT_OK) {

                Uri selectedImage = null;
                if (data != null) {
                    selectedImage = data.getData();
                }
                Glide.with( this ).load( selectedImage ).centerCrop().into( imageView );
                final InputStream imageStream;
                try {
                    if (selectedImage != null) {
                        imageStream = Objects.requireNonNull( getActivity() ).getContentResolver().openInputStream( selectedImage );
                        final Bitmap bmp = BitmapFactory.decodeStream( imageStream );
                        boolean ret = utils.saveImage( getContext(), bmp, "qr", "qr.png" );
                        if (ret) {
                            prefs.edit().putBoolean( IS_QR_DOWNLOADED, true ).apply();
                        }
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
        super.onActivityResult( requestCode, resultCode, data );
    }

    public void change(boolean check) {
        if (check == true) {
            changeqr.show();
            new CountDownTimer( 10000, 1000 ) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    changeqr.hide();
                }
            }.start();
        }
    }

}
