package com.grobo.notifications.admin.clubevents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grobo.notifications.R;
import com.grobo.notifications.account.por.PORItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClubEventRecyclerAdapter extends RecyclerView.Adapter<ClubEventRecyclerAdapter.EventViewHolder> {

    private Context context;
    private List<ClubEventItem> clubEventItemList;
    private PORItem porItem;

    public ClubEventRecyclerAdapter(Context context, PORItem porItem) {
        this.context = context;
        this.porItem = porItem;
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

            holder.image.setTransitionName("transition_image" + position);

            ClubEventItem item = clubEventItemList.get(position);
            holder.title.setText(item.getName());
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(holder.image);

            holder.club.setText(item.getRelatedClub().getName());

            Date date = new Date(item.getDate());
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            holder.time.setText(format.format(date));

            if (item.getDate() < (System.currentTimeMillis() - 3600000))
                holder.availableIndicator.setBackgroundResource(R.color.dark_gray);
            else holder.availableIndicator.setBackgroundResource(R.color.light_green);

            holder.root.setOnClickListener(v -> {
                Activity activity = (Activity) context;
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        new Pair<>(holder.image, "transition_image" + position)
                );

                Intent intent = new Intent(context, ClubEventDetailActivity.class);
                intent.putExtra("eventId", item.getId());
                intent.putExtra("transition_image", "transition_image" + position);
                if (porItem != null) intent.putExtra("por", porItem);
                ActivityCompat.startActivity(context, intent, options.toBundle());
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
        ImageView image;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            availableIndicator = itemView.findViewById(R.id.indicator);
            time = itemView.findViewById(R.id.time);
            title = itemView.findViewById(R.id.title);
            club = itemView.findViewById(R.id.club);
            root = itemView.findViewById(R.id.root);
            image = itemView.findViewById(R.id.image);
        }

    }

    public void setClubEventItemList(List<ClubEventItem> clubEvents) {
        clubEventItemList = clubEvents;
        notifyDataSetChanged();
    }

}
