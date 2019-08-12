package com.grobo.notifications.main;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.grobo.notifications.Mess.QRFragment;
import com.grobo.notifications.R;

import java.util.Calendar;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private int dayOfWeek;

    public HomeFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("IITP App");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        Calendar calendar = Calendar.getInstance();
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        View qrFragment = rootView.findViewById(R.id.home_fr_mess_qr);

        qrFragment.setOnClickListener(v -> {
            final QRFragment frag = new QRFragment();
            transactFragment(frag);
            new CountDownTimer(210, 100) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    frag.change(true);
                }
            }.start();


        });

        return rootView;
    }

    private void transactFragment(Fragment frag) {
        FragmentTransaction fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        fragmentManager.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out)
                .replace(R.id.frame_layout_main, frag)
                .addToBackStack("later_fragment")
                .commit();
    }

//    TODO: improve grid layout params

}
