package com.grobo.notifications.feed;

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

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.FeedViewHolder> {

    private Context context;
    private List<FeedItem> feedItemList;
    final private OnFeedSelectedListener callback;


    public FeedRecyclerAdapter(Context context, OnFeedSelectedListener listener){
        this.context = context;
        callback = listener;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_schedule, parent, false);

        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedViewHolder holder, int position) {

        if (feedItemList != null) {
            final FeedItem current = feedItemList.get(position);

            holder.title.setText(current.getEventName());
            holder.speaker.setText(current.getEventDescription());
            Glide.with(context)
                    .load(current.getEventImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(holder.imageView);
            holder.imageView.setTransitionName("transition" + position);

            if(current.isInterested()){
                holder.availableIndicator.setBackgroundResource(R.color.red_dark2);
            } else {
                if (current.getEventDate() < (System.currentTimeMillis() - 3600000))
                    holder.availableIndicator.setBackgroundResource(R.color.dark_gray);
                else holder.availableIndicator.setBackgroundResource(R.color.light_green);
            }

            Date date = new Date(current.getEventDate());
            SimpleDateFormat format = new SimpleDateFormat("dd MMM YYYY, hh:mm a");
            holder.time.setText(format.format(date));

            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onFeedSelected(current.getId(), holder.imageView, holder.getAdapterPosition());
                }
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

        View availableIndicator;
        TextView title;
        TextView time;
        TextView venue;
        TextView speaker;
        LinearLayout rootLayout;
        LinearLayout sessionLayout;
        ImageView imageView;

        FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            availableIndicator = itemView.findViewById(R.id.item_available_indicator);
            time = itemView.findViewById(R.id.item_time_text);
            title = itemView.findViewById(R.id.item_title_text);
            venue = itemView.findViewById(R.id.item_room_text);
            speaker = itemView.findViewById(R.id.item_speaker_name_text);
            rootLayout = itemView.findViewById(R.id.itemScheduleRootLayout);
            sessionLayout = itemView.findViewById(R.id.item_session_layout);
            imageView = itemView.findViewById(R.id.item_speaker_image);
        }
    }

    void setFeedItemList(List<FeedItem> feeds){
        feedItemList = feeds;
        notifyDataSetChanged();
    }

    public interface OnFeedSelectedListener {
        void onFeedSelected(String id, View view, int position);
    }
}
