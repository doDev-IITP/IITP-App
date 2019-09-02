package com.grobo.notifications.feed;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.transition.TransitionInflater;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;
import com.grobo.notifications.admin.XPortal;
import com.grobo.notifications.feed.addfeed.AddFeedActivity;
import com.grobo.notifications.main.MainActivity;
import com.grobo.notifications.utils.ImageViewerActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;

import static com.grobo.notifications.utils.Constants.USER_POR;

public class FeedDetailFragment extends Fragment {

    public FeedDetailFragment() {
    }

    private FeedItem current;

    public static FeedDetailFragment newInstance() {
        return new FeedDetailFragment();
    }

    private FeedViewModel feedViewModel;
    private FloatingActionButton interestedFab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);

        setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.slide_bottom));
        setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.slide_bottom));
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.default_transition));
        setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.default_transition));
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
        TextView eventDescription = view.findViewById(R.id.tv_event_detail_description);

        Bundle b = getArguments();

        if (getContext() instanceof MainActivity || getContext() instanceof XPortal) {

            if (b != null) {
                int transitionPosition = b.getInt("transition_position");
                eventPoster.setTransitionName("transition_image" + transitionPosition);
                eventTime.setTransitionName("transition_time" + transitionPosition);
                eventVenue.setTransitionName("transition_venue" + transitionPosition);
                eventTitle.setTransitionName("transition_title" + transitionPosition);
                String id = b.getString("id");

                current = feedViewModel.getFeedById(id);

                if (current != null) {

                    Glide.with(this)
                            .load("")
                            .thumbnail(Glide.with(this).load(current.getEventImageUrl()))
                            .centerInside()
                            .placeholder(R.drawable.baseline_dashboard_24)
                            .into(eventPoster);

                    eventTitle.setText(current.getEventName());
                    eventVenue.setText(current.getEventVenue());

                    if (current.getEventDescription() == null) {
                        current.setEventDescription("No Description");
                    }

                    final Markwon markwon = Markwon.builder(getContext())
                            .usePlugin(GlideImagesPlugin.create(getContext()))
                            .usePlugin(HtmlPlugin.create())
                            .build();

                    final Spanned spanned = markwon.toMarkdown(current.getEventDescription());
                    markwon.setParsedMarkdown(eventDescription, spanned);

                    SimpleDateFormat format = new SimpleDateFormat("dd MMM YYYY, hh:mm a", Locale.getDefault());
                    eventTime.setText(format.format(new Date(current.getEventDate())));

                    if (current.isInterested()) {
                        interestedFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_blue)));
                    } else {
                        interestedFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark_gray)));
                    }

                    interestedFab.setOnClickListener(v -> toggleStar());

                    if (current.getEventImageUrl() != null && !current.getEventImageUrl().isEmpty()) {
                        eventPoster.setOnClickListener(v -> {
                            Intent i = new Intent(getActivity(), ImageViewerActivity.class);
                            i.putExtra("image_url", current.getEventImageUrl());

                            if (getActivity() != null) {
                                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        getActivity(), eventPoster, "transition" + transitionPosition);
                                ActivityCompat.startActivity(getContext(), i, activityOptions.toBundle());
                            }
                        });
                    }
                }
            }

        } else if (getContext() instanceof AddFeedActivity) {

            interestedFab.hide();
            if (b != null) {

                Glide.with(this)
                        .load(b.getString("image"))
                        .centerInside()
                        .placeholder(R.drawable.baseline_dashboard_24)
                        .into(eventPoster);

                eventTitle.setText(b.getString("title"));
                eventVenue.setText(b.getString("venue"));

                final Markwon markwon = Markwon.builder(getContext())
                        .usePlugin(GlideImagesPlugin.create(getContext()))
                        .usePlugin(HtmlPlugin.create())
                        .build();

                final Spanned spanned = markwon.toMarkdown(b.getString("description", "No Description"));
                markwon.setParsedMarkdown(eventDescription, spanned);

                SimpleDateFormat format = new SimpleDateFormat("dd MMM YYYY, hh:mm a", Locale.getDefault());
                eventTime.setText(format.format(new Date(b.getLong("date"))));
            }
        }

        if (getContext() instanceof XPortal) {

            List<String> itemsList = Converters.arrayFromString(PreferenceManager
                    .getDefaultSharedPreferences(getContext()).getString(USER_POR, ""));

            if (itemsList.size() != 0)

                for (String por : itemsList) {
                    String[] array = por.split("_", 2);
                    String club = array[1];
                }

            //TODO : implement feed edit and delete functionality
        }

        return view;
    }

    private void toggleStar() {
        if (current.isInterested()) {
            current.setInterested(false);
            interestedFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark_gray)));
        } else {
            current.setInterested(true);
            interestedFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_blue)));
        }
        feedViewModel.insert(current);
    }

}
