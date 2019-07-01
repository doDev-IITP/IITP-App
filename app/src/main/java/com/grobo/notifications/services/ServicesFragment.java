package com.grobo.notifications.services;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.grobo.notifications.R;
import com.grobo.notifications.services.lostandfound.LostAndFoundActivity;
import com.grobo.notifications.services.maintenance.MaintenanceActivity;

public class ServicesFragment extends Fragment implements View.OnClickListener {


    public ServicesFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        CardView maintenance = view.findViewById(R.id.maintenance_cv);
        CardView complaints = view.findViewById(R.id.complaints_cv);
        CardView lostFound = view.findViewById(R.id.lost_found_cv);

        maintenance.setOnClickListener(this);
        complaints.setOnClickListener(this);
        lostFound.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.complaints_cv:
                transactFragment(new ComplaintFragment());
                break;

            case R.id.maintenance_cv:
                startActivity(new Intent(getActivity(), MaintenanceActivity.class));
                break;

            case R.id.lost_found_cv:
                startActivity(new Intent(getActivity(), LostAndFoundActivity.class));
                break;

        }
    }

    private void transactFragment(Fragment frag){
        FragmentTransaction fragmentManager = getActivity().getSupportFragmentManager().beginTransaction();

        fragmentManager.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out)
                .replace(R.id.frame_layout_main, frag, frag.getTag())
                .addToBackStack("later_fragment")
                .commit();
    }
}
