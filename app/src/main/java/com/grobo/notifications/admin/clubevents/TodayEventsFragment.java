package com.grobo.notifications.admin.clubevents;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;
import com.grobo.notifications.network.EventsRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class TodayEventsFragment extends Fragment {

    public TodayEventsFragment() {
    }

//    private SwipeRefreshLayout swipeRefreshLayout;
    private TodayEventsRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyView;

    private ClubEventViewModel viewModel;

    private boolean refreshed = false;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getContext() != null)
            context = getContext();

        viewModel = new ViewModelProvider(this).get(ClubEventViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

//        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_club_event);
//        swipeRefreshLayout.setOnRefreshListener(this::updateData);

//        if (!refreshed) {
//            swipeRefreshLayout.setRefreshing(true);
//            updateData();
//        }

        emptyView = view.findViewById(R.id.club_events_empty_view);
        TextView emptyMessage = emptyView.findViewById(R.id.tv_message);
        emptyMessage.setText("No events today!");

        recyclerView = view.findViewById(R.id.recycler_club_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        ViewTreeObserver observer = recyclerView.getViewTreeObserver();
        postponeEnterTransition();
        observer.addOnGlobalLayoutListener(this::startPostponedEnterTransition);

        adapter = new TodayEventsRecyclerAdapter(context);
        recyclerView.setAdapter(adapter);

        populateRecycler();

        super.onViewCreated(view, savedInstanceState);
    }

    private void populateRecycler() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long start = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, 1);
        long end = calendar.getTimeInMillis();

        viewModel.getAllDateEvents(start, end).observe(getViewLifecycleOwner(), clubEventItems -> {
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

        Call<ClubEventItem.ClubEventSuper> call = service.getEventsByDate(token, 1);
        call.enqueue(new Callback<ClubEventItem.ClubEventSuper>() {
            @Override
            public void onResponse(@NonNull Call<ClubEventItem.ClubEventSuper> call, @NonNull Response<ClubEventItem.ClubEventSuper> response) {
//                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getEvents() != null) {
                        List<ClubEventItem> allItems = response.body().getEvents();

                        if (allItems != null) {
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
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


}
