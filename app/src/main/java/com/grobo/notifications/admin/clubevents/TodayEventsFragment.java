package com.grobo.notifications.admin.clubevents;

import android.content.Context;
import android.content.SharedPreferences;
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

    private TodayEventsRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyView;

    private ClubEventViewModel viewModel;

    private Context context;
    private SharedPreferences prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getContext() != null)
            context = getContext();

        viewModel = new ViewModelProvider(this).get(ClubEventViewModel.class);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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

        if (prefs.getLong("last_today_event_update", 0) < System.currentTimeMillis() - 60 * 60 * 1000)
            updateData(start, end);
    }

    private void updateData(long start, long end) {

        String token = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(USER_TOKEN, "0");
        EventsRoutes service = RetrofitClientInstance.getRetrofitInstance().create(EventsRoutes.class);

        Call<List<ClubEventItem>> call = service.getEventsByDate(token, start, end);
        call.enqueue(new Callback<List<ClubEventItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<ClubEventItem>> call, @NonNull Response<List<ClubEventItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ClubEventItem> allItems = response.body();
                    for (ClubEventItem item : allItems) viewModel.insert(item);
                    Toast.makeText(context, "Events Updated", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(context, "Failed to get events!", Toast.LENGTH_SHORT).show();
                prefs.edit().putLong("last_today_event_update", System.currentTimeMillis()).apply();
            }

            @Override
            public void onFailure(@NonNull Call<List<ClubEventItem>> call, @NonNull Throwable t) {
                if (t.getMessage() != null)
                    Log.e("failure", t.getMessage());
                Toast.makeText(context, "Event fetch failure!!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
