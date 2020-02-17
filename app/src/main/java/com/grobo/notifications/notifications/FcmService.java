package com.grobo.notifications.notifications;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.grobo.notifications.R;
import com.grobo.notifications.database.AppDatabase;
import com.grobo.notifications.utils.utils;

import java.util.Map;

import static com.grobo.notifications.utils.utils.createMainNotificationChannel;

public class FcmService extends FirebaseMessagingService {

    private static final String LOG_TAG = FcmService.class.getSimpleName();
    private int notificationId;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(LOG_TAG, "FROM: " + remoteMessage.getFrom());
        Log.e(LOG_TAG, remoteMessage.getSentTime() + " ");

        notificationId = NotificationId.getID();

        Map<String, String> data = remoteMessage.getData();

        if (data.containsKey("notify") && data.get("notify").equals("1")) {

            String messageBody = data.get("body");
            String messageTitle = data.get("title");
            String messageDescription = data.get("description");
            long time = remoteMessage.getSentTime();
            String link = data.get("link");

            String imageUri = "";
            if (data.containsKey("image_uri")) imageUri = remoteMessage.getData().get("image_uri");

            addToDb(messageTitle, messageBody, messageDescription, imageUri, time, link);

            if (imageUri != null && !imageUri.isEmpty()) {
                Glide.with(this).asBitmap()
                        .load(imageUri)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                sendNotification(messageTitle, messageBody, resource, time);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                sendNotification(messageTitle, messageBody, null, time);
                            }
                        });
            } else {
                sendNotification(messageTitle, messageBody, null, time);
            }
        }
    }


    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("FCM", "Refreshed token: " + token);
        utils.storeFCMToken(this, token);
    }

    private void addToDb(String messageTitle, String messageBody, String messageDescription, String imageUri, long time, String link) {
        NotificationDao notificationDao = AppDatabase.getDatabase(this).notificationDao();

        Notification notification = new Notification();
        notification.setTitle(messageTitle);
        notification.setBody(messageBody);
        if (imageUri != null) notification.setImageUrl(imageUri);
        notification.setDescription(messageDescription);
        notification.setTimeStamp(time);
        notification.setLink(link);

        notificationDao.insertNotification(notification);
    }

    private void sendNotification(String title, String body, Bitmap image, long time) {
        createMainNotificationChannel(getApplicationContext());

        String link = getResources().getString(R.string.iitp_web) + "notification/" + time;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(getPackageName());
        intent.setData(Uri.parse(link));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.baseline_dashboard_24)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_EVENT);

        if (image == null) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
        } else {
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));
            notificationBuilder.setLargeIcon(image);
        }

        Log.e("notificationid", String.valueOf(notificationId));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notificationBuilder.build());

    }
}
