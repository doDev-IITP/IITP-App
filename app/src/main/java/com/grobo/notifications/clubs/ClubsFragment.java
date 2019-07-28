package com.grobo.notifications.clubs;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;

import java.util.List;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;

public class ClubsFragment extends Fragment {

    private ClubViewModel clubViewModel;

    public ClubsFragment() {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );
        if(getActivity()!=null)
        getActivity().setTitle( "Clubs" );
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clubViewModel = ViewModelProviders.of(this).get(ClubViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);

        RecyclerView clubsRecyclerView = view.findViewById(R.id.rv_clubs);
        clubsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ClubsRecyclerAdapter adapter = new ClubsRecyclerAdapter(getContext(), (ClubsRecyclerAdapter.OnClubSelectedListener)getActivity());
        clubsRecyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), HORIZONTAL);
        clubsRecyclerView.addItemDecoration(itemDecor);

        List<ClubItem> clubs = clubViewModel.getAllClubs();

        adapter.setClubList(clubs);

        return view;
    }
}