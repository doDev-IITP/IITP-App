package com.grobo.notifications.feed;


import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.noties.markwon.Markwon;
import ru.noties.markwon.image.ImagesPlugin;

public class FeedDetailFragment extends Fragment {

    public FeedDetailFragment() {}

    public static FeedDetailFragment newInstance() {

        return new FeedDetailFragment();
    }

    private FeedViewModel feedViewModel;
    private FloatingActionButton interestedFab;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_detail, container, false);

        ImageView eventPoster = view.findViewById(R.id.event_detail_header_bg);
        TextView eventTitle = view.findViewById(R.id.tv_event_detail_title);

        interestedFab = view.findViewById(R.id.fab_event_detail_interested);
        TextView eventVenue = view.findViewById(R.id.tv_event_detail_venue);
        TextView eventTime = view.findViewById(R.id.tv_event_detail_time);
        TextView guestTitle = view.findViewById(R.id.tv_event_detail_guest_title);
        TextView guestName = view.findViewById(R.id.tv_event_detail_guest_name);
        TextView eventDescription = view.findViewById(R.id.tv_event_detail_description);

        Bundle b = getArguments();
        if (b != null) {
            String transitionName = b.getString("transitionName");
            eventPoster.setTransitionName(transitionName);
            int id = b.getInt("feedId");

            final FeedItem current = feedViewModel.getFeedById(id);

            Glide.with(this)
                    .load(current.getEventImageUrl())
                    .centerInside()
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(eventPoster);

            eventTitle.setText(current.getEventName());
            eventVenue.setText(current.getEventVenue());

            if (current.getEventDescription() == null){
                current.setEventDescription("No Description");
            }
            final Markwon markwon = Markwon.builder(getContext())
                    .usePlugin(ImagesPlugin.create(getContext())).build();
            final Spanned spanned = markwon.toMarkdown(current.getEventDescription());
            markwon.setParsedMarkdown(eventDescription, spanned);

            SimpleDateFormat format = new SimpleDateFormat("dd MMM YYYY, hh:mm a");
            eventTime.setText(format.format(new Date(current.getEventDate())));

            guestTitle.setText("Speakers:");

            StringBuilder guests = new StringBuilder();
            for (String s : current.getGuests()) guests.append(s).append("\n");
            guestName.setText(guests.toString());

            if (current.isInterested()){
                interestedFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_blue)));
            } else {
                interestedFab.getBackground().setTint(getResources().getColor(R.color.dark_gray));
            }

            interestedFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleStar(current);
                }
            });

        }

        return view;
    }

    private void toggleStar(FeedItem item){
        if (item.isInterested()){
            item.setInterested(false);
        } else {
            item.setInterested(true);
        }
        feedViewModel.insert(item);

        getActivity().getSupportFragmentManager().beginTransaction()
                .detach(this)
                .attach(this)
                .commit();
    }



}
