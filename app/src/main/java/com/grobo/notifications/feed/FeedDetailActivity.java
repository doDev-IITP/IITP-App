package com.grobo.notifications.feed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.grobo.notifications.R;
import com.grobo.notifications.feed.addfeed.AddFeedActivity;
import com.grobo.notifications.network.FeedRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.utils.ImageViewerActivity;
import com.grobo.notifications.utils.utils;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;
import static com.grobo.notifications.utils.Constants.USER_TOKEN;
import static com.grobo.notifications.utils.utils.openWebsiteIntent;

public class FeedDetailActivity extends FragmentActivity {

    public static final String IMAGE_TRANSITION_NAME = "transitionImage";

    private FeedViewModel feedViewModel;
    private FeedItem current;
    private String myMongoId;
    private boolean reload = false;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        myMongoId = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_MONGO_ID, "");
        feedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        ((FloatingActionButton) findViewById(R.id.fab_edit)).hide();

        if (getIntent().hasExtra("reload"))
            reload = getIntent().getBooleanExtra("reload", false);

        if (!reload) {
            String feedId = getIntent().getStringExtra("feedId");
            current = feedViewModel.getFeedById(feedId);
            showData();
        } else {
            String feedId = getIntent().getStringExtra("feedId");
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            downloadData(feedId);
        }

    }

    private void showData() {

        Iconify.with(new FontAwesomeModule());

        ImageView imageView = findViewById(R.id.image);
        ViewCompat.setTransitionName(imageView, IMAGE_TRANSITION_NAME);

        TextView title = findViewById(R.id.title);
        TextView description = findViewById(R.id.description);
        TextView feedPoster = findViewById(R.id.feed_poster);
        TextView reactCount = findViewById(R.id.react_count);
        FloatingActionButton like = findViewById(R.id.fab_like);
        FloatingActionButton editFab = findViewById(R.id.fab_edit);

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
                    i.putExtra("transition_image", IMAGE_TRANSITION_NAME);

                    ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this, imageView, "transition");
                    ActivityCompat.startActivity(FeedDetailActivity.this, i, activityOptions.toBundle());
                });
            }

            if (current.getLikes() != null)
                reactCount.setText(String.valueOf(current.getLikes().size()));
            else reactCount.setText("0");

            if (current.getLikes().contains(PreferenceManager.getDefaultSharedPreferences(this).getString(USER_MONGO_ID, "")))
                like.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_heart).colorRes(R.color.feed_detail_like));
            else
                like.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_heart_o).colorRes(R.color.feed_detail_like));

            like.setOnClickListener(v -> {
                FeedUtils.reactOnFeed(this, current.getId());
                if (current.getLikes().contains(myMongoId)) {
                    like.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_heart_o).colorRes(R.color.feed_detail_like));
                    current.getLikes().remove(myMongoId);
                    reactCount.setText(String.valueOf(current.getLikes().size()));
                    feedViewModel.insert(current);
                } else {
                    like.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_heart).colorRes(R.color.feed_detail_like));
                    current.getLikes().add(myMongoId);
                    reactCount.setText(String.valueOf(current.getLikes().size()));
                    feedViewModel.insert(current);
                }
            });

            if (current.getPostLinks() != null) {
                for (String page : current.getPostLinks()) {
                    if (page.contains("facebook")) {
                        ImageView fb = findViewById(R.id.feed_facebook);
                        fb.setVisibility(View.VISIBLE);
                        fb.setOnClickListener(view1 -> openWebsiteIntent(this, page));
                    } else if (page.contains("instagram")) {
                        ImageView inst = findViewById(R.id.feed_instagram);
                        inst.setVisibility(View.VISIBLE);
                        inst.setOnClickListener(view1 -> openWebsiteIntent(this, page));
                    } else if (page.contains("twitter")) {
                        ImageView tw = findViewById(R.id.feed_twitter);
                        tw.setVisibility(View.VISIBLE);
                        tw.setOnClickListener(view1 -> openWebsiteIntent(this, page));
                    }
                }
            }

            ImageView share = findViewById(R.id.share);
            share.setOnClickListener(v -> {
                String link = getResources().getString(R.string.iitp_web) + "feed/" + current.getId();
                utils.shareIntent(this, link);
            });

            TextView lastEdited = findViewById(R.id.last_edited);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            lastEdited.setText(String.format("Last edited: %s", dateFormat.format(current.getEventId())));

            if (current.getDataPoster().getId().equals(myMongoId)) {
                editFab.show();
                editFab.setOnClickListener(v -> {
                    Intent i = new Intent(FeedDetailActivity.this, AddFeedActivity.class);
                    i.putExtra("feedId", current.getId());
                    startActivity(i);
                });
            } else {
                editFab.hide();
            }

        } else utils.showFinishAlertDialog(this, "Alert!!!", "Feed not found.");

    }

    private void downloadData(String feedId) {

        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_TOKEN, "0");

        FeedRoutes service = RetrofitClientInstance.getRetrofitInstance().create(FeedRoutes.class);

        Call<ResponseBody> call = service.getFeedById(token, feedId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (progressDialog != null) progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject root = new JSONObject(response.body().string());

                        Gson gson = new Gson();
                        current = gson.fromJson(root.getJSONObject("feed").toString(), FeedItem.class);

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
                showData();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (progressDialog != null) progressDialog.dismiss();
            }
        });
    }


}
