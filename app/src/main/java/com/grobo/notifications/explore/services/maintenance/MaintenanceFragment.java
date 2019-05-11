package com.grobo.notifications.explore.services.maintenance;

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

public class MaintenanceFragment extends Fragment {

    public MaintenanceFragment() {}

    private MaintenanceRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maintenance, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.maintenance_fragment_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MaintenanceRecyclerAdapter(getContext(), (MaintenanceRecyclerAdapter.OnItemSelectedListener) getActivity());
        recyclerView.setAdapter(adapter);

        populateRecycler();

        return view;
    }

    private void populateRecycler() {

        List<MaintenanceItem> items = new ArrayList<>();

        adapter.setItemList(items);

    }

}
