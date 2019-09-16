package com.grobo.notifications.Mess;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.grobo.notifications.R;
import com.grobo.notifications.utils.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.grobo.notifications.utils.Constants.IS_QR_DOWNLOADED;
import static com.grobo.notifications.utils.Constants.LOGIN_STATUS;
import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;

public class QRFragment extends Fragment implements utils.ImageDownloaderListener {

    public QRFragment() {
    }

    private ImageView imageView;
    private SharedPreferences prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        imageView = view.findViewById(R.id.qr_fragment_qr);

        if (prefs.getBoolean(LOGIN_STATUS, false)) {
            Bitmap bitmap = null;
            if (prefs.getBoolean(IS_QR_DOWNLOADED, false)) bitmap = getQRBitmap();
            else downloadQR();

            if (bitmap != null) imageView.setImageBitmap(bitmap);
            else downloadQR();
        }

        super.onViewCreated(view, savedInstanceState);
    }

    private Bitmap getQRBitmap() {
        File qrDir = new File(requireContext().getFilesDir(), "qr");
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
            downloadQR();
        }
        return null;
    }

    private void downloadQR() {
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?data=" + prefs.getString(USER_MONGO_ID, null) + "&amp;size=1000x1000";
        utils.ImageDownloader imageDownloader = new utils.ImageDownloader(this);

        imageDownloader.execute(qrUrl);
    }

    @Override
    public void onImageDownloaded(Bitmap bitmap) {
        if (bitmap != null) {
            boolean ret = utils.saveImage(requireContext(), bitmap, "qr", "qr.png");
            if (ret) {
                prefs.edit().putBoolean(IS_QR_DOWNLOADED, true).apply();
            }
            imageView.setImageBitmap(bitmap);
        }
    }
}
