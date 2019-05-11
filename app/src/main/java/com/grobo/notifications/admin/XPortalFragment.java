package com.grobo.notifications.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;

import java.util.ArrayList;
import java.util.List;

public class XPortalFragment extends Fragment {


    public XPortalFragment() {}

    ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xportal, container, false);

        ListView listView = view.findViewById(R.id.lv_fragment_portal);

        List<String> itemsList = new ArrayList<>();

        //TODO: get PORs from server and add to above list

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, itemsList);
        listView.setAdapter(adapter);

        return view;
    }

}
