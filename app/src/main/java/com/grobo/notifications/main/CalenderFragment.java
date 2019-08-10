package com.grobo.notifications.main;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;
import com.grobo.notifications.admin.clubevents.ClubEventItem;
import com.grobo.notifications.admin.clubevents.ClubEventRecyclerAdapter;
import com.grobo.notifications.network.EventsRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;


public class CalenderFragment extends Fragment {

    private TextView events;
    private RecyclerView eventList;
    private ArrayList<CalendarDay> dates;
    private List<ClubEventItem> allItems;
    private Calendar c;
    private MaterialCalendarView calendarView;
    private ClubEventRecyclerAdapter clubEventRecyclerAdapter;
    private CalendarDay currentDay;

    public CalenderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_calender, container, false );

        c = Calendar.getInstance();
        currentDay = CalendarDay.from( c.get( Calendar.YEAR ), c.get( Calendar.MONTH ) + 1, c.get( Calendar.DATE ) );


        dates = new ArrayList<>();
        allItems = new ArrayList<>();
        events = view.findViewById( R.id.events );
        eventList = view.findViewById( R.id.eventlist );
        eventList.setLayoutManager( new LinearLayoutManager( getContext() ) );
        clubEventRecyclerAdapter = new ClubEventRecyclerAdapter( getContext(), (ClubEventRecyclerAdapter.OnFeedSelectedListener) getActivity() );
        //eventList.addItemDecoration( new DividerItemDecoration( getContext(),DividerItemDecoration.VERTICAL ) );
        eventList.setAdapter( clubEventRecyclerAdapter );
        populateRecycler();
        calendarView = view.findViewById( R.id.calendarView );

        return view;
    }


    private void populateRecycler() {

        String token = PreferenceManager.getDefaultSharedPreferences( getContext() ).getString( USER_TOKEN, "0" );
        Log.e( "token", token );

        EventsRoutes service = RetrofitClientInstance.getRetrofitInstance().create( EventsRoutes.class );

        Call<ClubEventItem.ClubEventSuper> call = service.getAllEvents( token );

        call.enqueue( new Callback<ClubEventItem.ClubEventSuper>() {
            @Override
            public void onResponse(Call<ClubEventItem.ClubEventSuper> call, Response<ClubEventItem.ClubEventSuper> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getEvents() != null) {
                        allItems = response.body().getEvents();


                        for (int i = 0; i < allItems.size(); i++) {
                            c.setTimeInMillis( allItems.get( i ).getDate() );
                            dates.add( CalendarDay.from( c.get( Calendar.YEAR ), c.get( Calendar.MONTH ) + 1, c.get( Calendar.DATE ) ) );
                        }
                        further();
                    }


//                    adapter.setItemList(allItems);
//                    if (allItems.size() == 0) {
//                        recyclerView.setVisibility(View.INVISIBLE);
//                        emptyView.setVisibility(View.VISIBLE);
//                    } else {
//                        recyclerView.setVisibility(View.VISIBLE);
//                        emptyView.setVisibility(View.INVISIBLE);
//                    }
                }
                // swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<ClubEventItem.ClubEventSuper> call, @NonNull Throwable t) {
                Log.e( "failure", t.getMessage() );
                //  swipeRefreshLayout.setRefreshing(false);
            }
        } );


    }

    private void showEvents(CalendarDay date) {

//        if (date.getDay() == 2 && date.getMonth() == 8 && date.getYear() == 2019)
//            events.setText( "No events" );
//        if (date.getDay() == 5 && date.getMonth() == 8 && date.getYear() == 2019) {
//            events.setText( "1 event" );
//        }
        int x = 0;
        ArrayList<ClubEventItem> clubEventItems = new ArrayList<>();
        for (int i = 0; i < allItems.size(); i++) {
            c.setTimeInMillis( allItems.get( i ).getDate() );

            CalendarDay calendarDay = CalendarDay.from( c.get( Calendar.YEAR ), c.get( Calendar.MONTH ) + 1, c.get( Calendar.DATE ) );
            if (calendarDay.equals( date )) {
                clubEventItems.add( allItems.get( i ) );
                x++;

            }
        }
        clubEventRecyclerAdapter.setClubEventItemList( clubEventItems );
        if (x != 0 && x != 1)
            events.setText( x + " Events" );
        else if (x == 1)
            events.setText( "1 Event" );
        else
            events.setText( "No Events" );

    }

    private void further() {


        // final CalendarDay current = CalendarDay.today();


        //calendarView.setDateSelected( calendarView.getCurrentDate(), true );
        calendarView.setSelectionColor( getResources().getColor( R.color.colorPrimary ) );

        // CalendarDay.

        Log.e( "dat", String.valueOf( c.getTime() ) );

        DayViewDecorator e = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {

                if (dates.contains( day )) {
                    return true;

                }
                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan( new DotSpan( 5, R.color.shadow ) );

            }
        };
        DayViewDecorator e2 = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {

                if (day.equals( currentDay )) {
                    return true;

                }
                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setBackgroundDrawable( getResources().getDrawable( R.drawable.calendar_current ) );

            }
        };

        calendarView.setOnDateChangedListener( new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                //Toast.makeText(getContext(), date.toString(), Toast.LENGTH_SHORT).show();
                showEvents( date );
            }
        } );
        // e.shouldDecorate( );
        calendarView.addDecorator( e );
        calendarView.addDecorator( e2 );

    }


}
