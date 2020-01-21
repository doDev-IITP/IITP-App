package com.grobo.notifications.feed;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;

import com.grobo.notifications.R;
import com.grobo.notifications.network.FeedRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireContext();
        feedViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allFeed = feedViewModel.loadAllFeeds();
        fillViewPager();
        if (!loaded) loadData();

        ImageView mess = view.findViewById(R.id.icon_mess);
        mess.setOnClickListener(v -> NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.nav_mess));
        ImageView notifications = view.findViewById(R.id.icon_notification);
        notifications.setOnClickListener(v -> NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.nav_notification));

    }

    private void fillViewPager() {
        if (getView() != null) {

            ViewPager viewPager = getView().findViewById(R.id.viewpager_home);

            viewPager.setPageTransformer(false, new FeedPagerTransformer(context));

            viewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                @NonNull
                @Override
                public Fragment getItem(int position) {
                    FeedCardFragment fragment = new FeedCardFragment();
                    fragment.bindData(allFeed.get(position).getId());
                    return fragment;
                }

                @Override
                public int getCount() {
                    return allFeed.size();
                }
            });

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
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
            }

            @Override
            public void onFailure(@NonNull Call<FeedItems> call, @NonNull Throwable t) {
                if (t.getMessage() != null)
                    Log.e("failure", t.getMessage());
                Toast.makeText(context, "Update failed!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
