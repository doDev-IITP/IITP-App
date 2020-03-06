package com.grobo.notifications.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;
import com.grobo.notifications.admin.clubevents.ClubEventItem;
import com.grobo.notifications.admin.clubevents.ClubEventRecyclerAdapter;
import com.grobo.notifications.admin.clubevents.ClubEventViewModel;
import com.grobo.notifications.network.EventsRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class CalenderFragment extends Fragment {

    private ArrayList<CalendarDay> dates;
    private List<ClubEventItem> allItems = new ArrayList<>();
    private Calendar calendar;
    private MaterialCalendarView calendarView;
    private ClubEventRecyclerAdapter clubEventRecyclerAdapter;
    private CalendarDay currentDay;
    private CalendarDay selectedDay;

    private ClubEventViewModel viewModel;

    public CalenderFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ClubEventViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calender, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendar = Calendar.getInstance();
        currentDay = CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
        selectedDay = currentDay;

        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setSelectionColor(getResources().getColor(R.color.colorPrimary));

        dates = new ArrayList<>();

        RecyclerView eventsRecycler = view.findViewById(R.id.eventlist);
        eventsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        clubEventRecyclerAdapter = new ClubEventRecyclerAdapter(getContext(), null);
        eventsRecycler.setAdapter(clubEventRecyclerAdapter);

        DayViewDecorator todayDecorator = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.equals(currentDay);
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.calendar_current));
            }
        };
        calendarView.addDecorator(todayDecorator);

        viewModel.getAllEvents().observe(getViewLifecycleOwner(), clubEventItems -> {
            allItems = clubEventItems;
            for (ClubEventItem i : clubEventItems) {
                calendar.setTimeInMillis(i.getDate());
                dates.add(CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE)));
            }
            further();
        });

        updateData();
    }

    private void updateData() {

        String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(USER_TOKEN, "0");
        Log.e("token", token);

        EventsRoutes service = RetrofitClientInstance.getRetrofitInstance().create(EventsRoutes.class);
        Call<ClubEventItem.ClubEventSuper> call = service.getAllEvents(token);

        call.enqueue(new Callback<ClubEventItem.ClubEventSuper>() {
            @Override
            public void onResponse(@NonNull Call<ClubEventItem.ClubEventSuper> call, @NonNull Response<ClubEventItem.ClubEventSuper> response) {
                if (response.isSuccessful())
                    viewModel.deleteAllEvents();
                    if (response.body() != null && response.body().getEvents() != null)
                        for (ClubEventItem i : response.body().getEvents()) viewModel.insert(i);
            }

            @Override
            public void onFailure(@NonNull Call<ClubEventItem.ClubEventSuper> call, @NonNull Throwable t) {
                if (t.getMessage() != null) Log.e("failure", t.getMessage());
            }
        });

    }

    private void further() {

        DayViewDecorator e = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return dates.contains(day);
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new DotSpan(5, R.color.shadow));
            }
        };
        calendarView.addDecorator(e);

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            showEvents(date);
            selectedDay = date;
        });

    }

    private void showEvents(CalendarDay date) {

        int x = 0;
        ArrayList<ClubEventItem> clubEventItems = new ArrayList<>();
        for (ClubEventItem i : allItems) {
            calendar.setTimeInMillis(i.getDate());

            CalendarDay calendarDay = CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
            if (calendarDay.equals(date)) {
                clubEventItems.add(i);
                x++;
            }
        }

        clubEventRecyclerAdapter.setClubEventItemList(clubEventItems);

        if (getView() != null) {
            TextView events = getView().findViewById(R.id.events);
            if (x != 0 && x != 1)
                events.setText(x + " Events");
            else if (x == 1)
                events.setText("1 Event");
            else
                events.setText("No Events");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showEvents(selectedDay);
    }
}
