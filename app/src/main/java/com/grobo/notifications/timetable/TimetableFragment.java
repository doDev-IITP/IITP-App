package com.grobo.notifications.timetable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.grobo.notifications.utils.Constants.IS_TT_DOWNLOADED;

public class TimetableFragment extends Fragment {

    private SharedPreferences prefs;
    private ImageView noTimetableImage;

    public TimetableFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timetable, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        RelativeLayout timetableAvailable = view.findViewById(R.id.tt_rl_yes_timetable);
        RelativeLayout timetableNotAvailable = view.findViewById(R.id.tt_rl_no_timetable);

        if (prefs.getString("jsonString", "").equals("")) {

            timetableAvailable.setVisibility(View.GONE);
            timetableNotAvailable.setVisibility(View.VISIBLE);

            noTimetableImage = view.findViewById(R.id.no_timetable_image);
            FloatingActionButton button = view.findViewById(R.id.change_timetable_fab);
            button.setOnClickListener(view1 -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            });

            Bitmap bitmap = null;

            if (prefs.getBoolean(IS_TT_DOWNLOADED, false)) {
                bitmap = getTimetableBitmap();
            }

            if (bitmap != null) {
                noTimetableImage.setImageBitmap(bitmap);
            } else {
                Glide.with(this).load("http://www.sohrabdaver.com/images/upload-qr.jpg").centerCrop().into(noTimetableImage);
            }

        } else {

            timetableAvailable.setVisibility(View.VISIBLE);
            timetableNotAvailable.setVisibility(View.GONE);

            ListView ttListView = view.findViewById(R.id.tt_list_view);

            TimetableAdapter ttAdapter = new TimetableAdapter(getActivity(), R.layout.card_timetable);
            ttListView.setAdapter(ttAdapter);

            final String jsonString = prefs.getString("jsonString", "");
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            final String mDayPreference = dateFormat.format(new Date()).toLowerCase();

            List<TimetableItem> timetableItems = TimetableUtility.extractTimetable(jsonString, mDayPreference);
            if (timetableItems != null && !timetableItems.isEmpty()) {
                ttAdapter.clear();
                ttAdapter.addAll(timetableItems);
                ttAdapter.notifyDataSetChanged();
            }
        }

        super.onViewCreated(view, savedInstanceState);
    }

    private Bitmap getTimetableBitmap() {
        File qrDir = new File(requireContext().getFilesDir(), "timetable");
        if (!qrDir.exists()) {
            qrDir.mkdirs();
        }

        String fileName = "timetable.png";
        File file = new File(qrDir, fileName);
        try {
            FileInputStream stream = new FileInputStream(file);
            return BitmapFactory.decodeStream(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                if (data != null) {
                    Uri selectedImage = data.getData();

                    if (noTimetableImage != null && selectedImage != null)
                        Glide.with(this).load(selectedImage).centerCrop().into(noTimetableImage);

                    final InputStream imageStream;
                    try {
                        if (selectedImage != null) {
                            imageStream = requireContext().getContentResolver().openInputStream(selectedImage);
                            final Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                            boolean ret = utils.saveImage(requireContext(), bmp, "timetable", "timetable.png");
                            if (ret) {
                                prefs.edit().putBoolean(IS_TT_DOWNLOADED, true).apply();
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
