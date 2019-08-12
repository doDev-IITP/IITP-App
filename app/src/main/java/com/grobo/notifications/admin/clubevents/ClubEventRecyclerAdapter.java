package com.grobo.notifications.admin.clubevents;

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

public class ClubEventRecyclerAdapter extends RecyclerView.Adapter<ClubEventRecyclerAdapter.EventViewHolder> {

    private Context context;
    private List<ClubEventItem> clubEventItemList;
    final private OnEventSelectedListener callback;


    public ClubEventRecyclerAdapter(Context context, OnEventSelectedListener listener) {
        this.context = context;
        callback = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_event, parent, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder holder, int position) {

        if (clubEventItemList != null) {

            ClubEventItem item = clubEventItemList.get(position);
            holder.name.setText(item.getName());
            holder.description.setText(item.getDescription());
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(holder.image);

            holder.root.setOnClickListener(v -> callback.onEventSelected(item.getId()));
        } else {
            holder.name.setText("Loading ...");
        }
    }

    @Override
    public int getItemCount() {
        if (clubEventItemList != null)
            return clubEventItemList.size();
        else return 0;
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;
        TextView description;
        View root;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.event_image);
            name = itemView.findViewById(R.id.event_name);
            description = itemView.findViewById(R.id.event_description);
            root = itemView.findViewById(R.id.card_event_root);
        }
    }

    public void setClubEventItemList(List<ClubEventItem> clubEvents) {
        clubEventItemList = clubEvents;
        notifyDataSetChanged();
    }

    public interface OnEventSelectedListener {
        void onEventSelected(String eventId);
    }
}
