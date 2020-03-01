package com.grobo.notifications.todolist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;
import com.grobo.notifications.utils.OnTodoSwipe;

import java.util.ArrayList;
import java.util.List;

public class TodoFragment extends Fragment implements TodoRecyclerAdapter.OnTodoInteractionListener {

    public TodoFragment() {
    }

    private TodoViewModel viewModel;
    private TodoRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyView;
    private SharedPreferences preferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        viewModel = new ViewModelProvider(this).get( TodoViewModel.class );
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_todo, container, false );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        emptyView = view.findViewById(R.id.feed_empty_view);

        FloatingActionButton fab = view.findViewById( R.id.fab_add_todo );
        fab.setOnClickListener( v -> {
            DialogFragment dialogFrag = DialogFragment.newInstance();
            dialogFrag.show( requireActivity().getSupportFragmentManager(), dialogFrag.getTag() );
        } );

        new Handler().postDelayed(() -> {
            fab.animate().scaleX(1).scaleY(1).setDuration(200).start();
        }, 600);

        recyclerView = view.findViewById( R.id.recycler_todo );
        recyclerView.setLayoutManager( new LinearLayoutManager( requireContext() ) );

        adapter = new TodoRecyclerAdapter( requireContext(), this );
        recyclerView.setAdapter( adapter );

        if (preferences.getBoolean( "first_open_todo", true)) {
            insertInitialItems();
        }

        populateRecycler();
        enableSwipeToDeleteAndUndo();

        super.onViewCreated( view, savedInstanceState );
    }

    private void populateRecycler() {
        viewModel.getAllTodo().observe( getViewLifecycleOwner(), goals -> {
            List<Goal> modGoals = new ArrayList<>();
            for (Goal g : goals)
                if (g.getChecked() == 0)
                    modGoals.add( g );
            for (Goal g : goals)
                if (g.getChecked() != 0)
                    modGoals.add( g );
            new Handler().postDelayed( () -> adapter.setItemList( modGoals ), 50 );

//            void onNewDataArrived(List<News> news) {
//                List<News> oldNews = myAdapter.getItems();
//                DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
//                    @Override
//                    public int getOldListSize() {
//                        return 0;
//                    }
//
//                    @Override
//                    public int getNewListSize() {
//                        return 0;
//                    }
//
//                    @Override
//                    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//                        return false;
//                    }
//                });
//                myAdapter.setNews(news);
//                result.dispatchUpdatesTo(myAdapter);
//            }

            if (modGoals.size() == 0) {
                recyclerView.setVisibility(View.INVISIBLE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.INVISIBLE);
            }
        } );
    }

    private void insertInitialItems() {
        long time = System.currentTimeMillis();

        Goal g4 = new Goal();
        g4.setName( "This is a completed todo, swipe this to delete" );
        g4.setChecked(1);
        g4.setTimestamp(time);

        Goal g3 = new Goal();
        g3.setName( "Click this to mark as done !!" );
        g3.setTimestamp(time + 10);

        Goal g2 = new Goal();
        g2.setName( "This is a TODO" );
        g2.setTimestamp(time + 20);

        Goal g1 = new Goal();
        g1.setName( "Welcome to IITP App" );
        g1.setTimestamp(time + 30);

        viewModel.insert(g4);
        viewModel.insert(g3);
        viewModel.insert(g2);
        viewModel.insert(g1);

        preferences.edit().putBoolean( "first_open_todo", false).apply();
    }

    @Override
    public void onTodoSelected(Goal goal) {
        goal.setChecked( goal.getChecked() == 0 ? 1 : 0 );
        viewModel.update( goal );
    }

    private void enableSwipeToDeleteAndUndo() {
        OnTodoSwipe swipeToDeleteCallback = new OnTodoSwipe( getActivity() ) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                Goal goal = adapter.getGoalAtPosition( position );
                if (goal != null) {
                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    viewModel.deleteById(goal);
                }
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                Goal goal = adapter.getGoalAtPosition( position );
                if (goal != null) {
                    if (goal.getChecked() == 0)
                        return 0;
                    return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                }
                return 0;
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 1.7f;
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper( swipeToDeleteCallback );
        itemTouchhelper.attachToRecyclerView( recyclerView );
    }
}
