package com.grobo.notifications.account;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.goodiebag.pinview.Pinview;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;

public class OtpFragment extends Fragment {

    private OnOtpEnteredListener callback;
    private int otpValue;

    public OtpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_otp, container, false );
        Pinview pinview = view.findViewById( R.id.otpView );
        pinview.setPinViewEventListener( new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                otpValue = Integer.parseInt( pinview.getValue() );
            }
        } );
        FloatingActionButton button = view.findViewById( R.id.otp_next );
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.OnOtpEntered( 1, otpValue );
            }
        } );

        TextView problem = view.findViewById(R.id.tv_problem);
        problem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.gle/HdTdTK9VnA25FvGy6"));
                startActivity(browserIntent);
            }
        });
        return view;
    }

    public interface OnOtpEnteredListener {
        void OnOtpEntered(int status, int otpValue);
    }

    @Override
    public void onAttach(Context context) {
        callback = (OnOtpEnteredListener) getActivity();
        super.onAttach( context );
    }

    @Override
    public void onDestroy() {
        callback = null;
        super.onDestroy();
    }
}
