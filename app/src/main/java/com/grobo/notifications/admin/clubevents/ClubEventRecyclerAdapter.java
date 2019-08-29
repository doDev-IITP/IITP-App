package com.grobo.notifications.admin.clubevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grobo.notifications.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_feed, parent, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder holder, int position) {

        if (clubEventItemList != null) {

            ClubEventItem item = clubEventItemList.get(position);
            holder.title.setText(item.getName());
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(holder.imageView);

            holder.rootLayout.setOnClickListener(v -> callback.onEventSelected(item.getId()));
            holder.venue.setText(item.getVenue());

            Date date = new Date(item.getDate());
            SimpleDateFormat format = new SimpleDateFormat("dd MMM YYYY, hh:mm a", Locale.getDefault());
            holder.time.setText(format.format(date));

            if (item.getDate() < (System.currentTimeMillis() - 3600000))
                holder.availableIndicator.setBackgroundResource(R.color.dark_gray);
            else holder.availableIndicator.setBackgroundResource(R.color.light_green);

        } else {
            holder.title.setText("Loading ...");
        }
    }

    @Override
    public int getItemCount() {
        if (clubEventItemList != null)
            return clubEventItemList.size();
        else return 0;
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        View availableIndicator;
        TextView title;
        TextView time;
        TextView venue;
        LinearLayout rootLayout;
        LinearLayout sessionLayout;
        ImageView imageView;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            availableIndicator = itemView.findViewById(R.id.item_available_indicator);
            time = itemView.findViewById(R.id.item_time_text);
            title = itemView.findViewById(R.id.item_title_text);
            venue = itemView.findViewById(R.id.item_room_text);
            rootLayout = itemView.findViewById(R.id.itemScheduleRootLayout);
            sessionLayout = itemView.findViewById(R.id.item_session_layout);
            imageView = itemView.findViewById(R.id.item_speaker_image);
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
