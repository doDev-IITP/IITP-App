package com.grobo.notifications.clubs;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.grobo.notifications.R;
import com.grobo.notifications.network.ClubRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;
import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class ClubsFragment extends Fragment {

    private ClubViewModel clubViewModel;
    private View emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ClubsRecyclerAdapter adapter;
    private RecyclerView clubsRecyclerView;
    private SharedPreferences prefs;
    private List<ClubItem> allClubs = new ArrayList<>();

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
        clubViewModel = new ViewModelProvider(this).get(ClubViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_clubs);
        swipeRefreshLayout.setOnRefreshListener(this::updateData);

        if (getContext() != null)
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if ((System.currentTimeMillis() - prefs.getLong("last_club_update_time", 0)) >= (6 * 60 * 60 * 1000)) {
            swipeRefreshLayout.setRefreshing(true);
            updateData();
        }

        emptyView = view.findViewById(R.id.clubs_empty_view);
        clubsRecyclerView = view.findViewById(R.id.rv_clubs);
        clubsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ViewTreeObserver observer = clubsRecyclerView.getViewTreeObserver();
        postponeEnterTransition();
        observer.addOnGlobalLayoutListener(this::startPostponedEnterTransition);

        adapter = new ClubsRecyclerAdapter(getContext());
        clubsRecyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), HORIZONTAL);
        clubsRecyclerView.addItemDecoration(itemDecor);

        observeAll();

        SearchView searchView = view.findViewById(R.id.sv_club_list);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                List<ClubItem> temp = new ArrayList<>();
                for (ClubItem ci : allClubs)
                    if (ci.getName().toLowerCase().contains(s.toLowerCase()))
                        temp.add(ci);
                adapter.setClubList(temp);

                return false;
            }
        });

        return view;
    }

    private void updateData() {
        String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(USER_TOKEN, "0");

        ClubRoutes service = RetrofitClientInstance.getRetrofitInstance().create(ClubRoutes.class);

        Call<List<ClubItem>> call = service.getAllClubs(token);
        call.enqueue(new Callback<List<ClubItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<ClubItem>> call, @NonNull Response<List<ClubItem>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        List<ClubItem> allItems = response.body();
                        for (ClubItem newItem : allItems) {
                            clubViewModel.insert(newItem);
                        }

                    }
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Clubs updated.", Toast.LENGTH_SHORT).show();
                        prefs.edit().putLong("last_club_update_time", System.currentTimeMillis()).apply();
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<ClubItem>> call, @NonNull Throwable t) {
                if (t.getMessage() != null)
                    Log.e("failure", t.getMessage());
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Update failed!! Please check internet connection!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void observeAll() {
        clubViewModel.getAllClubs().removeObservers(ClubsFragment.this);
        clubViewModel.getAllClubs().observe(getViewLifecycleOwner(), clubItems -> {
            adapter.setClubList(clubItems);
            allClubs = clubItems;
            if (clubItems.size() == 0) {
                clubsRecyclerView.setVisibility(View.INVISIBLE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                clubsRecyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.INVISIBLE);
            }
        });
    }

}