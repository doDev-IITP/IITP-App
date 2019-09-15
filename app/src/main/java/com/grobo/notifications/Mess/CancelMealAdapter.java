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
import com.grobo.notifications.feed.FeedItem;
import com.grobo.notifications.internship.InternshipAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CancelMealAdapter extends RecyclerView.Adapter<CancelMealAdapter.ViewHolder> {

    private Context context;
    private List<MessModel> itemList;
    final private CancelMealAdapter.OnCancelSelectedListener callback;

    public CancelMealAdapter(Context context, OnCancelSelectedListener listener) {
        this.context = context;
        callback = listener;
    }

    @NonNull
    @Override
    public CancelMealAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.card_cancel_meal, parent, false );

        return new CancelMealAdapter.ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull final CancelMealAdapter.ViewHolder holder, int position) {
        MessModel messModel = itemList.get( position );
        if (messModel != null && messModel.getDays() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat( "EEE, MMM d, ''yy" );
            String d = dateFormat.format( messModel.getDays().get( 0 ).toDate().getTime() );
            holder.date_cancelled.setText( d );


        }
        if (messModel.isFull())
            holder.type_of_meal.setText( "(Full meal cancelled)" );
        else {
            String meals[] = {"Breakfast", "Lunch", "Snacks", "Dinner"};
            String s = "(";
            for (int i = 0; i < messModel.getMeals().size(); i++) {
                s = s + meals[messModel.getMeals().get( i ) - 1] + "+";
            }
            s = s.substring( 0, s.length() - 1 );
            s = s + ")";
            holder.type_of_meal.setText( s );
        }

        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.clear();
        calendar1.set( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ), calendar.get( Calendar.DATE ) );
        calendar1.add( Calendar.DATE, 3 );
        ;
        if (messModel.getDays().get( 0 ).toDate().compareTo( calendar1.getTime() ) >= 0)
            holder.cancel.setVisibility( View.GONE );
        else
            holder.cancel.setVisibility( View.GONE );

        holder.cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onCancelSelected( messModel.getDocumentId() );
            }
        } );


    }

    public interface OnCancelSelectedListener {
        void onCancelSelected(String id);
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

    public void ItemList(List<MessModel> feeds) {
        itemList = feeds;
        notifyDataSetChanged();
    }
}
