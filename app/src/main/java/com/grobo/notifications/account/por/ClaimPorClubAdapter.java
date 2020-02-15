package com.grobo.notifications.account.por;

import android.content.Context;
import android.graphics.Color;
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
import com.grobo.notifications.clubs.ClubItem;

import java.util.List;

public class ClaimPorClubAdapter extends RecyclerView.Adapter<ClaimPorClubAdapter.ClubsViewHolder> {

    private Context context;
    private List<ClubItem> clubList;
    private OnClaimClubSelListener callback;


    public ClaimPorClubAdapter(Context context, OnClaimClubSelListener listener) {
        this.context = context;
        this.callback = listener;
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
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(holder.image);

            holder.rootLayout.setOnClickListener(v -> {
                callback.onClubSelected(current);
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
            name.setTextColor(Color.BLACK);
            bio = itemView.findViewById(R.id.club_bio);
            bio.setTextColor(Color.DKGRAY);
            rootLayout = itemView.findViewById(R.id.card_club_root);
            image = itemView.findViewById(R.id.club_image);
        }
    }

    void setClubList(List<ClubItem> clubs) {
        clubList = clubs;
        notifyDataSetChanged();
    }

    public interface OnClaimClubSelListener {
        void onClubSelected(ClubItem clubItem);
    }
}
