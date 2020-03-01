package com.grobo.notifications.feed;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;

import com.grobo.notifications.R;
import com.grobo.notifications.network.FeedRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.timetable.DayFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    private Context context;
    private FeedViewModel feedViewModel;
    private List<FeedItem> allFeed = new ArrayList<>();
    private boolean loaded = false;
    private ProgressBar progressBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireContext();
        feedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_load_feed);

        allFeed = feedViewModel.loadAllFeeds();
        fillViewPager();
        if (!loaded) {
            if (allFeed.isEmpty()) progressBar.setVisibility(View.VISIBLE);
            loadData();
        }

        ImageView mess = view.findViewById(R.id.icon_mess);
        mess.setOnClickListener(v -> NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.nav_mess));
        ImageView notifications = view.findViewById(R.id.icon_notification);
        notifications.setOnClickListener(v -> NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.nav_notification));
        ImageView todo = view.findViewById(R.id.icon_todo);
        todo.setOnClickListener(v -> NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.nav_todo));
        ImageView addFeed = view.findViewById(R.id.icon_add_feed);
        addFeed.setOnClickListener(v -> NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.nav_add_feed));
    }

    private void fillViewPager() {
        if (getView() != null) {

            ViewPager viewPager = getView().findViewById(R.id.viewpager_home);

            viewPager.setPageTransformer(false, new FeedPagerTransformer(context));

            viewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                @NonNull
                @Override
                public Fragment getItem(int position) {
                    if (position == 0) {
                        return DayFragment.newInstance(position + 2);
                    } else {
                        FeedCardFragment fragment = new FeedCardFragment();
                        fragment.bindData(allFeed.get(position - 1).getId());
                        return fragment;
                    }
                }

                @Override
                public int getCount() {
                    return allFeed.size() + 1;
                }

                @NonNull
                @Override
                public Object instantiateItem(@NonNull ViewGroup container, int position) {
                    if (position == 1 && viewPager.getCurrentItem() == 0) {
                        viewPager.setCurrentItem(1, true);
                    }
                    return super.instantiateItem(container, position);
                }
            });

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 0) {
                        if (getView() != null)
                            getView().findViewById(R.id.bottom_layout).animate().translationY(100).alpha(0).setDuration(300).start();
                    } else {
                        if (getView() != null)
                            getView().findViewById(R.id.bottom_layout).animate().translationY(0).alpha(1).setDuration(300).start();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
    }

    private void loadData() {

        FeedRoutes service = RetrofitClientInstance.getRetrofitInstance().create(FeedRoutes.class);

        Call<FeedItems> call = service.getAllFeeds();
        call.enqueue(new Callback<FeedItems>() {
            @Override
            public void onResponse(@NonNull Call<FeedItems> call, @NonNull Response<FeedItems> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getFeeds() != null) {

                        List<FeedItem> newFeeds = response.body().getFeeds();

                        feedViewModel.deleteAllFeeds();
                        for (FeedItem newItem : newFeeds) feedViewModel.insert(newItem);

                        allFeed = newFeeds;
                        fillViewPager();

                        loaded = true;
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<FeedItems> call, @NonNull Throwable t) {
                if (t.getMessage() != null)
                    Log.e("failure", t.getMessage());
                Toast.makeText(context, "Update failed!!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

}
