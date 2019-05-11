package com.grobo.notifications.explore.services.lostandfound;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;

import java.util.ArrayList;
import java.util.List;

public class LostAndFoundFragment extends Fragment {

    public LostAndFoundFragment() {}

    private LostAndFoundRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lost_and_found, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.lost_found_fragment_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new LostAndFoundRecyclerAdapter(getContext(), (LostAndFoundRecyclerAdapter.OnItemSelectedListener) getActivity());
        recyclerView.setAdapter(adapter);

        populateRecycler();

        return view;
    }

    private void populateRecycler() {

        List<LostAndFoundItem> items = new ArrayList<>();

        adapter.setItemList(items);

    }


}

//TODO: get data from server about all lost and found activities and display in recycler view, take reference from clubs fragment and clubs recycler view
//TODO: implement fab to launch new Lost and found fragment using transact fragment