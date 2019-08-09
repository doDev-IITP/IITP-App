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

import java.util.ArrayList;
import java.util.List;

public class ClubEventRecyclerAdapter extends RecyclerView.Adapter<ClubEventRecyclerAdapter.FeedViewHolder> {

    private Context context;
    private ArrayList<ClubEventItem> clubEventItemList;
    final private OnFeedSelectedListener callback;


    public ClubEventRecyclerAdapter(Context context, OnFeedSelectedListener listener) {
        this.context = context;
        callback = listener;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.card_event, parent, false );

        return new FeedViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedViewHolder holder, int position) {

        ClubEventItem item = clubEventItemList.get( position );
        holder.clubname.setText( item.getName() );
        holder.description.setText( "Shit it is" );
        Glide.with( context ).load( item.getImageUrl() ).into( holder.imageView );
//        if (clubEventItemList != null) {
//            final ClubEventItem current = clubEventItemList.get(position);
//
//            holder.title.setText(current.getEventName());
//            holder.venue.setText(current.getEventVenue());
//            Glide.with(context)
//                    .load(current.getEventImageUrl())
//                    .centerCrop()
//                    .placeholder(R.drawable.baseline_dashboard_24)
//                    .into(holder.imageView);
//            holder.imageView.setTransitionName("transition" + position);
//
//            if(current.isInterested()){
//                holder.availableIndicator.setBackgroundResource(R.color.red_dark2);
//            } else {
//                if (current.getEventDate() < (System.currentTimeMillis() - 3600000))
//                    holder.availableIndicator.setBackgroundResource(R.color.dark_gray);
//                else holder.availableIndicator.setBackgroundResource(R.color.light_green);
//            }
//
//            Date date = new Date(current.getEventDate());
//            SimpleDateFormat format = new SimpleDateFormat("dd MMM YYYY, hh:mm a");
//            holder.time.setText(format.format(date));
//
//            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    callback.onFeedSelected(current.getId(), holder.imageView, holder.getAdapterPosition());
//                }
//            });
//
//
//        } else {
//            holder.title.setText("Loading ...");
//        }
    }

    @Override
    public int getItemCount() {
        if (clubEventItemList != null)
            return clubEventItemList.size();
        else return 0;
    }

    class FeedViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView clubname;
        TextView description;

        FeedViewHolder(@NonNull View itemView) {
            super( itemView );
            imageView = itemView.findViewById( R.id.club_image );
            clubname = itemView.findViewById( R.id.club_name );
            description = itemView.findViewById( R.id.club_bio );
        }
    }

    public void setClubEventItemList(ArrayList<ClubEventItem> feeds) {
        clubEventItemList = feeds;
        notifyDataSetChanged();
    }

    public interface OnFeedSelectedListener {
        void onFeedSelected(String id, View view, int position);
    }
}
