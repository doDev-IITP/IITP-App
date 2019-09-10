package com.grobo.notifications.todolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.grobo.notifications.R;
import com.grobo.notifications.notifications.Notification;
import com.grobo.notifications.utils.OnSwipeTouchListener;
import com.grobo.notifications.utils.OnTodoSwipe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodoFragment extends Fragment implements TodoRecyclerAdapter.OnTodoInteractionListener {

    public TodoFragment() {
    }

    private TodoViewModel viewModel;
    private TodoRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private SharedPreferences preferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        viewModel = ViewModelProviders.of( this ).get( TodoViewModel.class );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_todo, container, false );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat( "EEE, dd MMM YYYY", Locale.getDefault() );
        requireActivity().setTitle( dateFormat.format( date ) );

        preferences = getActivity().getPreferences( Context.MODE_PRIVATE );
        recyclerView = view.findViewById( R.id.recycler_todo );
        recyclerView.setLayoutManager( new LinearLayoutManager( requireContext() ) );

        adapter = new TodoRecyclerAdapter( requireContext(), this );
        recyclerView.setAdapter( adapter );

        populateRecycler();

        FloatingActionButton fab = view.findViewById( R.id.fab_add_todo );
        fab.setOnClickListener( v -> {
            DialogFragment dialogFrag = DialogFragment.newInstance();
            dialogFrag.show( requireActivity().getSupportFragmentManager(), dialogFrag.getTag() );
        } );
        enableSwipeToDeleteAndUndo();

        super.onViewCreated( view, savedInstanceState );
    }

    private void populateRecycler() {
        viewModel.getAllTodo().observe( TodoFragment.this, goals -> {

            List<Goal> modGoals = new ArrayList<>();
            for (Goal g : goals)
                if (g.getChecked() == 0)
                    modGoals.add( g );
            for (Goal g : goals)
                if (g.getChecked() != 0)
                    modGoals.add( g );
            if (preferences.getInt( "first", 0 ) == 0) {
                Goal goal = new Goal();
                goal.setName( "Welcome to IITP App" );
                goal.setChecked( 1 );
                modGoals.add( goal );
                preferences.edit().putInt( "first", 1 ).apply();
            }
            new Handler().postDelayed( () -> adapter.setItemList( modGoals ), 200 );
        } );
    }

    @Override
    public void onTodoSelected(Goal goal) {
        goal.setChecked( goal.getChecked() == 0 ? 1 : 0 );
        viewModel.update( goal );
    }

    @Override
    public void onTodoDeleted(Goal goal) {

    }

    private void enableSwipeToDeleteAndUndo() {
        OnTodoSwipe swipeToDeleteCallback = new OnTodoSwipe( getActivity() ) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final Goal goal = adapter.goal( position );
                Toast.makeText( getContext(), goal.getName(), Toast.LENGTH_SHORT ).show();

                adapter.removeitem( position );
                viewModel.deleteById( goal );
                // notificationViewModel.delete( item );


//                Snackbar snackbar = Snackbar
//                        .make( layout, "Notification deleted", Snackbar.LENGTH_LONG );
//                snackbar.setAction( "UNDO", view -> {
//                    adapter.restoreItem( item, position );
//                    recyclerView.scrollToPosition( position );
//                    notificationViewModel.insert( item );
//                });

//                snackbar.setActionTextColor( Color.YELLOW );
//                snackbar.show();
                adapter.notifyDataSetChanged();

            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

//                int pos = viewHolder.getAdapterPosition();
//                Notification notification = adapter.get( pos );
//                if (notification.isStarred())
//                    return 0;
                return makeMovementFlags( 1, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT );
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 2.9f;
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper( swipeToDeleteCallback );
        itemTouchhelper.attachToRecyclerView( recyclerView );
    }
}
