package com.grobo.notifications.account.por;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.grobo.notifications.R;

import java.util.List;

public class PORRecyclerAdapter extends RecyclerView.Adapter<PORRecyclerAdapter.ViewHolder> {

    private List<PORItem> porItems;
    private OnPORSelectedListener callback;

    public PORRecyclerAdapter(OnPORSelectedListener listener) {
        callback = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_por, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        if (porItems != null) {
            final PORItem current = porItems.get(position);

            holder.club.setText(String.format("%s  -  %s", current.getClubName(), current.getPosition()));

            holder.root.setOnClickListener(v -> {
                callback.onPORSelected(current);
            });

        } else {
            holder.club.setText("Loading ...");
        }
    }

    @Override
    public int getItemCount() {
        if (porItems != null)
            return porItems.size();
        else return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView club;
        MaterialCardView root;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            club = itemView.findViewById(R.id.card_por_club);
            root = itemView.findViewById(R.id.card_por_root);
        }
    }

    public void setItemList(List<PORItem> feeds) {
        porItems = feeds;
        notifyDataSetChanged();
    }

    public interface OnPORSelectedListener {
        void onPORSelected(PORItem porItem);
    }
}
