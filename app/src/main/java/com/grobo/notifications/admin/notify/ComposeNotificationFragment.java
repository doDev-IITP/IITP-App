package com.grobo.notifications.admin.notify;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.ArrayMap;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.grobo.notifications.R;
import com.grobo.notifications.account.por.PORItem;
import com.grobo.notifications.network.AdminRoutes;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.utils.utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;
import static com.grobo.notifications.utils.utils.createTestNotificationChannel;

public class ComposeNotificationFragment extends Fragment {

    public ComposeNotificationFragment() {
    }

    private PORItem currentPor;

    private ProgressDialog dialog;
    private Context context;

    private EditText title;
    private EditText description;
    private EditText image;
    private EditText body;
    private EditText reference;

    private ArrayList<String> audience;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        if (getArguments() != null && getArguments().containsKey("por"))
            currentPor = getArguments().getParcelable("por");

        if (getArguments() != null && getArguments().containsKey("audience"))
            audience = getArguments().getStringArrayList("audience");

        if (currentPor == null || audience == null || audience.size() == 0)
            utils.showFinishAlertDialog(context, "Alert!!!", "Error in retrieving POR data!");
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

        Button previewButton = view.findViewById(R.id.add_notification_preview_button);
        previewButton.setOnClickListener(v -> {
            if (validateInput()) {
                if (!image.getText().toString().isEmpty()) {
                    dialog.setMessage("Loading image...");
                    dialog.show();

                    Glide.with(this).asBitmap()
                            .load(image.getText().toString())
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    sendTestNotification(title.getText().toString(), body.getText().toString(), resource);
                                    if (dialog != null) dialog.dismiss();
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                    sendTestNotification(title.getText().toString(), body.getText().toString(), null);
                                    if (dialog != null) dialog.dismiss();                                }
                            });
                } else
                    sendTestNotification(title.getText().toString(), body.getText().toString(), null);
            }
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

        dialog.setMessage("Sending Notification...");
        dialog.show();

        String token = PreferenceManager.getDefaultSharedPreferences(context).getString(USER_TOKEN, "");

        Map<String, Object> data = new ArrayMap<>();
        data.put("title", title.getText().toString());
        data.put("body", body.getText().toString());
        data.put("description", description.getText().toString());
        data.put("image_uri", image.getText().toString());
        data.put("link", reference.getText().toString());
        data.put("club", currentPor.getClubId());

        String[] audienceArray = audience.toArray(new String[0]);
        data.put("audience", audienceArray);

        RequestBody body = RequestBody.create((new JSONObject(data)).toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));

        AdminRoutes service = RetrofitClientInstance.getRetrofitInstance().create(AdminRoutes.class);
        Call<ResponseBody> call = service.postNotification(token, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (dialog != null) dialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Notification Sent", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) getActivity().finish();
                } else {
                    Log.e("failure", String.valueOf(response.code()));
                    utils.showSimpleAlertDialog(context, "Alert!!!", "Notification send failed! Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (t.getMessage() != null) Log.e("failure", t.getMessage());
                if (dialog != null) dialog.dismiss();

                Toast.makeText(context, "Notification send failed, please check internet connection", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void sendTestNotification(String title, String body, Bitmap image) {
        createTestNotificationChannel(context.getApplicationContext());

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, getString(R.string.test_notification_channel_id))
                .setSmallIcon(R.drawable.baseline_dashboard_24)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_EVENT);

        if (image == null) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
        } else {
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));
            notificationBuilder.setLargeIcon(image);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(5325, notificationBuilder.build());

    }
}
