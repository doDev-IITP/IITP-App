package com.grobo.notifications.feed.addfeed;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.grobo.notifications.R;
import com.grobo.notifications.utils.DatePickerHelper;

public class AddFeedFragment extends Fragment {

    private OnFeedPreviewListener callback;

    public AddFeedFragment() {
        // Required empty public constructor
    }

    private EditText title;
    private EditText description;
    private EditText venue;
    private EditText image;
    private EditText coordinators;
    private EditText fb;
    private EditText inst;
    private EditText twitter;
    private Button feedPreview;
    private EditText date;

    SharedPreferences prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_feed, container, false);

        title = view.findViewById(R.id.add_feed_title);
        description = view.findViewById(R.id.add_feed_description);
        venue = view.findViewById(R.id.add_feed_venue);
        image = view.findViewById(R.id.add_feed_image);
        coordinators = view.findViewById(R.id.add_feed_coordinators);
        fb = view.findViewById(R.id.add_feed_fb);
        inst = view.findViewById(R.id.add_feed_inst);
        twitter = view.findViewById(R.id.add_feed_twitter);
        feedPreview = view.findViewById(R.id.post_button);
        date = view.findViewById(R.id.add_feed_date);

        final DatePickerHelper dateHelper = new DatePickerHelper(getContext(), date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateHelper.getDatePickerDialog().show();
            }
        });

        feedPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long eventDate = dateHelper.getTimeInMillisFromCalender();
                if (validateFeed()) {
                    if (callback != null) {
                        callback.onFeedPreview(title.getText().toString(), description.getText().toString(),
                                venue.getText().toString(), eventDate, image.getText().toString(),
                                fb.getText().toString(), inst.getText().toString(), twitter.getText().toString(),
                                coordinators.getText().toString());
                    }
                } else {
                    Toast.makeText(getContext(), "Please check the errors", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFeedPreviewListener) {
            callback = (OnFeedPreviewListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public interface OnFeedPreviewListener {
        void onFeedPreview(String title, String description, String venue, long eventDate, String image, String fb, String inst, String twitter, String coordinators);

    }

    private boolean validateFeed() {
        boolean valid = true;

        String t = title.getText().toString();
        String d = description.getText().toString();
        String v = venue.getText().toString();

        if (t.isEmpty()) {
            title.setError("Please enter a valid title");
            valid = false;
        } else {
            title.setError(null);
        }

        if (d.isEmpty()) {
            description.setError("Please enter a description");
            valid = false;
        } else {
            description.setError(null);
        }

        if (v.isEmpty()) {
            venue.setError("Please enter a valid venue");
            valid = false;
        } else {
            venue.setError(null);
        }
        return valid;
    }

}
