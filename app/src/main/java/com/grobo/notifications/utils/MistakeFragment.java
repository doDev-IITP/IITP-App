package com.grobo.notifications.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;

public class MistakeFragment extends Fragment {


    public MistakeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mistake, container, false);

        Button back = view.findViewById(R.id.mistake_back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        TextView mistake=view.findViewById( R.id.temp_tv_mistake );
        mistake.setText( "You landed here by mistake, please go back "+new String(  Character.toChars( 0x1F609 ) ));

        return view;
    }

}
