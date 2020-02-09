package com.grobo.notifications.admin.notify;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.ArrayMap;
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

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ComposeNotificationFragment extends Fragment {

    public ComposeNotificationFragment() {
    }

    private ProgressDialog dialog;
    private Context context;

    private EditText title;
    private EditText description;
    private EditText image;
    private EditText body;
    private EditText reference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.add_notification_title);
        image = view.findViewById(R.id.add_notification_image_uri);
        body = view.findViewById(R.id.add_notification_body);
        description = view.findViewById(R.id.add_notification_description);
        reference = view.findViewById(R.id.add_notification_reference);

        Button sendButton = view.findViewById(R.id.add_notification_send_button);
        sendButton.setOnClickListener(v -> {
            if (validateInput()) showUnsavedChangesDialog();
        });
    }

    private boolean validateInput() {
        boolean valid = true;

        if (title.getText().toString().isEmpty()) {
            title.setError("Please enter valid title");
            valid = false;
        } else title.setError(null);

        if (body.getText().toString().isEmpty()) {
            body.setError("Please enter valid body");
            valid = false;
        } else body.setError(null);

        return valid;
    }

    private void showUnsavedChangesDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Confirmation Dialog")
                .setMessage("Sending this notification... Please confirm!!")
                .setPositiveButton("Send", (dialog, which) -> post())
                .setNegativeButton("Cancel", (dialog, id) -> {
                }).show();
    }

    private void post() {

        dialog.setMessage("Sending Notification");
        dialog.show();

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("to", "/topics/dev");
        jsonParams.put("priority", "high");

        Map<String, Object> data = new ArrayMap<>();
        data.put("title", title.getText().toString());
        data.put("body", body.getText().toString());
        data.put("description", description.getText().toString());
        data.put("image_uri", image.getText().toString());
        data.put("notify", "1");
        data.put("reference", reference.getText().toString());
        jsonParams.put("data", data);

        RequestBody body = RequestBody.create((new JSONObject(jsonParams)).toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .addHeader("Authorization", getResources().getString(R.string.FCM_authorization))
                .addHeader("content-type", "application/json")
                .build();

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {
                try (okhttp3.Response response = client.newCall(request).execute()) {
                    return response.code();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (integer == 200) {
                    Toast.makeText(context, "Notification sent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Notification send Failed", Toast.LENGTH_LONG).show();
                }
            }

        }.execute();

    }
}
