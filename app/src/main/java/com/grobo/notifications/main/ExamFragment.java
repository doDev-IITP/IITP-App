package com.grobo.notifications.main;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.grobo.notifications.R;


import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExamFragment extends Fragment {

    private RichLinkView examSchedule, seatingPlan, resources;

    public ExamFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_exam, container, false );
        examSchedule = view.findViewById( R.id.examschedule );
        seatingPlan = view.findViewById( R.id.seating_plan );
        //resources = view.findViewById( R.id.resources );
        final String seating = "https://www.codechef.com/";

        //TODO:Add proper links and try to customise as per need
        examSchedule.setLink( seating, new ViewListener() {
            @Override
            public void onSuccess(boolean status) {

            }

            @Override
            public void onError(Exception e) {

            }
        } );
        seatingPlan.setLink( seating, new ViewListener() {
            @Override
            public void onSuccess(boolean status) {

            }

            @Override
            public void onError(Exception e) {

            }
        } );


        return view;
    }

    private void browserIntent(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor( getResources().getColor( R.color.colorPrimary ) );

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl( getContext(), Uri.parse( url ) );
    }
}
