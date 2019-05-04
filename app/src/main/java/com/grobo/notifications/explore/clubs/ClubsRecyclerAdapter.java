package com.grobo.notifications.explore.clubs;

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

import java.util.List;

public class ClubsRecyclerAdapter extends RecyclerView.Adapter<ClubsRecyclerAdapter.ClubsViewHolder> {

    private Context context;
    private List<ClubItem> clubList;
    final private OnClubSelectedListener callback;


    public ClubsRecyclerAdapter(Context context, OnClubSelectedListener listener){
        this.context = context;
        callback = listener;
    }

    @NonNull
    @Override
    public ClubsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_club, parent, false);

        return new ClubsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ClubsViewHolder holder, int position) {

        if (clubList != null) {
            final ClubItem current = clubList.get(position);

            holder.name.setText(current.getName());
            holder.bio.setText(current.getBio());
            Glide.with(context)
                    .load(current.getImage())
                    .centerCrop()
                    .placeholder(R.drawable.ic_website_black_24dp)
                    .into(holder.image);
            holder.image.setTransitionName("transition" + position);

            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onClubSelected(current.getId(), holder.image, holder.getAdapterPosition());
                }
            });


        } else {
            holder.name.setText("Loading ...");
        }
    }

    @Override
    public int getItemCount() {
        if (clubList != null)
            return clubList.size();
        else return 0;
    }

    class ClubsViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView bio;
        LinearLayout rootLayout;
        ImageView image;

        ClubsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.club_name);
            bio = itemView.findViewById(R.id.club_bio);
            rootLayout = itemView.findViewById(R.id.card_club_root);
            image = itemView.findViewById(R.id.club_image);
        }
    }

    void setClubList(List<ClubItem> clubs){
        clubList = clubs;
        notifyDataSetChanged();
    }

    public interface OnClubSelectedListener {
        void onClubSelected(int id, View view, int position);
    }
}
