package com.grobo.notifications.admin.clubevents;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;
import com.grobo.notifications.admin.XPortal;
import com.grobo.notifications.main.MainActivity;
import com.grobo.notifications.network.EventsRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class ClubEventFragment extends Fragment {

    public ClubEventFragment() {
    }

    private SwipeRefreshLayout swipeRefreshLayout;
    private ClubEventRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyView;
    private FloatingActionButton addFab;

    private ClubEventViewModel viewModel;

    private String clubId;
    private boolean refreshed = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clubId = getArguments().getString("club_id", "");
        viewModel = ViewModelProviders.of(this).get(ClubEventViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_club_event, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_club_event);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.e("events", "refreshing");
            updateData();
        });
        if (!refreshed) {
            swipeRefreshLayout.setRefreshing(true);
            updateData();
        }

        addFab = view.findViewById(R.id.add_club_event_fab);
        addFab.hide();
        emptyView = view.findViewById(R.id.club_events_empty_view);
        recyclerView = view.findViewById(R.id.recycler_club_events);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClubEventRecyclerAdapter(getContext(), (ClubEventRecyclerAdapter.OnEventSelectedListener) getActivity());
        recyclerView.setAdapter(adapter);

        populateRecycler();

        if (getContext() instanceof MainActivity) {

            addFab.hide();

        } else if (getContext() instanceof XPortal) {

//            addFab.show();
//            addFab.setOnClickListener(v -> {
//                Intent intent = new Intent(getContext(), AddFeedActivity.class);
//                startActivity(intent);
//            });

        }

        return view;
    }

    private void populateRecycler() {

        viewModel.getAllClubEvents(clubId).removeObservers(ClubEventFragment.this);
        viewModel.getAllClubEvents(clubId).observe(ClubEventFragment.this, clubEventItems -> {

            adapter.setClubEventItemList(clubEventItems);
            if (clubEventItems.size() == 0) {
                recyclerView.setVisibility(View.INVISIBLE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.INVISIBLE);
            }

        });
    }

    private void updateData() {

        String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(USER_TOKEN, "0");

        EventsRoutes service = RetrofitClientInstance.getRetrofitInstance().create(EventsRoutes.class);

        Call<ClubEventItem.ClubEventSuper> call = service.getEventsByClub(token, clubId);
        call.enqueue(new Callback<ClubEventItem.ClubEventSuper>() {
            @Override
            public void onResponse(Call<ClubEventItem.ClubEventSuper> call, Response<ClubEventItem.ClubEventSuper> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getEvents() != null) {
                        List<ClubEventItem> allItems = response.body().getEvents();

                        if (allItems != null) {
                            viewModel.deleteEventByClubId(clubId);
                            for (ClubEventItem item : allItems) {
                                viewModel.insert(item);
                            }
                        }
                        refreshed = true;
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to get events!", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ClubEventItem.ClubEventSuper> call, Throwable t) {
                Log.e("failure", t.getMessage());
                Toast.makeText(getContext(), "Event fetch failure!!", Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }


}
