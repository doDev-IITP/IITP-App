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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;

import java.util.ArrayList;

import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;


public class ClubDetailsFragment extends Fragment  {

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

        Bundle b = getArguments();
        if (b != null) {
            String transitionName = b.getString("transitionName");
            cover.setTransitionName(transitionName);
            String id = b.getString("clubId");

            final ClubItem current = clubViewModel.getClubById(id);

            Glide.with(this)
                    .load(current.getImage())
                    .centerInside()
                    .placeholder(R.drawable.baseline_dashboard_24)
                    .into(cover);

            name.setText(current.getName());

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
                    String website = current.getWebsite().contains("http") ? current.getWebsite() : "https://" + current.getWebsite();
                    customTabsIntent.launchUrl(getContext(), Uri.parse(website));
                }
            });

        }
        porRecyclerView=view.findViewById( R.id.por );
        porRecyclerView.setLayoutManager( new LinearLayoutManager( getContext(),LinearLayoutManager.HORIZONTAL,false) );
        ArrayList<PorItem> porItems=new ArrayList<>(  );
        porItems.add( new PorItem("Anmol Chaddha","https://firebasestorage.googleapis.com/v0/b/timetable-grobo.appspot.com/o/developers%2FIMG_20190726_154411(1)-min(1).jpg?alt=media&token=62203ad1-48a2-4568-a68c-f203a5a9ef14","Coordinator") );
        porItems.add( new PorItem("Aman Jee","https://firebasestorage.googleapis.com/v0/b/timetable-grobo.appspot.com/o/developers%2Fimg_aman.jpg?alt=media&token=b9fb030e-ec72-4c5d-9596-08f96e423c62","Coordinator") );
        porItems.add( new PorItem("Ashwani Yadav","https://scontent-bom1-1.xx.fbcdn.net/v/t1.0-9/13465932_575022276008402_3643238272861381971_n.jpg?_nc_cat=107&_nc_oc=AQl6h5Kelo5Yhj3FvLsD_7DokoGGJfQfV2lS8KQ51YnH5YoMDfBB7T7T6XOO4JFVeZo&_nc_ht=scontent-bom1-1.xx&oh=53d8be896066fb79aa4dc062754507e0&oe=5DE44B87","Coordinator") );
        porItems.add( new PorItem("Anmol Chaddha","https://firebasestorage.googleapis.com/v0/b/timetable-grobo.appspot.com/o/developers%2FIMG_20190726_154411(1)-min(1).jpg?alt=media&token=62203ad1-48a2-4568-a68c-f203a5a9ef14","Coordinator") );
        porItems.add( new PorItem("Aman Jee","https://firebasestorage.googleapis.com/v0/b/timetable-grobo.appspot.com/o/developers%2Fimg_aman.jpg?alt=media&token=b9fb030e-ec72-4c5d-9596-08f96e423c62","Coordinator") );
        porItems.add( new PorItem("Ashwani Yadav","https://scontent-bom1-1.xx.fbcdn.net/v/t1.0-9/13465932_575022276008402_3643238272861381971_n.jpg?_nc_cat=107&_nc_oc=AQl6h5Kelo5Yhj3FvLsD_7DokoGGJfQfV2lS8KQ51YnH5YoMDfBB7T7T6XOO4JFVeZo&_nc_ht=scontent-bom1-1.xx&oh=53d8be896066fb79aa4dc062754507e0&oe=5DE44B87","Coordinator") );
        PorAdapter porAdapter=new PorAdapter(porItems,getContext(),(PorAdapter.OnCategorySelectedListener)getActivity() );
        porRecyclerView.setAdapter( porAdapter );

        return view;
    }

    private void toggleStar(ClubItem item) {
        if (item.isFollowed()) {
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
