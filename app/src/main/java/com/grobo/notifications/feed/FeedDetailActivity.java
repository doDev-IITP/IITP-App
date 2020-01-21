package com.grobo.notifications.feed;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;
import com.grobo.notifications.utils.ImageViewerActivity;
import com.grobo.notifications.utils.utils;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;

import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;

public class FeedDetailActivity extends FragmentActivity {

    public static final String IMAGE_TRANSITION_NAME = "transitionImage";

    private FeedViewModel feedViewModel;
    private FeedItem current;
    private String myMongoId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        myMongoId = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_MONGO_ID, "");
        feedViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);

        ImageView imageView = findViewById(R.id.image);
        ViewCompat.setTransitionName(imageView, IMAGE_TRANSITION_NAME);

        TextView title = findViewById(R.id.title);
        TextView description = findViewById(R.id.description);
        TextView feedPoster = findViewById(R.id.feed_poster);
        TextView reactCount = findViewById(R.id.react_count);
        FloatingActionButton like = findViewById(R.id.fab_like);

        String feedId = getIntent().getStringExtra("feedId");

        current = feedViewModel.getFeedById(feedId);

        if (current != null) {

            Glide.with(this)
                    .load("")
                    .thumbnail(Glide.with(this).load(current.getEventImageUrl()))
                    .centerInside()
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(imageView);

            title.setText(current.getEventName());

            if (current.getEventDescription() == null) {
                current.setEventDescription("No Description");
            }

            feedPoster.setText(String.format("%s\n%s", current.getDataPoster().getName(), current.getDataPoster().getInstituteId().toUpperCase()));

            final Markwon markwon = Markwon.builder(this)
                    .usePlugin(GlideImagesPlugin.create(this))
                    .usePlugin(HtmlPlugin.create())
                    .build();

            final Spanned spanned = markwon.toMarkdown(current.getEventDescription());
            markwon.setParsedMarkdown(description, spanned);

            if (current.getEventImageUrl() != null && !current.getEventImageUrl().isEmpty()) {
                imageView.setOnClickListener(v -> {
                    Intent i = new Intent(FeedDetailActivity.this, ImageViewerActivity.class);
                    i.putExtra("image_url", current.getEventImageUrl());

                    ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this, imageView, "transition");
                    ActivityCompat.startActivity(FeedDetailActivity.this, i, activityOptions.toBundle());
                });
            }

            if (current.getLikes() != null)
                reactCount.setText(String.valueOf(current.getLikes().size()));
            else reactCount.setText("0");

            if (current.getLikes().contains(PreferenceManager.getDefaultSharedPreferences(this).getString(USER_MONGO_ID, "")))
                like.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_heart).colorRes(R.color.deep_red));
            else
                like.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_heart_o).colorRes(R.color.deep_red));

            like.setOnClickListener(v -> {
                FeedUtils.reactOnFeed(this, current.getId());
                if (current.getLikes().contains(myMongoId)) {
                    like.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_heart_o).colorRes(R.color.deep_red));
                    current.getLikes().remove(myMongoId);
                    reactCount.setText(String.valueOf(current.getLikes().size()));
                    feedViewModel.insert(current);
                } else {
                    like.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_heart).colorRes(R.color.deep_red));
                    current.getLikes().add(myMongoId);
                    reactCount.setText(String.valueOf(current.getLikes().size()));
                    feedViewModel.insert(current);}
            });

        } else {
            utils.showSimpleAlertDialog(this, "Alert!!!", "Feed not found.");
//            NavHostFragment.findNavController(this).navigateUp();
//            NavHostFragment.findNavController(this).navigate(R.id.nav_feed);
        }


    }

}
