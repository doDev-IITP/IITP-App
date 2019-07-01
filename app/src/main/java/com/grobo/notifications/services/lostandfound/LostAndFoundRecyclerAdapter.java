package com.grobo.notifications.services.lostandfound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;

import java.util.List;

import static com.grobo.notifications.services.lostandfound.LostAndFoundItem.ITEM_FOUND;
import static com.grobo.notifications.services.lostandfound.LostAndFoundItem.ITEM_LOST;
import static com.grobo.notifications.services.lostandfound.LostAndFoundItem.ITEM_RECOVERED;

public class LostAndFoundRecyclerAdapter extends RecyclerView.Adapter<LostAndFoundRecyclerAdapter.LostFoundViewHolder> {

    private Context context;
    private List<LostAndFoundItem> itemList;
    final private OnItemSelectedListener callback;

    public LostAndFoundRecyclerAdapter(Context context, OnItemSelectedListener listener) {
        this.context = context;
        callback = listener;
    }

    @NonNull
    @Override
    public LostFoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_lost_found, parent, false);

        return new LostFoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LostFoundViewHolder holder, int position) {
        if (itemList != null) {

            final LostAndFoundItem current = itemList.get(position);

            holder.name.setText(current.getName());

            switch (current.getLostStatus()) {

                case ITEM_LOST:
                    holder.status.setText("LOST");
                case ITEM_FOUND:
                    holder.status.setText("FOUND");
                case ITEM_RECOVERED:
                    holder.status.setText("RECOVERED");

            }

            holder.date.setText(current.getDate());
            holder.time.setText(current.getTime());

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onItemSelected(current.getId());
                }
            });

        } else {
            holder.name.setText("Loading ...");
        }

    }

    @Override
    public int getItemCount() {
        if (itemList != null)
            return itemList.size();
        else return 0;
    }

    class LostFoundViewHolder extends RecyclerView.ViewHolder {

        TextView status;
        TextView name;
        TextView date;
        TextView time;
        LinearLayout root;

        LostFoundViewHolder(@NonNull View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.lost_found_status);
            name = itemView.findViewById(R.id.lost_found_name);
            date = itemView.findViewById(R.id.lost_found_date);
            time = itemView.findViewById(R.id.lost_found_time);
            root = itemView.findViewById(R.id.card_lost_found_root);
        }
    }

    void setItemList(List<LostAndFoundItem> feeds) {
        itemList = feeds;
        notifyDataSetChanged();
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int id);
    }
}