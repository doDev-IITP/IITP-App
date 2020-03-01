package com.grobo.notifications.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickerHelper {

    private final Calendar calendar = Calendar.getInstance();
    private final String dateFormat = "dd MMM YYYY, hh:mm a";
    private final Context context;
    private EditText editText;

    public DatePickerHelper(Context context, EditText editText) {
        this.context = context;
        this.editText = editText;
    }

    public DatePickerDialog getDatePickerDialog() {
        return new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            getTimePickerDialog().show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private TimePickerDialog getTimePickerDialog() {
        return new TimePickerDialog(context, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            DateFormat format = SimpleDateFormat.getDateTimeInstance();
            editText.setText(format.format(calendar.getTime()));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
    }

    public long getTimeInMillisFromCalender(){
        return calendar.getTimeInMillis();
    }
}

