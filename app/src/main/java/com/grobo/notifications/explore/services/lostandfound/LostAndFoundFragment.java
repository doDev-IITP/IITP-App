package com.grobo.notifications.explore.services.lostandfound;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;

public class LostAndFoundFragment extends Fragment {


    public LostAndFoundFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lost_and_found, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.lost_found_fragment_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        LostAndFoundRecyclerAdapter adapter = new LostAndFoundRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

}

//TODO: get data from server about all lost and found activities and display in recycler view, take reference from clubs fragment and clubs recycler view
//TODO: implement fab to launch new Lost and found fragment using transact fragment