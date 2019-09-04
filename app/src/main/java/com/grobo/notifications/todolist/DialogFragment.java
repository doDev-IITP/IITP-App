package com.grobo.notifications.todolist;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.grobo.notifications.R;

public class DialogFragment extends BottomSheetDialogFragment {

    private TodoViewModel viewModel;

    public static DialogFragment newInstance() {
        return new DialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
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

        TextView alarm = view.findViewById(R.id.task_alarm);
        EditText title = view.findViewById(R.id.task_title);

        Goal goal = new Goal();

        Button button = view.findViewById(R.id.button_add);
        button.setOnClickListener(v -> {

            if (title.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter name of task !!", Toast.LENGTH_SHORT).show();
                return;
            }
            goal.setChecked(false);
            goal.setTimestamp(System.currentTimeMillis());
            goal.setName(title.getText().toString());

            viewModel.insert(goal);

            this.dismiss();

        });

        super.onViewCreated(view, savedInstanceState);
    }
}
