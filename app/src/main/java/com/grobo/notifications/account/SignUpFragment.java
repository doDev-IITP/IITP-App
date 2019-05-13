package com.grobo.notifications.account;

import android.content.Context;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;

import java.util.Map;

public class SignUpFragment extends Fragment {

    private OnSignUpInteractionListener callback;

    public SignUpFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        Bundle args = getArguments();
        final Map<String, Object> jsonParams = new ArrayMap<>();

        if (args != null) {
            if (args.containsKey("email")) {
                jsonParams.put("email", args.getString("email"));
                Log.e("email", args.getString("email"));
            }
            if (args.containsKey("password")) {
                jsonParams.put("password", args.getString("password"));
                Log.e("passw", args.getString("password"));
            }
        }
        EditText name = view.findViewById(R.id.signup_input_name);
        jsonParams.put("name", name.getText().toString());

        EditText roll = view.findViewById(R.id.signup_input_roll);
        jsonParams.put("instituteId", roll.getText().toString());

        String[] por = {"hello"};
        jsonParams.put("por", por);
        jsonParams.put("isSuperUser", false);

        Button finish = view.findViewById(R.id.signup_finish_button);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onFinishSelected(jsonParams);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSignUpInteractionListener) {
            callback = (OnSignUpInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public interface OnSignUpInteractionListener {
        void onFinishSelected(Map<String, Object> jsonParams);
    }
}
