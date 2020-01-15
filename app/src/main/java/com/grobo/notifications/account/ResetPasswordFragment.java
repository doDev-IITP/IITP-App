package com.grobo.notifications.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.network.UserRoutes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ResetPasswordFragment extends Fragment {

    private OnResetPasswordInteractionListener mListener;
    private ProgressDialog progressDialog;
    private Context context;
    private String webmail;

    public ResetPasswordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getContext() != null)
            this.context = getContext();

        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        if (getArguments() != null && getArguments().containsKey("data")) {
            this.webmail = getArguments().getString("data");
        } else {
            Toast.makeText(context, "Invalid data !!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText emailInput = view.findViewById(R.id.input_webmail);
        emailInput.setText(webmail);

        Button resetButton = view.findViewById(R.id.button_change_password);
        resetButton.setOnClickListener(v -> {

            EditText codeInput = view.findViewById(R.id.input_reset_code);
            EditText passwordInput = view.findViewById(R.id.input_new_password);
            EditText confirmPassword = view.findViewById(R.id.input_confirm_password);

            if (codeInput.getText().toString().isEmpty()) {
                codeInput.setError("Enter a valid code");
            } else if (passwordInput.getText().toString().isEmpty()) {
                codeInput.setError("Enter a valid password");
            } else if (!confirmPassword.getText().toString().equals(passwordInput.getText().toString())) {
                codeInput.setError("Passwords do not match!");
            } else {
                sendHelp(Integer.parseInt(codeInput.getText().toString()), passwordInput.getText().toString());
            }
        });
    }

    private void sendHelp(int code, String password) {

        progressDialog.setMessage("Processing...");
        progressDialog.show();

        Map<String, Object> data = new HashMap<>();
        data.put("email", webmail);
        data.put("code", code);
        data.put("password", password);
        data.put("confirmPassword", password);

        RequestBody body = RequestBody.create((new JSONObject(data)).toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));

        UserRoutes service = RetrofitClientInstance.getRetrofitInstance().create(UserRoutes.class);
        Call<ResponseBody> call = service.resetPassword(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (progressDialog.isShowing()) progressDialog.dismiss();

                if (response.body() != null) {
                    try {
                        JSONObject o = new JSONObject(response.body().string());
                        String message = o.getString("message");
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Unhandled Error", Toast.LENGTH_LONG).show();
                    }
                }

                if (response.code() == 200) {
                    new AlertDialog.Builder(context)
                            .setTitle("Password reset successful")
                            .setMessage("Please sign in with your new password.")
                            .setPositiveButton("OK", (dialog, which) -> {
                                if (dialog != null) dialog.dismiss();
                                mListener.onPasswordResetComplete();
                            }).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (t.getMessage() != null) Log.e("failure", t.getMessage());
                if (progressDialog.isShowing()) progressDialog.dismiss();

                Toast.makeText(context, "Failed, please try again !!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnResetPasswordInteractionListener) {
            mListener = (OnResetPasswordInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnResetPasswordInteractionListener {
        void onPasswordResetComplete();
    }
}
