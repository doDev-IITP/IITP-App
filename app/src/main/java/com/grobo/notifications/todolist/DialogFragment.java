package com.grobo.notifications.todolist;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.grobo.notifications.R;

public class DialogFragment extends AAH_FabulousFragment {

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
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.dialog_add_todo, null);
        RelativeLayout main = contentView.findViewById(R.id.root_view_add_todo);

        TextView alarm = contentView.findViewById(R.id.task_alarm);
        EditText title = contentView.findViewById(R.id.task_title);

        Goal goal = new Goal();

        Button button = contentView.findViewById(R.id.button_add);
        button.setOnClickListener(v -> {

            if (title.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter name of task !!", Toast.LENGTH_SHORT).show();
                return;
            }
            goal.setChecked(false);
            goal.setTimestamp(System.currentTimeMillis());
            goal.setName(title.getText().toString());

            viewModel.insert(goal);

            closeFilter("closed");
        });

        setAnimationDuration(500); //optional; default 500ms
        setPeekHeight(300); // optional; default 400dp
//        setCallbacks((Callbacks) getActivity()); //optional; to get back result
//        setAnimationListener((AAH_FabulousFragment.AnimationListener) getActivity()); //optional; to get animation callbacks
//        setViewgroupStatic(button); // optional; layout to stick at bottom on slide
//        setViewPager(vp_types); //optional; if you use viewpager that has scrollview
        setViewMain(main); //necessary; main bottomsheet view
        setMainContentView(contentView); // necessary; call at end before super
        super.setupDialog(dialog, style); //call super at last
    }

}
