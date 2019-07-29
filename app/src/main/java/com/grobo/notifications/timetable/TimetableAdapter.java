package com.grobo.notifications.timetable;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grobo.notifications.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class TimetableAdapter extends ArrayAdapter<TimetableItem> {

    String branchPre;

    public TimetableAdapter(Activity context, int resource, List<TimetableItem> objects) {
        super( context, resource, objects );
        branchPre = context.getSharedPreferences( "PREFERENCE", MODE_PRIVATE ).getString( "branchPre", "" );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate( R.layout.card_timetable, parent, false );
        }

        TextView timeTextView = (TextView) convertView.findViewById( R.id.tt_time_view );
        TextView subjectTextView = (TextView) convertView.findViewById( R.id.tt_subject_view );
        LinearLayout linearLayout = (LinearLayout) convertView.findViewById( R.id.timetable_item_ll );

        TimetableItem singleTimetable = getItem( position );

        timeTextView.setText( singleTimetable.gettime() );
        subjectTextView.setText( singleTimetable.getsubject() );

        return convertView;
    }
}
