package com.grobo.notifications.services.maintenance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grobo.notifications.R;

import java.util.List;

public class MaintenanceRecyclerAdapter extends RecyclerView.Adapter<MaintenanceRecyclerAdapter.MaintenanceViewHolder> {

    private Context context;
    private List<MaintenanceItem> itemList;
    final private OnMaintenanceSelectedListener callback;

    public MaintenanceRecyclerAdapter(Context context, OnMaintenanceSelectedListener listener) {
        this.context = context;
        callback = listener;
    }

    @NonNull
    @Override
    public MaintenanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_maintenance, parent, false);

        return new MaintenanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaintenanceViewHolder holder, int position) {

        if (itemList != null) {

            final MaintenanceItem current = itemList.get(position);

            holder.category.setText(String.valueOf(current.getCategory()));
            holder.status.setText(current.getStatus());
            holder.problem.setText(current.getProblem());
            holder.poster.setText(current.getMaintenancePoster().getInstituteId());

            Glide.with(context)
                    .load(current.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(holder.image);



            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onMaintenanceSelected(current.getId());
                }
            });

        } else {
            holder.category.setText("Loading ...");
        }
    }

    @Override
    public int getItemCount() {
        if (itemList != null)
            return itemList.size();
        else return 0;
    }

    class MaintenanceViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView category;
        TextView status;
        TextView problem;
        View root;
        TextView location;
        TextView poster;

        MaintenanceViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.maintenance_image);
            category = itemView.findViewById(R.id.maintenance_category);
            status = itemView.findViewById(R.id.maintenance_status);
            problem = itemView.findViewById(R.id.maintenance_problem);
            root = itemView.findViewById(R.id.card_maintenance_root);
            poster = itemView.findViewById(R.id.maintenance_poster);
            location = itemView.findViewById(R.id.maintenance_location);

        }
    }

    void setItemList(List<MaintenanceItem> feeds) {
        itemList = feeds;
        notifyDataSetChanged();
    }

    public interface OnMaintenanceSelectedListener {
        void onMaintenanceSelected(String id);
    }
}