package com.grobo.notifications.main;


import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.grobo.notifications.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;


public class CalenderFragment extends Fragment {

    private TextView events;
    private RecyclerView eventList;

    public CalenderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_calender, container, false );

        events = view.findViewById( R.id.events );
        eventList = view.findViewById( R.id.eventlist );


        final MaterialCalendarView calendarView = view.findViewById( R.id.calendarView );


        calendarView.setOnDateChangedListener( new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                //Toast.makeText(getContext(), date.toString(), Toast.LENGTH_SHORT).show();
                showEvents( date );
            }
        } );
        final CalendarDay date = CalendarDay.today();
        Calendar c=Calendar.getInstance();

        //calendarView.setDateSelected( calendarView.getCurrentDate(), true );
        calendarView.setSelectionColor( getResources().getColor( R.color.colorPrimary ) );
        c.setTimeInMillis( System.currentTimeMillis() +24*60*60*1000);
        final CalendarDay calendarDay=CalendarDay.from( c.get( Calendar.YEAR ),c.get( Calendar.MONTH )+1,c.get( Calendar.DATE ) );
       // CalendarDay.

        Log.e("dat",String.valueOf(c.getTime()));

        DayViewDecorator e = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {

                if (day.equals( date ) || day.equals( calendarDay )) {
                    return true;

                }
                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setBackgroundDrawable( getResources().getDrawable( R.drawable.calendar_current) );
                view.addSpan( new DotSpan( 5, R.color.shadow ) );

            }
        };



       // e.shouldDecorate( );
        calendarView.addDecorator( e );

        return view;
    }

    private void showEvents(CalendarDay date) {

        if (date.getDay() == 2 && date.getMonth() == 8 && date.getYear() == 2019)
            events.setText( "No events" );
        if (date.getDay() == 5 && date.getMonth() == 8 && date.getYear() == 2019) {
            events.setText( "1 event" );
        }

    }


}
