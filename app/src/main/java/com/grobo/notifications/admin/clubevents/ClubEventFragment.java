package com.grobo.notifications.admin.clubevents;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;
import com.grobo.notifications.account.por.PORItem;
import com.grobo.notifications.network.EventsRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.utils.utils;

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

    private PORItem porItem = null;
    private String clubId = null;

    private boolean refreshed = false;

    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getContext() != null)
            context = getContext();

        if (getArguments() != null && getArguments().containsKey("por")) {
            porItem = getArguments().getParcelable("por");
            if (porItem != null)
                clubId = porItem.getClubId();
            else
                utils.showSimpleAlertDialog(context, "Alert!!!", "Error in retrieving POR data!");

        } else if (getArguments() != null && getArguments().containsKey("club_id")) {
            clubId = getArguments().getString("club_id");
        } else {
            utils.showSimpleAlertDialog(context, "Alert!!!", "Error in retrieving information!");
        }

        viewModel = new ViewModelProvider(this).get(ClubEventViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_club_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_club_event);
        swipeRefreshLayout.setOnRefreshListener(this::updateData);

        if (!refreshed) {
            swipeRefreshLayout.setRefreshing(true);
            updateData();
        }

        addFab = view.findViewById(R.id.add_club_event_fab);
        addFab.hide();

        emptyView = view.findViewById(R.id.club_events_empty_view);
        recyclerView = view.findViewById(R.id.recycler_club_events);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        ViewTreeObserver observer = recyclerView.getViewTreeObserver();
        postponeEnterTransition();
        observer.addOnGlobalLayoutListener(this::startPostponedEnterTransition);

        adapter = new ClubEventRecyclerAdapter(context, porItem);
        recyclerView.setAdapter(adapter);

        populateRecycler();

        if (porItem == null) {
            addFab.hide();
        } else {
            addFab.show();
            addFab.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), EditClubEventDetailActivity.class);
                intent.putExtra("por", porItem);
                startActivity(intent);
            });
        }

        super.onViewCreated(view, savedInstanceState);
    }

    private void populateRecycler() {

        viewModel.getAllClubEvents(clubId).observe(getViewLifecycleOwner(), clubEventItems -> {
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

        String token = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(USER_TOKEN, "0");

        EventsRoutes service = RetrofitClientInstance.getRetrofitInstance().create(EventsRoutes.class);

        Call<ClubEventItem.ClubEventSuper> call = service.getEventsByClub(token, clubId);
        call.enqueue(new Callback<ClubEventItem.ClubEventSuper>() {
            @Override
            public void onResponse(@NonNull Call<ClubEventItem.ClubEventSuper> call, @NonNull Response<ClubEventItem.ClubEventSuper> response) {
                swipeRefreshLayout.setRefreshing(false);
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
                    Toast.makeText(getContext(), "Events Updated", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "Failed to get events!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ClubEventItem.ClubEventSuper> call, @NonNull Throwable t) {
                if (t.getMessage() != null)
                    Log.e("failure", t.getMessage());
                Toast.makeText(getContext(), "Event fetch failure!!", Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


}
