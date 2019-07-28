package com.grobo.notifications.setting;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {


    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle( "Settings" );
        super.onViewCreated( view, savedInstanceState );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        rootView.findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        rootView.findViewById(R.id.notification_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getContext().getPackageName());
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, getContext().getResources().getString(R.string.default_notification_channel_id));
                    startActivity(intent);
                }

            }
        });

        return rootView;
    }



}
