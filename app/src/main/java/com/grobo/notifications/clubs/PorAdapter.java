package com.grobo.notifications.clubs;

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

import java.util.List;

public class PorAdapter extends RecyclerView.Adapter<PorAdapter.MyHolder> {

    private Context context;
    final private OnPORSelectedListener callback;
    private List<PorItem> porList;

    PorAdapter(Context context, OnPORSelectedListener listener) {
        callback = listener;
        this.context = context;
    }

    public interface OnPORSelectedListener {
        void onPORSelected(String userId);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_por, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

        if (porList != null) {
            PorItem item = porList.get(position);
            holder.name.setText(item.getName());
            holder.position.setText(item.getPosition());
            Glide.with(context)
                    .load(item.getImage())
                    .placeholder(R.drawable.profile_photo)
                    .into(holder.profile);

            holder.root.setOnClickListener(view -> callback.onPORSelected(item.getUserId()));
        }
    }

    void setItemList(List<PorItem> names) {
        porList = names;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (porList != null)
            return porList.size();
        else return 0;
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView position;
        ImageView profile;
        View root;

        MyHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            position = itemView.findViewById(R.id.position);
            profile = itemView.findViewById(R.id.image_url);
            root = itemView.findViewById(R.id.card_por_root);
        }
    }
}