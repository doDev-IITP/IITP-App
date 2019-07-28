package com.grobo.notifications.account;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtpFragment extends Fragment {


    private OnOtpListener callback;

    public OtpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(  R.layout.fragment_otp, container, false );
        FloatingActionButton button=view.findViewById( R.id.otp_next );
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.OnOtpCorrect( 1 );
            }
        } );
        return view;
    }
    public interface OnOtpListener{
        void OnOtpCorrect(int status);
    }

    @Override
    public void onAttach(Context context) {
        callback=(OnOtpListener) getActivity();
        super.onAttach( context );
    }

    @Override
    public void onDestroy() {
        callback=null;
        super.onDestroy();
    }
}
