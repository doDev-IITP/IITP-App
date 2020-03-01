package com.grobo.notifications.todolist;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.grobo.notifications.R;
import com.grobo.notifications.utils.DatePickerHelper;

public class DialogFragment extends BottomSheetDialogFragment {

    private TodoViewModel viewModel;
    private Context context;

    public static DialogFragment newInstance() {
        return new DialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TodoViewModel.class);

        if (getContext() != null)
            context = getContext();
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(this.requireContext(), this.getTheme());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_todo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        EditText alarm = view.findViewById(R.id.task_alarm);
        EditText title = view.findViewById(R.id.task_title);
        ImageView removeAlarm = view.findViewById(R.id.remove_alarm);
        removeAlarm.setOnClickListener(view1 -> alarm.setText(""));

        Goal goal = new Goal();

        final DatePickerHelper dateHelper = new DatePickerHelper(getContext(), alarm);
        alarm.setOnClickListener(view1 -> {
            dateHelper.getDatePickerDialog().show();
        });

        Button button = view.findViewById(R.id.button_add);
        button.setOnClickListener(v -> {

            if (title.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter name of task !!", Toast.LENGTH_SHORT).show();
                return;
            }

            goal.setTimestamp(System.currentTimeMillis());
            goal.setName(title.getText().toString());

            if (alarm.getText().toString().isEmpty()) {
                goal.setAlarm(0);
            } else {
                goal.setAlarm(dateHelper.getTimeInMillisFromCalender());
                setAlarm(goal.getText(), goal.getAlarm());
            }

            viewModel.insert(goal);

            this.dismiss();

        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void setAlarm(String text, long alarm) {
        Intent notifyIntent = new Intent(context, ReminderBroadcastReceiver.class);
        notifyIntent.putExtra("title", "Reminder");
        notifyIntent.putExtra("body", text);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, (int) alarm, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarm, pendingIntent);
        }
    }
}
