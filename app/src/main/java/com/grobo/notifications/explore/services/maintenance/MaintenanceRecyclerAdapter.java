package com.grobo.notifications.explore.services.maintenance;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MaintenanceRecyclerAdapter extends RecyclerView.Adapter<MaintenanceRecyclerAdapter.MaintenanceViewHolder> {


    @NonNull
    @Override
    public MaintenanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MaintenanceViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MaintenanceViewHolder extends RecyclerView.ViewHolder {



        MaintenanceViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}

//TODO: implement this
