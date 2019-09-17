package com.grobo.notifications.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.transition.TransitionInflater;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.grobo.notifications.Mess.MessFragment;
import com.grobo.notifications.R;
import com.grobo.notifications.notifications.NotificationsFragment;
import com.grobo.notifications.timetable.TimetableFragment;
import com.grobo.notifications.todolist.TodoFragment;

public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    private FragmentManager manager;
    private SharedPreferences preferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = requireActivity().getSupportFragmentManager();
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_nav_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            preferences.edit().putInt("selected_item_id_home", id).apply();

            switch (id){
                case R.id.navigation_today:
                    transactFragment(new TodoFragment());
                    return true;
                case R.id.navigation_mess:
                    transactFragment(new MessFragment());
                    return true;
                case R.id.navigation_notifications:
                    transactFragment(new NotificationsFragment());
                    return true;
                case R.id.navigation_timetable:
                    transactFragment(new TimetableFragment());
                    return true;
            }

            return false;
        });

        bottomNavigationView.setSelectedItemId(preferences.getInt("selected_item_id_home", R.id.navigation_today));

        super.onViewCreated(view, savedInstanceState);
    }

    private void transactFragment(Fragment frag) {
        Fragment current = manager.findFragmentById(R.id.frame_layout_home);
        if (current != null) {
            current.setExitTransition(TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.fade));
            frag.setEnterTransition(TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.fade));
        }

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_layout_home, frag)
                .commit();
    }

}
