package com.grobo.notifications.services.agenda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;
import com.grobo.notifications.feed.DataPoster;

import java.util.List;

public class LikesRecyclerAdapter extends RecyclerView.Adapter<LikesRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<DataPoster> itemList;

    public LikesRecyclerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_agenda_likes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (itemList != null) {

            DataPoster current = itemList.get(position);

            holder.name.setText(current.getName());
            holder.instituteId.setText(current.getInstituteId());

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

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView instituteId;
        CardView root;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.card_root);
            name = itemView.findViewById(R.id.name);
            instituteId = itemView.findViewById(R.id.institute_id);
        }
    }

    void setItemList(List<DataPoster> dataPosters) {
        itemList = dataPosters;
        notifyDataSetChanged();
    }
}