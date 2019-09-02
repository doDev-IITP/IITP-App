package com.grobo.notifications.services.lostandfound;

import android.content.Context;
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

import java.util.List;

import static com.grobo.notifications.utils.Constants.ITEM_FOUND;
import static com.grobo.notifications.utils.Constants.ITEM_LOST;
import static com.grobo.notifications.utils.Constants.ITEM_RECOVERED;

public class LostAndFoundRecyclerAdapter extends RecyclerView.Adapter<LostAndFoundRecyclerAdapter.LostFoundViewHolder> {

    private Context context;
    private List<LostAndFoundItem> itemList;
    final private OnLostFoundSelectedListener callback;

    public LostAndFoundRecyclerAdapter(Context context, OnLostFoundSelectedListener listener) {
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
                    break;
                case ITEM_FOUND:
                    holder.status.setText("FOUND");
                    break;
                case ITEM_RECOVERED:
                    holder.status.setText("RECOVERED");
                    break;

            }

            holder.date.setText(current.getDate());
            holder.time.setText(current.getTime());
            holder.description.setText(current.getDescription());
            holder.contact.setText(current.getContact());
            holder.place.setText(current.getPlace());

            Glide.with(context)
                    .load(current.getImage())
                    .centerCrop()
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(holder.image);

            holder.image.setOnClickListener(v -> {
                if (current.getImage() != null && !current.getImage().equals(""))
                    callback.onLostFoundSelected(current.getImage(), v);
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
        CardView root;
        TextView contact;
        ImageView image;
        TextView place;
        TextView description;

        LostFoundViewHolder(@NonNull View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.lost_found_status);
            name = itemView.findViewById(R.id.lost_found_name);
            date = itemView.findViewById(R.id.lost_found_date);
            time = itemView.findViewById(R.id.lost_found_time);
            root = itemView.findViewById(R.id.card_lost_found_root);
            contact = itemView.findViewById(R.id.lost_found_contact);
            image = itemView.findViewById(R.id.lost_found_image);
            place = itemView.findViewById(R.id.lost_found_place);
            description = itemView.findViewById(R.id.lost_found_description);
        }
    }

    void setItemList(List<LostAndFoundItem> feeds) {
        itemList = feeds;
        notifyDataSetChanged();
    }

    public interface OnLostFoundSelectedListener {
        void onLostFoundSelected(String id, View view);
    }
}