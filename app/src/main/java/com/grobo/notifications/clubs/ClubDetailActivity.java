package com.grobo.notifications.clubs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;
import com.grobo.notifications.admin.clubevents.ClubEventActivity;
import com.grobo.notifications.network.ClubRoutes;
import com.grobo.notifications.network.PorRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.profile.UserProfileActivity;
import com.grobo.notifications.utils.ImageViewerActivity;
import com.grobo.notifications.utils.utils;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;
import static com.grobo.notifications.utils.utils.openWebsiteIntent;

public class ClubDetailActivity extends FragmentActivity implements PorAdapter.OnPORSelectedListener {

    private ClubViewModel clubViewModel;

    private ProgressDialog progressDialog;

    private PorAdapter porAdapter;
    private View porListParent;

    private ClubItem current;

    private boolean reload = false;
    private boolean porRefreshed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        clubViewModel = new ViewModelProvider(this).get(ClubViewModel.class);

        if (getIntent().hasExtra("reload"))
            reload = getIntent().getBooleanExtra("reload", false);

        if (reload) {
            String clubId = getIntent().getStringExtra("clubId");
            downloadClubData(clubId);
        } else {
            String clubId = getIntent().getStringExtra("clubId");
            current = clubViewModel.getClubById(clubId);
            showData();
        }


    }

    private void showData() {

        Iconify.with(new FontAwesomeModule());

        ImageView imageView = findViewById(R.id.image);
        if (getIntent().hasExtra("transition_image")) {
            ViewCompat.setTransitionName(imageView, getIntent().getStringExtra("transition_image"));
        }

        TextView title = findViewById(R.id.title);
        TextView description = findViewById(R.id.description);
        TextView clubBio = findViewById(R.id.club_bio);
        TextView followersCount = findViewById(R.id.react_count);
        FloatingActionButton follow = findViewById(R.id.fab_follow);
        CardView events = findViewById(R.id.club_card_events);
        CardView website = findViewById(R.id.cv_website);
        CardView share = findViewById(R.id.cv_share);

        if (current != null) {

            Glide.with(this)
                    .load("")
                    .thumbnail(Glide.with(this).load(current.getImage()))
                    .centerInside()
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(imageView);

            title.setText(current.getName());

            if (current.getDescription() == null) current.setDescription("No Description");

            clubBio.setText(current.getBio());

            final Markwon markwon = Markwon.builder(this)
                    .usePlugin(GlideImagesPlugin.create(this))
                    .usePlugin(HtmlPlugin.create())
                    .build();

            final Spanned spanned = markwon.toMarkdown(current.getDescription());
            markwon.setParsedMarkdown(description, spanned);

            if (current.getImage() != null && !current.getImage().isEmpty() && !current.getImage().equals("placeholder")) {
                imageView.setOnClickListener(v -> {
                    Intent i = new Intent(ClubDetailActivity.this, ImageViewerActivity.class);
                    i.putExtra("image_url", current.getImage());
                    if (getIntent().hasExtra("transition_image")) {
                        i.putExtra("transition_image", getIntent().getStringExtra("transition_image"));
                    }

                    ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this, imageView, "transition");
                    ActivityCompat.startActivity(ClubDetailActivity.this, i, activityOptions.toBundle());
                });
            }

            followersCount.setText(String.valueOf(current.getFollowers()));

            if (current.getLiked())
                follow.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_heart).colorRes(R.color.club_detail_like));
            else
                follow.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_heart_o).colorRes(R.color.club_detail_like));

            follow.setOnClickListener(v -> {
                ClubUtils.followClub(this, current.getId());
                if (current.getLiked()) {
                    follow.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_heart_o).colorRes(R.color.club_detail_like));
                    current.setLiked(false);
                    current.setFollowers(current.getFollowers() - 1);
                } else {
                    follow.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_heart).colorRes(R.color.club_detail_like));
                    current.setLiked(true);
                    current.setFollowers(current.getFollowers() + 1);
                }
                followersCount.setText(String.valueOf(current.getFollowers()));
                clubViewModel.insert(current);
            });

            if (current.getPages() != null) {
                for (String page : current.getPages()) {
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

            share.setOnClickListener(v -> {
                String link = getResources().getString(R.string.iitp_web) + "club/" + current.getId();
                utils.shareIntent(this, link);
            });

            website.setOnClickListener(v -> utils.openWebsiteIntent(ClubDetailActivity.this, current.getWebsite()));

            events.setOnClickListener(v -> {
                Intent i = new Intent(ClubDetailActivity.this, ClubEventActivity.class);
                i.putExtra("club_id", current.getId());
                startActivity(i);
            });


            porListParent = findViewById(R.id.por_cv_clubs);
            RecyclerView porRecyclerView = findViewById(R.id.por_recycler_clubs);
            porRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            porAdapter = new PorAdapter(this, this);
            porRecyclerView.setAdapter(porAdapter);

            if (!porRefreshed) {
                getClubPOR(current.getId());
            }
        } else {
            utils.showSimpleAlertDialog(this, "Alert!!!", "Club not found.");
        }
    }

    private void downloadClubData(String clubId) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_TOKEN, "0");

        ClubRoutes service = RetrofitClientInstance.getRetrofitInstance().create(ClubRoutes.class);

        Call<ClubItem> call = service.getClubById(token, clubId);
        call.enqueue(new Callback<ClubItem>() {
            @Override
            public void onResponse(@NonNull Call<ClubItem> call, @NonNull Response<ClubItem> response) {
                if (progressDialog != null) progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    current = response.body();
                }
                showData();
            }

            @Override
            public void onFailure(@NonNull Call<ClubItem> call, @NonNull Throwable t) {
                if (progressDialog != null) progressDialog.dismiss();
            }
        });
    }

    private void getClubPOR(String clubId) {
        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_TOKEN, "0");

        PorRoutes service = RetrofitClientInstance.getRetrofitInstance().create(PorRoutes.class);

        Call<ResponseBody> call = service.getPorByClub(token, clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<ClubPorItem> allPors = new ArrayList<>();
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            JSONArray array = object.getJSONArray("pors");

                            for (int i = 0; i < array.length(); i++) {

                                ClubPorItem newPor = new ClubPorItem();
                                JSONObject singlePor = array.getJSONObject(i);

                                if (singlePor.has("_id"))
                                    newPor.setPorId(singlePor.getString("_id"));
                                if (singlePor.has("club"))
                                    newPor.setClubId(singlePor.getString("club"));

                                if (singlePor.has("code"))
                                    newPor.setCode(singlePor.getInt("code"));
                                if (!(newPor.getCode() > 0)) continue;

                                if (singlePor.has("position"))
                                    newPor.setPosition(singlePor.getString("position"));

                                if (singlePor.has("user")) {
                                    JSONObject user = singlePor.getJSONObject("user");
                                    if (user.has("_id"))
                                        newPor.setUserId(user.getString("_id"));
                                    if (user.has("name"))
                                        newPor.setName(user.getString("name"));
                                    if (user.has("instituteId"))
                                        newPor.setInstituteId(user.getString("instituteId"));
                                    if (user.has("image"))
                                        newPor.setImage(user.getString("image"));
                                }
                                allPors.add(newPor);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (!allPors.isEmpty()) {
                            porListParent.setVisibility(View.VISIBLE);
                            porAdapter.setItemList(allPors);
                        }
                    }
                } else {
                    Toast.makeText(ClubDetailActivity.this, "Failed to get PORs!", Toast.LENGTH_SHORT).show();
                }
                porRefreshed = true;
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("failure", t.getMessage());
                Toast.makeText(ClubDetailActivity.this, "PORs fetch failure!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPORSelected(String userId) {
        Intent i = new Intent(ClubDetailActivity.this, UserProfileActivity.class);
        i.putExtra("user_id", userId);
        startActivity(i);
    }
}
