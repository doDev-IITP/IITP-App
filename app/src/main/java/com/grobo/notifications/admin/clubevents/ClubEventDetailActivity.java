package com.grobo.notifications.admin.clubevents;

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
import com.grobo.notifications.R;
import com.grobo.notifications.account.por.PORItem;
import com.grobo.notifications.network.EventsRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.utils.ImageViewerActivity;
import com.grobo.notifications.utils.utils;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;
import static com.grobo.notifications.utils.utils.openWebsiteIntent;

public class ClubEventDetailActivity extends FragmentActivity {

    private ClubEventViewModel viewModel;
    private boolean reload = false;
    private ProgressDialog progressDialog;

    private ClubEventItem current;
    private PORItem currentPor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_event_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        viewModel = new ViewModelProvider(this).get(ClubEventViewModel.class);

        ((FloatingActionButton) findViewById(R.id.fab_edit)).hide();

        if (getIntent().hasExtra("eventId")) {
            String eventId = getIntent().getStringExtra("eventId");

            if (getIntent().hasExtra("por")) {
                currentPor = getIntent().getParcelableExtra("por");
                if (currentPor != null) {
                    current = viewModel.getEventById(eventId);
                    showData();
                } else utils.showFinishAlertDialog(this, "Alert!!!", "Invalid POR.");
            } else {
                if (getIntent().hasExtra("reload"))
                    reload = getIntent().getBooleanExtra("reload", false);
                if (reload) {
                    downloadData(eventId);
                } else {
                    current = viewModel.getEventById(eventId);
                    showData();
                }
            }

        } else utils.showFinishAlertDialog(this, "Alert!!!", "Event not found.");
    }

    private void showData() {

        Iconify.with(new FontAwesomeModule());

        ImageView imageView = findViewById(R.id.image);
        if (getIntent().hasExtra("transition_image")) {
            ViewCompat.setTransitionName(imageView, getIntent().getStringExtra("transition_image"));
        }
        TextView title = findViewById(R.id.title);
        TextView date = findViewById(R.id.date);
        TextView venue = findViewById(R.id.venue);
        TextView description = findViewById(R.id.description);
        FloatingActionButton editFab = findViewById(R.id.fab_edit);

        if (current != null) {

            Glide.with(this)
                    .load("")
                    .thumbnail(Glide.with(this).load(current.getImageUrl()))
                    .centerInside()
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(imageView);

            title.setText(current.getName());

            if (current.getDescription() == null) {
                current.setDescription("No Description");
            }

            final Markwon markwon = Markwon.builder(this)
                    .usePlugin(GlideImagesPlugin.create(this))
                    .usePlugin(HtmlPlugin.create())
                    .build();

            final Spanned spanned = markwon.toMarkdown(current.getDescription());
            markwon.setParsedMarkdown(description, spanned);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            date.setText(dateFormat.format(current.getDate()));

            venue.setText(String.format("Venue: %s", current.getVenue()));

            if (current.getImageUrl() != null && !current.getImageUrl().isEmpty() && !current.getImageUrl().equals("placeholder")) {
                imageView.setOnClickListener(v -> {
                    Intent i = new Intent(ClubEventDetailActivity.this, ImageViewerActivity.class);
                    i.putExtra("image_url", current.getImageUrl());
                    if (getIntent().hasExtra("transition_image")) {
                        i.putExtra("transition_image", getIntent().getStringExtra("transition_image"));
                    }

                    ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this, imageView, "transition");
                    ActivityCompat.startActivity(ClubEventDetailActivity.this, i, activityOptions.toBundle());
                });
            }

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
                String link = getResources().getString(R.string.iitp_web) + "event/" + current.getId();
                utils.shareIntent(this, link);
            });

            ImageView clubImage = findViewById(R.id.club_image);
            TextView clubName = findViewById(R.id.club_name);
            TextView clubBio = findViewById(R.id.club_bio);
            clubName.setText(current.getRelatedClub().getName());
            clubBio.setText(current.getRelatedClub().getBio());
            Glide.with(this)
                    .load(current.getRelatedClub().getImage())
                    .centerInside()
                    .placeholder(R.mipmap.ic_launcher)
                    .into(clubImage);

            if (currentPor != null) {
                editFab.show();
                editFab.setOnClickListener(v -> {
                    Intent i = new Intent(ClubEventDetailActivity.this, EditClubEventDetailActivity.class);
                    i.putExtra("por", currentPor);
                    i.putExtra("eventId", current.getId());
                    startActivity(i);
                });
            } else {
                editFab.hide();
            }

        } else utils.showFinishAlertDialog(this, "Alert!!!", "Event not found.");

    }

    private void downloadData(String eventId) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_TOKEN, "0");

        EventsRoutes service = RetrofitClientInstance.getRetrofitInstance().create(EventsRoutes.class);

        Call<ClubEventItem> call = service.getEventById(token, eventId);

        call.enqueue(new Callback<ClubEventItem>() {
            @Override
            public void onResponse(@NonNull Call<ClubEventItem> call, @NonNull Response<ClubEventItem> response) {
                if (progressDialog != null) progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    current = response.body();
                }
                showData();
            }

            @Override
            public void onFailure(@NonNull Call<ClubEventItem> call, @NonNull Throwable t) {
                if (progressDialog != null) progressDialog.dismiss();
            }
        });

    }


}
