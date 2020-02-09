package com.grobo.notifications.admin.notify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;

import java.util.List;

public class AudienceListRecyclerAdapter extends RecyclerView.Adapter<AudienceListRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<String> itemList;
    private OnAudienceItemInteraction callback;

    public AudienceListRecyclerAdapter(Context context, OnAudienceItemInteraction callback) {
        this.context = context;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_each_audience, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (itemList != null) {
            String current = itemList.get(position);
            holder.text.setText(current);
            holder.cardRoot.setOnClickListener(v -> callback.onItemSelected(position));
            holder.cardRoot.setOnLongClickListener(v -> {
                Toast.makeText(context, current, Toast.LENGTH_LONG).show();
                return true;
            });
        } else {
            holder.text.setText("Loading ...");
        }
    }

    @Override
    public int getItemCount() {
        if (itemList != null)
            return itemList.size();
        else return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardRoot;
        TextView text;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardRoot = itemView.findViewById(R.id.card_root);
            text = itemView.findViewById(R.id.text_view);
        }
    }

    void setItemList(List<String> items) {
        itemList = items;
        notifyDataSetChanged();
    }

    void addItemToList(String item) {
        itemList.add(item);
        notifyItemChanged(itemList.size() - 1);
    }

    interface OnAudienceItemInteraction {
        void onItemSelected(int position);
    }

}