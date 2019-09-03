package com.grobo.notifications.feed;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grobo.notifications.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.FeedViewHolder> {

    private Context context;
    private List<FeedItem> feedItemList;

    public FeedRecyclerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_feed, parent, false);

        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedViewHolder holder, int position) {

        if (feedItemList != null) {
            final FeedItem current = feedItemList.get(position);

            holder.title.setText(current.getEventName());
            holder.title.setTransitionName("transition_title" + position);
            holder.venue.setText(current.getEventVenue());
            holder.venue.setTransitionName("transition_venue" + position);
            Glide.with(context)
                    .load(current.getEventImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(holder.imageView);
            holder.imageView.setTransitionName("transition_image" + position);

            if (current.isInterested()) {
                holder.availableIndicator.setBackgroundResource(R.color.red_dark2);
            } else {
                if (current.getEventDate() < (System.currentTimeMillis() - 3600000)) {
                    holder.availableIndicator.setBackgroundResource( R.color.dark_gray );
                    holder.cardView.setAlpha( 0.7f );
                }
                else {
                    holder.availableIndicator.setBackgroundResource( R.color.light_green );
                }
            }

            Date date = new Date(current.getEventDate());
            SimpleDateFormat format = new SimpleDateFormat("dd MMM YYYY, hh:mm a", Locale.getDefault());
            holder.time.setText(format.format(date));
            holder.time.setTransitionName("transition_time" + position);

            holder.rootLayout.setOnClickListener(v -> {

                Bundle bundle = new Bundle();
                bundle.putInt("transition_position", position);
                bundle.putString("id", current.getId());

                FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                        .addSharedElement(holder.imageView, "transition_image" + position)
                        .addSharedElement(holder.title, "transition_title" + position)
                        .addSharedElement(holder.time, "transition_time" + position)
                        .addSharedElement(holder.venue, "transition_venue" + position)
                        .build();

                Navigation.findNavController(v).navigate(R.id.nav_feed_detail,
                        bundle,
                        null,
                        extras);
            });


        } else {
            holder.title.setText("Loading ...");
        }
    }

    @Override
    public int getItemCount() {
        if (feedItemList != null)
            return feedItemList.size();
        else return 0;
    }

    class FeedViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        View availableIndicator;
        TextView title;
        TextView time;
        TextView venue;
        LinearLayout rootLayout;
        LinearLayout sessionLayout;
        ImageView imageView;

        FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            availableIndicator = itemView.findViewById(R.id.item_available_indicator);
            time = itemView.findViewById(R.id.item_time_text);
            title = itemView.findViewById(R.id.item_title_text);
            venue = itemView.findViewById(R.id.item_room_text);
            rootLayout = itemView.findViewById(R.id.itemScheduleRootLayout);
            sessionLayout = itemView.findViewById(R.id.item_session_layout);
            imageView = itemView.findViewById(R.id.item_speaker_image);
            cardView=itemView.findViewById( R.id.card_feed );
        }
    }

    void setFeedItemList(List<FeedItem> feeds) {
        feedItemList = feeds;
        notifyDataSetChanged();
    }
}
