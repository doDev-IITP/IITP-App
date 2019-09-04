package com.grobo.notifications.todolist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;
import com.grobo.notifications.utils.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TodoFragment extends Fragment implements TodoRecyclerAdapter.OnTodoInteractionListener { //implements OnOutsideClickedListener, BatListener {

    public TodoFragment() {
    }

    private TodoViewModel viewModel;
    private TodoRecyclerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        String a = utils.getDeviceInfo();
        Log.e(getClass().getSimpleName(), a);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM YYYY", Locale.getDefault());
        requireActivity().setTitle(dateFormat.format(date));

        RecyclerView recyclerView = view.findViewById(R.id.recycler_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new TodoRecyclerAdapter(requireContext(), this);
        recyclerView.setAdapter(adapter);

        populateRecycler();

        FloatingActionButton fab = view.findViewById(R.id.fab_add_todo);
        fab.setOnClickListener(v -> {
            DialogFragment dialogFrag = DialogFragment.newInstance();
            dialogFrag.show(requireActivity().getSupportFragmentManager(), dialogFrag.getTag());
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void populateRecycler() {
        viewModel.getAllTodo().observe(TodoFragment.this, goals -> {
            adapter.setItemList(goals);
        });
    }

    @Override
    public void onTodoSelected(Goal Goal) {

    }

    @Override
    public void onTodoDeleted(Goal goal) {

    }

    @Override
    public void onTodoChecked(Goal goal, boolean b) {

    }
}
