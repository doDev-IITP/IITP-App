package com.grobo.notifications.admin.clubevents;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodayEventsRecyclerAdapter extends RecyclerView.Adapter<TodayEventsRecyclerAdapter.EventViewHolder> {

    private Context context;
    private List<ClubEventItem> clubEventItemList;


    public TodayEventsRecyclerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_today_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder holder, int position) {

        if (clubEventItemList != null) {

            ClubEventItem item = clubEventItemList.get(position);
            holder.title.setText(item.getName());

            holder.club.setText(item.getRelatedClub().getName());

            Date date = new Date(item.getDate());
            SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            holder.time.setText(format.format(date));

            if (item.getDate() < (System.currentTimeMillis() - 3600000))
                holder.availableIndicator.setBackgroundResource(R.color.dark_gray);
            else holder.availableIndicator.setBackgroundResource(R.color.light_green);

            holder.root.setOnClickListener(v -> {
                Intent intent = new Intent(context, ClubEventDetailActivity.class);
                intent.putExtra("eventId", item.getId());
                ActivityCompat.startActivity(context, intent, null);
            });

        } else holder.title.setText("Loading ...");
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
        TextView club;
        LinearLayout root;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            availableIndicator = itemView.findViewById(R.id.indicator);
            time = itemView.findViewById(R.id.time);
            title = itemView.findViewById(R.id.title);
            club = itemView.findViewById(R.id.club);
            root = itemView.findViewById(R.id.root);
        }

    }

    public void setClubEventItemList(List<ClubEventItem> clubEvents) {
        clubEventItemList = clubEvents;
        notifyDataSetChanged();
    }

}
