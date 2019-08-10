package com.grobo.notifications.timetable;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayFragment extends Fragment {

    private TimetableAdapter ttAdapter;
    private ListView ttListView;

    private String mDay;
    private SharedPreferences prefs;
    private List<TimetableItem> timetableItems;

    public DayFragment() {
    }

    public static DayFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt( "dayNumber", page );
        DayFragment fragment = new DayFragment();
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        prefs = PreferenceManager.getDefaultSharedPreferences( getContext() );

        switch (getArguments().getInt( "dayNumber" )) {

            case 2:
                mDay = "monday";
                break;
            case 3:
                mDay = "tuesday";
                break;
            case 4:
                mDay = "wednesday";
                break;
            case 5:
                mDay = "thursday";
                break;
            case 6:
                mDay = "friday";
                break;
            default:

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate( R.layout.fragment_timetable, container, false );

        ttListView = rootView.findViewById( R.id.tt_list_view );

        timetableItems = new ArrayList<>();
        ttAdapter = new TimetableAdapter( getActivity(), R.layout.card_timetable, timetableItems );
        ttListView.setAdapter( ttAdapter );
        final String jsonString = prefs.getString( "jsonString", "" );
        final String mDayPreference = mDay;

//        new AsyncTask<String, Void, List<TimetableItem>>(){
//
//
//
//            @Override
//            protected List<TimetableItem> doInBackground(String... strings) {
//                List<TimetableItem> singleDayList = TimetableUtility.extractTimetable(jsonString, mDayPreference);
//                return singleDayList;
//            }
//
//            @Override
//            protected void onPostExecute(List<TimetableItem> timetableItems) {
//                Toast.makeText( getContext(), "a", Toast.LENGTH_SHORT ).show();
//                ttAdapter.clear();
//                if (timetableItems != null && !timetableItems.isEmpty()){
//                    ttAdapter.addAll(timetableItems);
//                    ttAdapter.notifyDataSetChanged();
//                }
//            }
//        };
        List<TimetableItem> timetableItems = TimetableUtility.extractTimetable( jsonString, mDayPreference );
        ttAdapter.clear();
        if (timetableItems != null && !timetableItems.isEmpty()) {
            ttAdapter.addAll( timetableItems );
            ttAdapter.notifyDataSetChanged();
        }

        ttListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogueBox( ttAdapter.getItem( position ).gettime() + "   :   " + ttAdapter.getItem( position ).getsubject() );
            }
        } );

        return rootView;
    }


    @Override
    public void onDestroyView() {
        timetableItems.clear();
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        timetableItems.clear();
        super.onStop();
    }

    private void showDialogueBox(String message) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder( Objects.requireNonNull( getContext() ) );
        builder.setMessage( message );
        builder.setTitle( "Timetable" );
        builder.setIcon( R.drawable.baseline_today_24 );
        builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        } );
        builder.setPositiveButtonIcon( getContext().getResources().getDrawable( R.drawable.baseline_done_24 ) );
        builder.setCancelable( true );

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

//    public class timetable extends AsyncTask<String,Void,List<TimetableItem>>{
//
//        @Override
//        protected List<TimetableItem> doInBackground(String... strings) {
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(List<TimetableItem> timetableItems) {
//            Toast.makeText( getContext(), "hi", Toast.LENGTH_SHORT ).show();
//            super.onPostExecute( timetableItems );
//        }
//    }
}
