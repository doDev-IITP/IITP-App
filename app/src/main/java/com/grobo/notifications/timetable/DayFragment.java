package com.grobo.notifications.timetable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.grobo.notifications.R;

import java.util.List;

public class DayFragment extends Fragment {

    private TimetableAdapter ttAdapter;

    private String mDay;
    private SharedPreferences prefs;

    public DayFragment() {
    }

    public static DayFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("dayNumber", page);
        DayFragment fragment = new DayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        switch (requireArguments().getInt("dayNumber")) {

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
        return inflater.inflate(R.layout.fragment_timetable_day, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ListView ttListView = view.findViewById(R.id.tt_list_view);

        ttAdapter = new TimetableAdapter(getActivity(), R.layout.card_timetable);
        ttListView.setAdapter(ttAdapter);

        final String jsonString = prefs.getString("jsonString", "");
        final String mDayPreference = mDay;

        List<TimetableItem> timetableItems = TimetableUtility.extractTimetable(jsonString, mDayPreference);
        if (timetableItems != null && !timetableItems.isEmpty()) {
            ttAdapter.clear();
            ttAdapter.addAll(timetableItems);
            ttAdapter.notifyDataSetChanged();
        }

        ttListView.setOnItemClickListener((parent, v, position, id) -> {
            TimetableItem item = ttAdapter.getItem(position);
            if (item != null)
                showDialogueBox(item.getTime() + "   :   " + item.getSubject());
        });


        super.onViewCreated(view, savedInstanceState);
    }

    private void showDialogueBox(String message) {
        new AlertDialog.Builder(requireContext())
                .setMessage(message)
                .setTitle("Timetable")
                .setIcon(R.drawable.baseline_today_24)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }).setCancelable(true)
                .show();
    }
}
