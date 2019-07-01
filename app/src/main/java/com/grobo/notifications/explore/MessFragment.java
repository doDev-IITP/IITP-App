package com.grobo.notifications.explore;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;
import com.grobo.notifications.utils.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

import static com.grobo.notifications.utils.Constants.IS_QR_DOWNLOADED;
import static com.grobo.notifications.utils.Constants.LOGIN_STATUS;
import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessFragment extends Fragment {


    public MessFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_mess, container, false);
    }


    public static class QRFragment extends Fragment {

        public QRFragment() {
        }

        private ImageView imageView;
        SharedPreferences prefs;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_qr, container, false);

            imageView = view.findViewById(R.id.qr_fragment_qr);

            Bitmap bitmap = null;
            if (prefs.getBoolean(LOGIN_STATUS, false)) {
                if (prefs.getBoolean(IS_QR_DOWNLOADED, false)) {
                    bitmap = getQRBitmap();
                } else {
                    downloadAndSave();
                }
            }

            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.color.shadow);
            }

            return view;
        }

        private Bitmap getQRBitmap() {
            File qrDir = new File(getContext().getFilesDir(), "qr");
            if (!qrDir.exists()) {
                qrDir.mkdirs();
            }

            String fileName = "qr.png";
            File file = new File(qrDir, fileName);
            try {
                FileInputStream stream = new FileInputStream(file);
                return BitmapFactory.decodeStream(stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                downloadAndSave();
            }

            return null;
        }

        private void downloadAndSave() {

            utils.ImageDownloader task = new utils.ImageDownloader();

            try {
                Bitmap bitmap = task.execute("https://api.qrserver.com/v1/create-qr-code/?data=" + PreferenceManager.getDefaultSharedPreferences(getContext()).getString(USER_MONGO_ID, null) + "&amp;size=100x100").get();
                if (bitmap != null) {
                    boolean ret = utils.saveImage(getContext(), bitmap, "qr", "qr.png");

                    if (ret) {
                        prefs.edit().putBoolean(IS_QR_DOWNLOADED, true).apply();
                    }
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }
}

