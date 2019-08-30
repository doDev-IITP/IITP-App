package com.grobo.notifications.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.grobo.notifications.Mess.MessFragment;
import com.grobo.notifications.R;
import com.grobo.notifications.notifications.NotificationsFragment;

public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


//        View qrFragment = rootView.findViewById(R.id.home_fr_mess_qr);
//
//        qrFragment.setOnClickListener(v -> {
//            final QRFragment frag = new QRFragment();
//            transactFragment(frag);
//            new CountDownTimer(210, 100) {
//                @Override
//                public void onTick(long l) {
//                }
//
//                @Override
//                public void onFinish() {
//                    frag.change(true);
//                }
//            }.start();
//
//
//        });

        BottomNavigationView bottomNavigationView = rootView.findViewById(R.id.bottom_nav_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();

            switch (id){
                case R.id.navigation_today:
                    transactFragment(new TodayFragment());
                    return true;
                case R.id.navigation_mess:
                    transactFragment(new MessFragment());
                    return true;
                case R.id.navigation_notifications:
                    transactFragment(new NotificationsFragment());
                    return true;
            }

            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_today);

        return rootView;
    }

    private void transactFragment(Fragment frag) {
        FragmentTransaction fragmentManager = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.frame_layout_home, frag)
                .commit();
    }

//    TODO: improve grid layout params

}
