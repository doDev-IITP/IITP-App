package com.grobo.notifications.timetable;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.grobo.notifications.R;

public class TimetableAdapter extends ArrayAdapter<TimetableItem> {

    private Context context;

    public TimetableAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = ((Activity)context).getLayoutInflater().inflate(R.layout.card_timetable, parent, false);
        }

        TextView timeTextView = view.findViewById(R.id.tt_time_view);

        TimetableItem current = getItem(position);

        if (current != null) {
            String data = current.getTime() + "    " + current.getSubject();
            timeTextView.setText(data);
        }

        return view;
    }
}
