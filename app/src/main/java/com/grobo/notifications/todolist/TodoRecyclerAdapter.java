package com.grobo.notifications.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TodoRecyclerAdapter extends RecyclerView.Adapter<TodoRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Goal> itemList;
    private OnTodoInteractionListener callback;

    public TodoRecyclerAdapter(Context context, OnTodoInteractionListener listener) {
        this.context = context;
        callback = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.card_todo, parent, false );
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (itemList != null) {

            Goal current = itemList.get( position );

            holder.checkbox.setChecked( current.getChecked() != 0 );
            holder.title.setText( current.getName() );

            if (current.getAlarm() != 0) {
                holder.alarm.setVisibility( View.VISIBLE );
                SimpleDateFormat format = new SimpleDateFormat( "dd MMM yyyy, hh:mm a", Locale.getDefault() );
                holder.alarm.setText( format.format( current.getAlarm() ) );
            } else {
                holder.alarm.setVisibility( View.GONE );
            }

            if (current.getChecked() == 1)
                holder.root.setAlpha(0.5f);
            else
                holder.root.setAlpha(1f);

            holder.root.setOnClickListener( v -> callback.onTodoSelected( current ) );

        } else {
            holder.title.setText( "Loading ..." );
        }
    }

    @Override
    public int getItemCount() {
        if (itemList != null)
            return itemList.size();
        else return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView alarm;
        CheckBox checkbox;
        View root;

        ViewHolder(@NonNull View itemView) {
            super( itemView );

            title = itemView.findViewById( R.id.title );
            alarm = itemView.findViewById( R.id.alarm );
            checkbox = itemView.findViewById( R.id.checkbox );
            root = itemView.findViewById( R.id.card_todo_root );
        }
    }

    void setItemList(List<Goal> feeds) {
        itemList = feeds;
        notifyDataSetChanged();
    }

    public interface OnTodoInteractionListener {
        void onTodoSelected(Goal Goal);
    }

    public Goal getGoalAtPosition(int position) {
        if (itemList != null && position >= 0 && position < itemList.size())
            return itemList.get( position );
        return null;
    }
}