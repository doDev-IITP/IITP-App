package com.grobo.notifications.Mess;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CancelMealAdapter extends RecyclerView.Adapter<CancelMealAdapter.ViewHolder> {

    private List<Long> itemList;

    public CancelMealAdapter(Context context) {
    }

    @NonNull
    @Override
    public CancelMealAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.card_cancel_meal, parent, false );

        return new CancelMealAdapter.ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull final CancelMealAdapter.ViewHolder holder, int position) {
        long mess = itemList.get( position );

        SimpleDateFormat dateFormat = new SimpleDateFormat( "EEE, MMM d, ''yy", Locale.getDefault());
        String s = dateFormat.format( mess );
        holder.date_cancelled.setText( s );


    }

    @Override
    public int getItemCount() {
        if (itemList != null)
            return itemList.size();
        else return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView date_cancelled;
        TextView type_of_meal;
        ImageView cancel;


        ViewHolder(@NonNull View itemView) {
            super( itemView );
            date_cancelled = itemView.findViewById( R.id.date_cancelled );
            type_of_meal = itemView.findViewById( R.id.type );
            cancel = itemView.findViewById( R.id.cancel );
        }
    }

    public void ItemList(List<Long> feeds) {
        itemList = feeds;
        notifyDataSetChanged();
    }
}
