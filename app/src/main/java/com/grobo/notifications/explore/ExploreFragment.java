package com.grobo.notifications.explore;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.grobo.notifications.explore.clubs.ClubsFragment;
import com.grobo.notifications.R;
import com.grobo.notifications.explore.services.ServicesFragment;
import com.grobo.notifications.timetable.TimetableActivity;

import java.util.Objects;

public class ExploreFragment extends Fragment {


    public ExploreFragment() {}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);

        View exploreClubs = rootView.findViewById(R.id.ll_explore_club);
        exploreClubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transactFragment(new ClubsFragment());
            }
        });

        View exploreTimetable = rootView.findViewById(R.id.ll_explore_timetable);
        exploreTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TimetableActivity.class));
            }
        });

        View exploreCalender = rootView.findViewById(R.id.ll_explore_calender);
        exploreCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transactFragment(new CalenderFragment());
            }
        });

        View exploreMess = rootView.findViewById(R.id.ll_explore_mess);
        exploreMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transactFragment(new MessFragment());
            }
        });

        View exploreExam = rootView.findViewById(R.id.ll_explore_exam);
        exploreExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transactFragment(new ExamFragment());
            }
        });

        View exploreLinks = rootView.findViewById(R.id.ll_explore_links);
        exploreLinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transactFragment(new LinksFragment());
            }
        });

        View exploreServices = rootView.findViewById(R.id.ll_explore_services);
        exploreServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transactFragment(new ServicesFragment());
            }
        });

        return rootView;
    }

    private void transactFragment(Fragment frag){
        FragmentTransaction fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        fragmentManager.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out)
                .replace(R.id.frame_layout_main, frag, frag.getTag())
                .addToBackStack("later_fragment")
                .commit();
    }

}
