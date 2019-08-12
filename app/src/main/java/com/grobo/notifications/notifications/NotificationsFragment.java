package com.grobo.notifications.notifications;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.grobo.notifications.R;
import com.grobo.notifications.utils.OnSwipeTouchListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationsFragment extends Fragment implements NotificationsRecyclerAdapter.OnNotificationSelectedListener {


    public NotificationsFragment() {
    }

    private NotificationViewModel notificationViewModel;
    private NotificationsRecyclerAdapter adapter;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private View emptyView;
    private ConstraintLayout layout;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getActivity() != null)
            getActivity().setTitle( "Notifications" );
        super.onViewCreated( view, savedInstanceState );
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        notificationViewModel = ViewModelProviders.of( this ).get( NotificationViewModel.class );
        //enableSwipeToDeleteAndUndo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate( R.layout.fragment_notifications, container, false );

        fab = rootView.findViewById( R.id.fab_notification_fragment );
        emptyView = rootView.findViewById( R.id.notification_empty_view );
        recyclerView = rootView.findViewById( R.id.rv_notification_fragment );
        recyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );
        layout = rootView.findViewById( R.id.layout );
        adapter = new NotificationsRecyclerAdapter( getContext(), this );
        recyclerView.setAdapter( adapter );
        enableSwipeToDeleteAndUndo();
        observeAll();
        Toast.makeText( getActivity(), "Swipe left to delete a notification", Toast.LENGTH_SHORT ).show();

        fab.setOnClickListener( firstClick() );

        return rootView;
    }

    private View.OnClickListener firstClick() {
        return v -> {
            observeStarred();
            fab.setImageDrawable( getContext().getResources().getDrawable( R.drawable.ic_star_border_black_24dp ) );
            fab.setOnClickListener(v1 -> {
                observeAll();
                fab.setOnClickListener( firstClick() );
                fab.setImageDrawable( getContext().getResources().getDrawable( R.drawable.ic_star_black_24dp ) );
            });
        };
    }

    private void observeAll() {
        notificationViewModel.loadAllNotifications().removeObservers( NotificationsFragment.this );
        notificationViewModel.loadAllNotifications().observe( NotificationsFragment.this, notifications -> {
            adapter.setNotificationList( notifications );
            if (notifications.size() == 0) {
                recyclerView.setVisibility( View.INVISIBLE );
                emptyView.setVisibility( View.VISIBLE );
            } else {
                recyclerView.setVisibility( View.VISIBLE );
                emptyView.setVisibility( View.INVISIBLE );
            }
        });
    }

    private void observeStarred() {
        notificationViewModel.loadAllNotifications().removeObservers( NotificationsFragment.this );
        notificationViewModel.loadAllNotifications().observe( NotificationsFragment.this, notifications -> {
            List<Notification> newList = new ArrayList<>();
            for (Notification n : notifications) {
                if (n.isStarred()) newList.add( n );
            }
            adapter.setNotificationList( newList );
            if (newList.size() == 0) {
                recyclerView.setVisibility( View.INVISIBLE );
                emptyView.setVisibility( View.VISIBLE );
            } else {
                recyclerView.setVisibility( View.VISIBLE );
                emptyView.setVisibility( View.INVISIBLE );
            }
        });
    }


    @Override
    public void onStarSelected(Notification notification) {
        toggleStar( notification );
    }

    @Override
    public void onNotificationSelected(Notification notification) {
        showDialogueBox( notification );
    }

    private void showDialogueBox(final Notification notification) {

        AlertDialog.Builder builder = new AlertDialog.Builder( Objects.requireNonNull( getContext() ) );
        //builder.setMessage( notification.getDescription() );
        //builder.setTitle( notification.getTitle() );
        View custom = getLayoutInflater().inflate(R.layout.custom_title, null);
        TextView title= custom.findViewById( R.id.titlenoti );
        ImageView background= custom.findViewById( R.id. back);

        //TextView body=custom.findViewById( R.id.body );
        TextView description= custom.findViewById( R.id.description );
        title.setText( notification.getTitle() );
       // body.setText( notification.getBody());
        description.setText( notification.getDescription() );
        //custom.setBackground( notification );
        builder.setView( custom );
        //Toast.makeText( getContext(), notification.getImageUrl(), Toast.LENGTH_SHORT ).show();
        if (notification.getImageUrl() == null || notification.getImageUrl().equals( "" ))
            background.setVisibility( View.GONE );
        else {
            background.setVisibility( View.VISIBLE );
            Glide.with( getContext() ).load( notification.getImageUrl() ).into( background );
        }

        builder.setIcon( R.drawable.baseline_notifications_none_24 );
        builder.setPositiveButton( "OK", (dialog, which) -> {
            if (dialog != null) dialog.dismiss();
        });

        builder.setNeutralButton( notification.isStarred() ? "Unstar" : "Star", (dialog, which) -> toggleStar( notification ));

        builder.setCancelable( true );


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void toggleStar(Notification notification) {
        if (notification.isStarred()) {
            notification.setStarred( false );
        } else {
            notification.setStarred( true );
        }
        notificationViewModel.insert( notification );
    }

    private void enableSwipeToDeleteAndUndo() {
        OnSwipeTouchListener swipeToDeleteCallback = new OnSwipeTouchListener( getActivity() ) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final Notification item = adapter.getData().get( position );

                adapter.removeItem( position );
                notificationViewModel.delete( item );


                Snackbar snackbar = Snackbar
                        .make( layout, "Notification deleted", Snackbar.LENGTH_LONG );
                snackbar.setAction( "UNDO", view -> {
                    adapter.restoreItem( item, position );
                    recyclerView.scrollToPosition( position );
                    notificationViewModel.insert( item );
                });

                snackbar.setActionTextColor( Color.YELLOW );
                snackbar.show();

            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

                int pos = viewHolder.getAdapterPosition();
                Notification notification = adapter.getData().get( pos );
                if (notification.isStarred())
                    return 0;
                return makeMovementFlags( 1, ItemTouchHelper.LEFT );
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper( swipeToDeleteCallback );
        itemTouchhelper.attachToRecyclerView( recyclerView );
    }

}
