package com.grobo.notifications.timetable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.grobo.notifications.R;
import com.grobo.notifications.utils.Constants;
import com.grobo.notifications.utils.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.grobo.notifications.utils.Constants.IS_TT_DOWNLOADED;
import static com.grobo.notifications.utils.Constants.USER_BRANCH;
import static com.grobo.notifications.utils.Constants.USER_YEAR;

public class TimetableActivity extends AppCompatActivity {


    private SharedPreferences prefs;
    private ImageView noTimetableImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(0);

        prefs = PreferenceManager.getDefaultSharedPreferences(TimetableActivity.this);

        RelativeLayout timetableAvailable = findViewById(R.id.tt_rl_yes_timetable);
        RelativeLayout timetableNotAvailable = findViewById(R.id.tt_rl_no_timetable);

        if (prefs.getString("jsonString", "").equals("")) {

            timetableAvailable.setVisibility(View.GONE);
            timetableNotAvailable.setVisibility(View.VISIBLE);

            noTimetableImage = findViewById(R.id.no_timetable_image);
            FloatingActionButton button = findViewById(R.id.change_timetable_fab);
            button.setOnClickListener(view -> {
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

            TimetableFragmentAdapter timetableFragmentAdapter = new TimetableFragmentAdapter(getSupportFragmentManager(), 1);

            ViewPager mViewPager = findViewById(R.id.tt_container);
            mViewPager.setAdapter(timetableFragmentAdapter);

            TabLayout tabLayout = findViewById(R.id.tt_tab_layout);
            tabLayout.setupWithViewPager(mViewPager);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timetable, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.update_timetable) {
            String mUrl = FirebaseRemoteConfig.getInstance().getString(Constants.TIMETABLE_URL) + prefs.getString(USER_YEAR, "") + "/" + prefs.getString(USER_BRANCH, "").toLowerCase() + "/.json/";
            downloadTimetable(this).execute(mUrl);
            return true;
        }

        return false;
    }

    private static AsyncTask<String, Void, String> downloadTimetable(Context context) {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating...");
        progressDialog.show();

        return new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                return TimetableUtility.downloadTimetable(strings[0]);
            }

            @Override
            protected void onPostExecute(String s) {
                if (s != null)
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString("jsonString", s).apply();
                progressDialog.dismiss();
                ((Activity) context).recreate();
            }
        };
    }

    public class TimetableFragmentAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 5;
        private String[] tabTitles = new String[]{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};

        TimetableFragmentAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return DayFragment.newInstance(position + 2);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    private Bitmap getTimetableBitmap() {
        File qrDir = new File(getFilesDir(), "timetable");
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
                            imageStream = getContentResolver().openInputStream(selectedImage);
                            final Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                            boolean ret = utils.saveImage(this, bmp, "timetable", "timetable.png");
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
