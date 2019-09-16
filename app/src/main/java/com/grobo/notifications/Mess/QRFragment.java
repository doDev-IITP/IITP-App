package com.grobo.notifications.Mess;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;
import com.grobo.notifications.utils.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;
import static com.grobo.notifications.utils.Constants.IS_QR_DOWNLOADED;
import static com.grobo.notifications.utils.Constants.LOGIN_STATUS;
import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;
import static com.grobo.notifications.utils.Constants.WEBMAIL;

public class QRFragment extends Fragment {


    public QRFragment() {
    }

    private ImageView imageView;
    private SharedPreferences prefs;
    //  private FloatingActionButton changeqr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        prefs = PreferenceManager.getDefaultSharedPreferences( requireContext() );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_qr, container, false );

        imageView = view.findViewById( R.id.qr_fragment_qr );
        //changeqr = view.findViewById( R.id.changefab );

        if (prefs.getBoolean( LOGIN_STATUS, false )) {

//            Bitmap bitmap = null;
//            if (prefs.getBoolean(IS_QR_DOWNLOADED, false)) {
//                bitmap = getQRBitmap();
//            } else {
//                showDummyImage();
//            }
//
//            if (bitmap != null) {
//                imageView.setImageBitmap(bitmap);
//            } else {
//                showDummyImage();
//            }
//
//            changeqr.setOnClickListener(view1 -> {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
//            });
            Glide.with( this ).load( "https://api.qrserver.com/v1/create-qr-code/?data=" + PreferenceManager.getDefaultSharedPreferences( requireContext() ).getString( USER_MONGO_ID, null ) + "&amp;size=100x100" ).into( imageView );
        }
        return view;
    }

    private Bitmap getQRBitmap() {
        File qrDir = new File( requireContext().getFilesDir(), "qr" );
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
            showDummyImage();
        }

        return null;
    }

    private void showDummyImage() {
        Glide.with( this ).load( "http://www.sohrabdaver.com/images/upload-qr.jpg" ).centerCrop().into( imageView );
        imageView.setOnClickListener( v -> {
            Intent intent = new Intent();
            intent.setType( "image/*" );
            intent.setAction( Intent.ACTION_GET_CONTENT );
            startActivityForResult( Intent.createChooser( intent, "Select Picture" ), 1 );
        } );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                Uri selectedImage = null;
                if (data != null) {
                    selectedImage = data.getData();
                }
                Glide.with( this ).load( selectedImage ).centerCrop().into( imageView );
                imageView.setOnClickListener( null );
                final InputStream imageStream;
                try {
                    if (selectedImage != null) {
                        imageStream = requireActivity().getContentResolver().openInputStream( selectedImage );
                        final Bitmap bmp = BitmapFactory.decodeStream( imageStream );
                        boolean ret = utils.saveImage( requireContext(), bmp, "qr", "qr.png" );
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

}
