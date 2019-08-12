package com.grobo.notifications.clubs;

import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;
import com.grobo.notifications.admin.clubevents.ClubEventFragment;
import com.grobo.notifications.network.OtherRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import java.util.ArrayList;

import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;


public class ClubDetailsFragment extends Fragment {

    public ClubDetailsFragment() {
    }

    private ClubViewModel clubViewModel;
    private RecyclerView porRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clubViewModel = ViewModelProviders.of(this).get(ClubViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_club_details, container, false);

        ImageView cover = view.findViewById(R.id.event_detail_header_bg);
        TextView name = view.findViewById(R.id.tv_event_detail_title);
        FloatingActionButton followingFab = view.findViewById(R.id.fab_event_detail_interested);
        TextView bio = view.findViewById(R.id.tv_event_detail_time);
        ImageView website = view.findViewById(R.id.fab_website);
        TextView description = view.findViewById(R.id.tv_event_detail_description);
        View events = view.findViewById(R.id.club_card_events);

        Bundle b = getArguments();
        if (b != null) {
            String transitionName = b.getString("transitionName");
            cover.setTransitionName(transitionName);
            String id = b.getString("clubId");

            final ClubItem current = clubViewModel.getClubById(id);

            if (current != null) {

                Glide.with(this)
                        .load(current.getImage())
                        .centerInside()
                        .placeholder(R.drawable.baseline_dashboard_24)
                        .into(cover);

                name.setText(current.getName());

                getClubPOR(current.getId());

                if (current.getDescription() == null) {
                    current.setDescription("No Description");
                }
                final Markwon markwon = Markwon.builder(getContext())
                        .usePlugin(GlideImagesPlugin.create(getContext()))
                        .usePlugin(HtmlPlugin.create())
                        .build();
                final Spanned spanned = markwon.toMarkdown(current.getDescription());
                markwon.setParsedMarkdown(description, spanned);

                bio.setText(current.getBio());

                if (current.isFollowed()) {
                    followingFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_blue)));
                } else {
                    followingFab.getBackground().setTint(getResources().getColor(R.color.dark_gray));
                }

                followingFab.setOnClickListener(v -> toggleStar(current));

                website.setOnClickListener(v -> {
                    openWebsite(current.getWebsite());
                });


                if (current.getEvents() != null && current.getEvents().size() > 1) {

                    events.setVisibility(View.VISIBLE);
                    events.setOnClickListener(v -> {

                        Fragment eventsFragment = new ClubEventFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("club_id", current.getId());
                        eventsFragment.setArguments(bundle);

                        if (getActivity() != null)
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_main, eventsFragment)
                                    .addToBackStack("later_fragment")
                                    .commit();
                    });
                }

                if (current.getPages() != null) {
                    for (String page : current.getPages()) {

                        if (page.contains("facebook")) {
                            ImageView fb = view.findViewById(R.id.club_facebook);
                            fb.setVisibility(View.VISIBLE);
                            fb.setOnClickListener(view1 -> {
                                openWebsite(page);
                            });
                        } else if (page.contains("instagram")) {
                            ImageView inst = view.findViewById(R.id.club_instagram);
                            inst.setVisibility(View.VISIBLE);
                            inst.setOnClickListener(view1 -> {
                                openWebsite(page);
                            });
                        } else if (page.contains("twitter")) {
                            ImageView tw = view.findViewById(R.id.club_twitter);
                            tw.setVisibility(View.VISIBLE);
                            tw.setOnClickListener(view1 -> {
                                openWebsite(page);
                            });
                        }

                    }
                }

            }
        }

        porRecyclerView = view.findViewById(R.id.por);
        porRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        ArrayList<PorItem> porItems = new ArrayList<>();
        porItems.add(new PorItem("Anmol Chaddha", "https://firebasestorage.googleapis.com/v0/b/timetable-grobo.appspot.com/o/developers%2FIMG_20190726_154411(1)-min(1).jpg?alt=media&token=62203ad1-48a2-4568-a68c-f203a5a9ef14", "Coordinator"));
        porItems.add(new PorItem("Aman Jee", "https://firebasestorage.googleapis.com/v0/b/timetable-grobo.appspot.com/o/developers%2Fimg_aman.jpg?alt=media&token=b9fb030e-ec72-4c5d-9596-08f96e423c62", "Coordinator"));
        porItems.add(new PorItem("Ashwani Yadav", "https://scontent-bom1-1.xx.fbcdn.net/v/t1.0-9/13465932_575022276008402_3643238272861381971_n.jpg?_nc_cat=107&_nc_oc=AQl6h5Kelo5Yhj3FvLsD_7DokoGGJfQfV2lS8KQ51YnH5YoMDfBB7T7T6XOO4JFVeZo&_nc_ht=scontent-bom1-1.xx&oh=53d8be896066fb79aa4dc062754507e0&oe=5DE44B87", "Coordinator"));
        porItems.add(new PorItem("Anmol Chaddha", "https://firebasestorage.googleapis.com/v0/b/timetable-grobo.appspot.com/o/developers%2FIMG_20190726_154411(1)-min(1).jpg?alt=media&token=62203ad1-48a2-4568-a68c-f203a5a9ef14", "Coordinator"));
        porItems.add(new PorItem("Aman Jee", "https://firebasestorage.googleapis.com/v0/b/timetable-grobo.appspot.com/o/developers%2Fimg_aman.jpg?alt=media&token=b9fb030e-ec72-4c5d-9596-08f96e423c62", "Coordinator"));
        porItems.add(new PorItem("Ashwani Yadav", "https://scontent-bom1-1.xx.fbcdn.net/v/t1.0-9/13465932_575022276008402_3643238272861381971_n.jpg?_nc_cat=107&_nc_oc=AQl6h5Kelo5Yhj3FvLsD_7DokoGGJfQfV2lS8KQ51YnH5YoMDfBB7T7T6XOO4JFVeZo&_nc_ht=scontent-bom1-1.xx&oh=53d8be896066fb79aa4dc062754507e0&oe=5DE44B87", "Coordinator"));
        PorAdapter porAdapter = new PorAdapter(porItems, getContext(), (PorAdapter.OnCategorySelectedListener) getActivity());
        porRecyclerView.setAdapter(porAdapter);

        return view;
    }

    private void toggleStar(ClubItem item) {
        if (item.isFollowed()) {
            item.setFollowed(false);
        } else {
            item.setFollowed(true);
        }
        clubViewModel.insert(item);

        if (getActivity() != null)
            getActivity().getSupportFragmentManager().beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
    }

    private void getClubPOR(String clubId) {
        String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(USER_TOKEN, "0");

        OtherRoutes service = RetrofitClientInstance.getRetrofitInstance().create(OtherRoutes.class);

        Call<ResponseBody> call = service.getPorByClub(token, clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
//TODO:sjsxjsxb


                    }
                } else {
                    Toast.makeText(getContext(), "Failed to get PORs!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("failure", t.getMessage());
                Toast.makeText(getContext(), "PORs fetch failure!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openWebsite(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        String website1 = url;
        if (!website1.startsWith("http") && !website1.startsWith("https")) {
            website1 = "https://" + website1;
        }

        Log.e("website", website1);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getContext(), Uri.parse(website1));
    }
}
