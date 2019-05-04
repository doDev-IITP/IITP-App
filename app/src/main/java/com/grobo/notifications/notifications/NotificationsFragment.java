package com.grobo.notifications.notifications;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationsFragment extends Fragment implements NotificationsRecyclerAdapter.OnNotificationSelectedListener {


    public NotificationsFragment() {}

    private NotificationViewModel notificationViewModel;
    private NotificationsRecyclerAdapter adapter;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private View emptyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationViewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_notifications, container, false);

        fab = rootView.findViewById(R.id.fab_notification_fragment);
        emptyView = rootView.findViewById(R.id.notification_empty_view);
        recyclerView = rootView.findViewById(R.id.rv_notification_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NotificationsRecyclerAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);

        observeAll();

        fab.setOnClickListener(firstClick());

        return rootView;
    }

    private View.OnClickListener firstClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                observeStarred();
                fab.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_star_border_black_24dp));
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        observeAll();
                        fab.setOnClickListener(firstClick());
                        fab.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_star_black_24dp));
                    }
                });
            }
        };
    }

    private void observeAll(){
        notificationViewModel.loadAllNotifications().removeObservers(NotificationsFragment.this);
        notificationViewModel.loadAllNotifications().observe(NotificationsFragment.this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> notifications) {
                adapter.setNotificationList(notifications);
                if (notifications.size() == 0){
                    recyclerView.setVisibility(View.INVISIBLE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void observeStarred(){
        notificationViewModel.loadAllNotifications().removeObservers(NotificationsFragment.this);
        notificationViewModel.loadAllNotifications().observe(NotificationsFragment.this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> notifications) {
                List<Notification> newList = new ArrayList<>();
                for (Notification n : notifications){
                    if (n.isStarred()) newList.add(n);
                }
                adapter.setNotificationList(newList);
                if (newList.size() == 0){
                    recyclerView.setVisibility(View.INVISIBLE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    @Override
    public void onStarSelected(Notification notification) {
        toggleStar(notification);
    }

    @Override
    public void onNotificationSelected(Notification notification) {
        showDialogueBox(notification);
    }

    private void showDialogueBox(final Notification notification) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setMessage(notification.getDescription());
        builder.setTitle(notification.getTitle());
        builder.setIcon(R.drawable.baseline_notifications_none_24);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        builder.setNeutralButton(notification.isStarred()?"Unstar":"Star", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toggleStar(notification);
            }
        });

        builder.setCancelable(true);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void toggleStar(Notification notification){
        if (notification.isStarred()){
            notification.setStarred(false);
        } else {
            notification.setStarred(true);
        }
        notificationViewModel.insert(notification);
    }

    //TODO: swipe to delete unstarred notification
    //TODO: add option to delete all unstarred notifications

}
