package com.grobo.notifications.services.lostandfound;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;

public class NewLostAndFound extends Fragment {


    public NewLostAndFound() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_lost_found, container, false);

        //TODO: call edit text from corresponding fragment to get data

        return view;
    }

}
