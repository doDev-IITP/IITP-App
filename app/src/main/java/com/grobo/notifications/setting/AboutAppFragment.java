package com.grobo.notifications.setting;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.grobo.notifications.R;
import com.grobo.notifications.utils.utils;

public class AboutAppFragment extends Fragment {

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private TextView version;

    public AboutAppFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );
        getActivity().setTitle( "About App" );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate( R.layout.fragment_about_app, container, false );
        image1=view.findViewById( R.id.layout1 );
        image2=view.findViewById( R.id.layout2 );
        image3=view.findViewById( R.id.layout3 );
        Glide.with( getContext() ).load( "https://firebasestorage.googleapis.com/v0/b/timetable-grobo.appspot.com/o/developers%2FIMG_20190726_154411(1)-min(1).jpg?alt=media&token=62203ad1-48a2-4568-a68c-f203a5a9ef14" ).placeholder( R.drawable.profile_photo ).into( image1 );
        Glide.with( getContext() ).load( "https://firebasestorage.googleapis.com/v0/b/timetable-grobo.appspot.com/o/developers%2Fimg_aman.jpg?alt=media&token=b9fb030e-ec72-4c5d-9596-08f96e423c62" ).into( image2 );
        Glide.with( getContext() ).load("https://scontent-bom1-1.xx.fbcdn.net/v/t1.0-9/13465932_575022276008402_3643238272861381971_n.jpg?_nc_cat=107&_nc_oc=AQl6h5Kelo5Yhj3FvLsD_7DokoGGJfQfV2lS8KQ51YnH5YoMDfBB7T7T6XOO4JFVeZo&_nc_ht=scontent-bom1-1.xx&oh=53d8be896066fb79aa4dc062754507e0&oe=5DE44B87"  ).into( image3 );
        version=view.findViewById( R.id.version );
        version.setText(String.format("Version:  %s", utils.getAppVersion(getContext())));
        image1.setOnClickListener(view13 -> browserIntent( "https://www.facebook.com/anmol.chaddha.125" ));
        image2.setOnClickListener(view1 -> browserIntent( "https://www.facebook.com/amangrobo" ));
        image3.setOnClickListener(view12 -> browserIntent( "https://www.facebook.com/ashwani.yadav9499" ));


        return view;
    }
    private void browserIntent(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getContext(), Uri.parse(url));
    }

}
