package com.grobo.notifications.account;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;

public class LoginFragment extends Fragment {

    private OnSignInInteractionListener callback;

    private EditText emailInput;
    private EditText passwordInput;

    private String email;
    private String password;

    public LoginFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        emailInput = view.findViewById(R.id.login_input_webmail);
        passwordInput = view.findViewById(R.id.login_input_password);

        Button loginButton = view.findViewById(R.id.login_login_button);
        loginButton.setOnClickListener(v -> {
            if (!validateInput()) {
                Toast.makeText(getContext(), "Please check input fields!", Toast.LENGTH_LONG).show();
            } else {
                callback.onLoginSelected(email.trim(), password.trim());
            }
        });

        TextView forgotPassword = view.findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(v -> {
            callback.onForgotPassword();
        });

        TextView privacyPolicy = view.findViewById(R.id.tv_privacy_policy);
        privacyPolicy.setOnClickListener(view1 -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://timetable-grobo.firebaseapp.com/privacy_policy.html"));
            startActivity(browserIntent);
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private boolean validateInput() {
        boolean valid = true;

        email = emailInput.getText().toString().trim();
        password = passwordInput.getText().toString();

        if (email.isEmpty() || !email.contains("@iitp.ac.in")) {
            emailInput.setError("Please enter a valid email");
            valid = false;
        } else {
            emailInput.setError(null);
        }

        if (password.isEmpty()) {
            passwordInput.setError("Please enter a password");
            valid = false;
        } else {
            passwordInput.setError(null);
        }
        return valid;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSignInInteractionListener) {
            callback = (OnSignInInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public interface OnSignInInteractionListener {
        void onLoginSelected(String email, String password);
        void onForgotPassword();
    }
}
