package com.grobo.notifications.clubs;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.grobo.notifications.R;
import com.grobo.notifications.network.ClubRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;

public class ClubsFragment extends Fragment {

    private ClubViewModel clubViewModel;
    private View emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ClubsRecyclerAdapter adapter;
    private RecyclerView clubsRecyclerView;
    private SharedPreferences prefs;

    public ClubsFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null)
            getActivity().setTitle("Explore");
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

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_clubs);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if ((System.currentTimeMillis() - prefs.getLong("last_club_update_time", 0)) >= (60 * 1000)) {
            swipeRefreshLayout.setRefreshing(true);
            updateData();
        }
        emptyView = view.findViewById(R.id.clubs_empty_view);

        clubsRecyclerView = view.findViewById(R.id.rv_clubs);
        clubsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ClubsRecyclerAdapter(getContext(), (ClubsRecyclerAdapter.OnClubSelectedListener) getActivity());
        clubsRecyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), HORIZONTAL);
        clubsRecyclerView.addItemDecoration(itemDecor);

        observeAll();

        return view;
    }

    private void updateData() {

        ClubRoutes service = RetrofitClientInstance.getRetrofitInstance().create(ClubRoutes.class);

        Call<ClubItem.Clubs> call = service.getAllClubs();
        call.enqueue(new Callback<ClubItem.Clubs>() {
            @Override
            public void onResponse(Call<ClubItem.Clubs> call, Response<ClubItem.Clubs> response) {
                if (response.isSuccessful()) {
                    List<ClubItem> allItems = response.body().getClubs();

                    for (ClubItem newItem : allItems) {
                        clubViewModel.insert(newItem);
                    }

                }
                prefs.edit().putLong("last_club_update_time", System.currentTimeMillis()).apply();
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<ClubItem.Clubs> call, Throwable t) {
                Log.e("failure", t.getMessage());
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Update failed!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void observeAll() {
        clubViewModel.getAllClubs().removeObservers(ClubsFragment.this);
        clubViewModel.getAllClubs().observe(ClubsFragment.this, new Observer<List<ClubItem>>() {
            @Override
            public void onChanged(List<ClubItem> clubItems) {
                adapter.setClubList(clubItems);
                if (clubItems.size() == 0) {
                    clubsRecyclerView.setVisibility(View.INVISIBLE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    clubsRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

}