package com.grobo.notifications.admin;


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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AddNotificationFragment extends Fragment {


    public AddNotificationFragment() {
    }

    private ProgressDialog dialog;
    private Context context;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_notification, container, false);

        EditText title = view.findViewById(R.id.add_notification_title);
        EditText description = view.findViewById(R.id.add_notification_description);
        EditText image = view.findViewById(R.id.add_notification_image_uri);
        EditText body = view.findViewById(R.id.add_notification_body);
        Button sendButton = view.findViewById(R.id.add_notification_send_button);


        post();

        return view;
    }

    private void post() {

        dialog.setMessage("Sending Notification");
        dialog.show();

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("to", "/topics/dev");

        Map<String, Object> data = new ArrayMap<>();
        data.put("title", "New notification");
        data.put("body", "New notification");
        data.put("description", "New notification");
        data.put("image_uri", "https://sample-videos.com/img/Sample-png-image-1mb.png");
        data.put("notify", "1");

        jsonParams.put("data", data);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());

        final OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .addHeader("Authorization", "key=AAAAHlsC9sw:APA91bEeRILLYmitqLt0DOYGKj6o0Xy9FW_nenzEwkBn4QIi5uMZt3LQqOLA4TsYT2WVcd_Ufn7eyFbeSSw5GPUXWHAjRg3IWYRPAYDn6jVoCKuiIZUlan5F-2SEX2YkMBNdsIBFSDQI")
                .addHeader("content-type", "application/json")
                .build();

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {
                int responseCode = 0;
                try (okhttp3.Response response = client.newCall(request).execute()) {
                    responseCode = response.code();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return responseCode;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (integer == 200) {
                    Toast.makeText(context, "Notification sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Notification send Failed", Toast.LENGTH_SHORT).show();
                }

            }

        }.execute();

    }
}
