package com.grobo.notifications.explore.services.lostandfound;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LostAndFoundRecyclerAdapter extends RecyclerView.Adapter<LostAndFoundRecyclerAdapter.LostFoundViewHolder> {


    @NonNull
    @Override
    public LostFoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull LostFoundViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class LostFoundViewHolder extends RecyclerView.ViewHolder {



        LostFoundViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}

//TODO: implement this
