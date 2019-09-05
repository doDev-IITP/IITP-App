package com.grobo.notifications.feed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;
import com.grobo.notifications.feed.addfeed.AddFeedActivity;
import com.grobo.notifications.main.MainActivity;
import com.grobo.notifications.network.FeedRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;

public class FeedFragment extends Fragment {

    public FeedFragment() {
    }

    private FeedViewModel feedViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FeedRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyView;
    private FloatingActionButton addFab;
    private SharedPreferences prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);
        if (getContext() != null)
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null)
            getActivity().setTitle("Feed");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_feed);
        swipeRefreshLayout.setOnRefreshListener(this::updateData);

        if ((System.currentTimeMillis() - prefs.getLong("last_feed_update_time", 0)) >= (5 * 60 * 1000)) {
            swipeRefreshLayout.setRefreshing(true);
            updateData();
        }

        addFab = view.findViewById(R.id.add_feed_fab);

        emptyView = view.findViewById(R.id.feed_empty_view);
        recyclerView = view.findViewById(R.id.rv_feed_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ViewTreeObserver observer = recyclerView.getViewTreeObserver();
        postponeEnterTransition();
        observer.addOnGlobalLayoutListener(this::startPostponedEnterTransition);

        adapter = new FeedRecyclerAdapter(getContext());
        recyclerView.setAdapter(adapter);

        RadioGroup radioGroup = view.findViewById(R.id.radio_group_feed);

        if (getContext() instanceof MainActivity) {

            addFab.hide();

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
                    case R.id.feed_radio_all:
                        addFab.hide();
                        observeAll();
                        break;
                    case R.id.feed_radio_starred:
                        addFab.hide();
                        observeStarred();
                        break;
                    case R.id.feed_radio_my:
                        addFab.show();
                        observeMy(prefs.getString(USER_MONGO_ID, ""));
                        break;
                }
            });
            radioGroup.check(R.id.feed_radio_all);

            addFab.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AddFeedActivity.class);
                startActivity(intent);
            });

        }

        return view;
    }

    private void updateData() {

        long latest = feedViewModel.getMaxEventId();
        if (latest == 0)
            latest = System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000;

        FeedRoutes service = RetrofitClientInstance.getRetrofitInstance().create(FeedRoutes.class);

        Call<FeedItem.FeedItemSuper1> call = service.getNewFeed(latest);
        call.enqueue(new Callback<FeedItem.FeedItemSuper1>() {
            @Override
            public void onResponse(@NonNull Call<FeedItem.FeedItemSuper1> call, @NonNull Response<FeedItem.FeedItemSuper1> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getLatestFeeds() != null) {

                        List<FeedItem> allItems = response.body().getLatestFeeds();

                        for (FeedItem newItem : allItems) {
                            feedViewModel.insert(newItem);
                        }
                    }
                    Toast.makeText(getContext(), "Feeds updated.", Toast.LENGTH_SHORT).show();
                    prefs.edit().putLong("last_feed_update_time", System.currentTimeMillis()).apply();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<FeedItem.FeedItemSuper1> call, @NonNull Throwable t) {
                Log.e("failure", t.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    private void observeAll() {
        feedViewModel.loadAllFeeds().removeObservers(FeedFragment.this);
        feedViewModel.loadAllFeeds().observe(FeedFragment.this, feedItems -> {
            adapter.setFeedItemList(feedItems);
            if (feedItems.size() == 0) {
                recyclerView.setVisibility(View.INVISIBLE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void observeStarred() {
        feedViewModel.loadAllFeeds().removeObservers(FeedFragment.this);
        feedViewModel.loadAllFeeds().observe(FeedFragment.this, feedItems -> {

            List<FeedItem> newList = new ArrayList<>();
            for (FeedItem n : feedItems) {
                if (n.isInterested()) newList.add(n);
            }
            adapter.setFeedItemList(newList);
            if (newList.size() == 0) {
                recyclerView.setVisibility(View.INVISIBLE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void observeMy(final String poster) {
        feedViewModel.loadAllFeeds().removeObservers(FeedFragment.this);
        feedViewModel.loadAllFeeds().observe(FeedFragment.this, feedItems -> {
            List<FeedItem> newList = new ArrayList<>();
            for (FeedItem n : feedItems) {
                if (n.getFeedPoster().getId() != null && n.getFeedPoster().getId().equals(poster))
                    newList.add(n);
            }
            adapter.setFeedItemList(newList);
            if (newList.size() == 0) {
                recyclerView.setVisibility(View.INVISIBLE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.INVISIBLE);
            }
        });
    }

}
