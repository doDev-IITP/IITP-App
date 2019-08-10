package com.grobo.notifications.admin.clubevents;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;
import com.grobo.notifications.admin.XPortal;
import com.grobo.notifications.feed.addfeed.AddFeedActivity;
import com.grobo.notifications.main.MainActivity;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.network.UserRoutes;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class ClubEventFragment extends Fragment {

    public ClubEventFragment() {
    }

    private SwipeRefreshLayout swipeRefreshLayout;
    private ClubEventRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyView;
    private FloatingActionButton addFab;

    private String clubId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clubId = getArguments().getString("club_id","");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_club_event, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_club_event);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.e("event", "refreshing");
            updateData();
        });
        swipeRefreshLayout.setRefreshing(true);
        updateData();

        addFab = view.findViewById(R.id.add_club_event_fab);
        emptyView = view.findViewById(R.id.club_events_empty_view);
        recyclerView = view.findViewById(R.id.recycler_club_events);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClubEventRecyclerAdapter(getContext(), (ClubEventRecyclerAdapter.OnEventSelectedListener) getActivity());
        recyclerView.setAdapter(adapter);

        if (getContext() instanceof MainActivity) {

            addFab.hide();

        } else if (getContext() instanceof XPortal){

            addFab.show();
            addFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), AddFeedActivity.class);
                    startActivity(intent);
                }
            });

        }

        return view;
    }

    private void updateData() {

        String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(USER_TOKEN, "0");
        Log.e("token", token);

        UserRoutes service = RetrofitClientInstance.getRetrofitInstance().create(UserRoutes.class);

//        Call<ClubEventItem.FeedItemSuper1> call = service.getNewFeed(token, latest);
//        call.enqueue(new Callback<ClubEventItem.FeedItemSuper1>() {
//            @Override
//            public void onResponse(Call<ClubEventItem.FeedItemSuper1> call, Response<ClubEventItem.FeedItemSuper1> response) {
//
//                if (response.isSuccessful()) {
//                    List<ClubEventItem> allItems = response.body().getLatestFeeds();
//
//                    for (ClubEventItem newItem : allItems) {
//                        if (feedViewModel.getFeedCount(newItem.getId()) == 0)
//                            feedViewModel.insert(newItem);
//                        Log.e("feed", newItem.getEventName());
//                    }
//
//                }
//                swipeRefreshLayout.setRefreshing(false);
//            }
//
//            @Override
//            public void onFailure(Call<ClubEventItem.FeedItemSuper1> call, Throwable t) {
//                Log.e("failure", t.getMessage());
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });

        new CountDownTimer(2100, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                swipeRefreshLayout.setRefreshing(false);

            }
        }.start();



    }


}
