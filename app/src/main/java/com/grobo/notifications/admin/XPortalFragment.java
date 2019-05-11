package com.grobo.notifications.admin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;

import java.util.ArrayList;
import java.util.List;

public class XPortalFragment extends Fragment {


    public XPortalFragment() {}

    ArrayAdapter<String> adapter;
    OnPORSelectedListener callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xportal, container, false);

        ListView listView = view.findViewById(R.id.lv_fragment_portal);

        List<String> itemsList = new ArrayList<>();

        //TODO: get PORs from server and add to above list

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, itemsList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPORSelectedListener) {
            callback = (OnPORSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public interface OnPORSelectedListener {
        void onPORSelected(String PORId);
    }

}
