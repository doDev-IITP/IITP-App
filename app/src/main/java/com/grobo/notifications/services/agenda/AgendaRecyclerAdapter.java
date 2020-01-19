package com.grobo.notifications.services.agenda;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grobo.notifications.R;
import com.grobo.notifications.utils.ImageViewerActivity;
import com.grobo.notifications.utils.ViewUtils;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.List;

public class AgendaRecyclerAdapter extends RecyclerView.Adapter<AgendaRecyclerAdapter.MaintenanceViewHolder> {

    private Context context;
    private List<Agenda> itemList;
    final private OnAgendaSelectedListener callback;

    public AgendaRecyclerAdapter(Context context, OnAgendaSelectedListener listener) {
        this.context = context;
        callback = listener;
    }

    @NonNull
    @Override
    public MaintenanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_agenda, parent, false);

        return new MaintenanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaintenanceViewHolder holder, int position) {

        if (itemList != null) {

            Agenda current = itemList.get(position);

            holder.category.setText(current.getCategory());
            holder.status.setText(current.getStatus());
            holder.problem.setText(current.getProblem());
            holder.reactCount.setText(String.valueOf(current.getLikesCount()));

            if (current.isLiked()) {
                holder.reactCard.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                holder.react.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            } else {
                holder.reactCard.setCardBackgroundColor(context.getResources().getColor(R.color.very_light_grey));
                holder.react.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
            }

            if (current.getImageUrl() != null && !current.getImageUrl().isEmpty()) {
                holder.image.setVisibility(View.VISIBLE);
                holder.image.getLayoutParams().height = ViewUtils.dpToPx(200f);
                holder.image.requestLayout();
                Glide.with(context)
                        .load(current.getImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.baseline_dashboard_24)
                        .into(holder.image);

                holder.image.setOnClickListener(v -> {
                    Intent i = new Intent(context, ImageViewerActivity.class);
                    i.putExtra("image_url", current.getImageUrl());
                    context.startActivity(i);
                });

            } else {
                holder.image.setVisibility(View.INVISIBLE);
                holder.image.getLayoutParams().height = ViewUtils.dpToPx(40f);
                holder.image.requestLayout();
            }

            holder.root.setOnClickListener(v -> callback.onAgendaSelected(current));

            holder.react.setOnClickListener(v -> {
                if (current.isLiked()) {
                    holder.reactCard.setCardBackgroundColor(context.getResources().getColor(R.color.very_light_grey));
                    holder.react.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));

                    current.setLiked(false);
                    current.setLikesCount(current.getLikesCount() - 1);

                    holder.reactCount.setText(String.valueOf(current.getLikesCount()));
                } else {
                    holder.reactCard.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                    holder.react.setImageTintList(ColorStateList.valueOf(Color.WHITE));

                    current.setLiked(true);
                    current.setLikesCount(current.getLikesCount() + 1);

                    holder.reactCount.setText(String.valueOf(current.getLikesCount()));
                }
                callback.onReactSelected(current.getId());
            });

            holder.share.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_share_square_o).colorRes(R.color.navy_blue_dark));
            holder.share.setOnClickListener(v -> {
                callback.onShareSelected(current.getId());
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
        ImageView react;
        TextView reactCount;
        CardView reactCard;
        ImageView share;

        MaintenanceViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            category = itemView.findViewById(R.id.category);
            status = itemView.findViewById(R.id.status);
            problem = itemView.findViewById(R.id.problem);
            root = itemView.findViewById(R.id.card_root);
            react = itemView.findViewById(R.id.react);
            reactCard = itemView.findViewById(R.id.react_card);
            reactCount = itemView.findViewById(R.id.react_count);
            share = itemView.findViewById(R.id.share);
        }
    }

    void setItemList(List<Agenda> feeds) {
        itemList = feeds;
        notifyDataSetChanged();
    }

    public interface OnAgendaSelectedListener {
        void onAgendaSelected(Agenda agenda);
        void onReactSelected(String agendaId);
        void onShareSelected(String agendaId);
    }
}