package com.grobo.notifications.feed;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;

import java.util.List;

public class FeedFragment extends Fragment {


    public FeedFragment() {
    }

    private FeedViewModel feedViewModel;
    private FeedRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        emptyView = view.findViewById(R.id.feed_empty_view);
        recyclerView = view.findViewById(R.id.rv_feed_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FeedRecyclerAdapter(getContext(), (FeedRecyclerAdapter.OnFeedSelectedListener) getActivity());
        recyclerView.setAdapter(adapter);

        observeAll();

        return view;
    }

    private void observeAll() {
        feedViewModel.loadAllFeeds().observe(FeedFragment.this, new Observer<List<FeedItem>>() {
            @Override
            public void onChanged(List<FeedItem> feedItems) {
                adapter.setFeedItemList(feedItems);
                if (feedItems.size() == 0) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
