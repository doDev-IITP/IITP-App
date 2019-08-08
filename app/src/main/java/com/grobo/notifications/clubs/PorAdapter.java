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

import java.util.ArrayList;

public class PorAdapter extends RecyclerView.Adapter<PorAdapter.MyHolder> {

    private Context context;
    final private OnCategorySelectedListener callback;
    private ArrayList<PorItem> porList;

    PorAdapter(ArrayList<PorItem> nameList, Context context,OnCategorySelectedListener listener) {
        this.porList = nameList;
        callback = listener;
        this.context = context;
    }

    public interface OnCategorySelectedListener {
        void onNameSelected(PorItem pos);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.card_por, parent, false );
        return new MyHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

        PorItem item=porList.get( position );
        holder.name.setText( item.getName() );
        holder.position.setText( item.getPosition() );
        Glide.with( context ).load( item.getImageurl() ).placeholder( R.drawable.profile_photo ).into( holder.profile );


    }

    void setItemList(ArrayList<PorItem> names) {
        porList = names;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return porList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView position;
        ImageView profile;


        MyHolder(View itemView) {
            super( itemView );
            name = itemView.findViewById( R.id.name );
            position=itemView.findViewById( R.id.position );
            profile=itemView.findViewById( R.id.imageurl );
        }
    }
}