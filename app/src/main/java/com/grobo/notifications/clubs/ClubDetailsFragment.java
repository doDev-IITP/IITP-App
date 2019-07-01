package com.grobo.notifications.clubs;

import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;

import ru.noties.markwon.Markwon;
import ru.noties.markwon.image.ImagesPlugin;

public class ClubDetailsFragment extends Fragment {

    public ClubDetailsFragment() {}

    private ClubViewModel clubViewModel;

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

        Bundle b = getArguments();
        if (b != null) {
            String transitionName = b.getString("transitionName");
            cover.setTransitionName(transitionName);
            int id = b.getInt("clubId");

            final ClubItem current = clubViewModel.getClubById(id);

            Glide.with(this)
                    .load(current.getImage())
                    .centerInside()
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(cover);

            name.setText(current.getName());

            if (current.getDescription() == null){
                current.setDescription("No Description");
            }
            final Markwon markwon = Markwon.builder(getContext())
                    .usePlugin(ImagesPlugin.create(getContext())).build();
            final Spanned spanned = markwon.toMarkdown(current.getDescription());
            markwon.setParsedMarkdown(description, spanned);

            bio.setText(current.getBio());

            if (current.isFollowed()){
                followingFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_blue)));
            } else {
                followingFab.getBackground().setTint(getResources().getColor(R.color.dark_gray));
            }

            followingFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleStar(current);
                }
            });

            website.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));

                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(getContext(), Uri.parse(current.getWebsite()));
                }
            });

        }

        return view;
    }

    private void toggleStar(ClubItem item){
        if (item.isFollowed()){
            item.setFollowed(false);
        } else {
            item.setFollowed(true);
        }
        clubViewModel.insert(item);

        getActivity().getSupportFragmentManager().beginTransaction()
                .detach(this)
                .attach(this)
                .commit();
    }

}
