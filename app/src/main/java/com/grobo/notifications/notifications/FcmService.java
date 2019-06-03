package com.grobo.notifications.notifications;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.grobo.notifications.R;
import com.grobo.notifications.database.AppDatabase;
import com.grobo.notifications.main.MainActivity;

import java.util.Map;
import java.util.concurrent.Future;

public class FcmService extends FirebaseMessagingService {

    private static final String LOG_TAG = FcmService.class.getName();
    private int notificationId;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e("mylog", "FROM: " + remoteMessage.getFrom());
        Log.d(LOG_TAG, "Message Data: " + remoteMessage.getData());

        notificationId = NotificationId.getID();

        Map<String, String> data = remoteMessage.getData();

        if (data.containsKey("notify")) {
            if (data.get("notify").equals("1")) {

                String imageUri = null;
                String messageBody = remoteMessage.getData().get("body");
                String messageTitle = remoteMessage.getData().get("title");
                String messageDescription = remoteMessage.getData().get("description");

                Bitmap bitmap = null;
                if (data.containsKey("image_uri")) {
                    imageUri = remoteMessage.getData().get("image_uri");
                    Future<Bitmap> futureTarget = Glide.with(this)
                            .asBitmap()
                            .load(imageUri)
                            .error(R.drawable.baseline_dashboard_24)
                            .submit();
                    try {
                        bitmap = futureTarget.get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                addToDb(messageTitle, messageBody, messageDescription, imageUri);
                sendNotification(messageTitle, messageBody, bitmap);
            }
        }

    }

    private void addToDb(String messageTitle, String messageBody, String messageDescription, String imageUri){
        NotificationDao notificationDao = AppDatabase.getDatabase(this).notificationDao();

        Notification notification = new Notification();
        notification.setTitle(messageTitle);
        notification.setBody(messageBody);
        if (imageUri != null) notification.setImageUrl(imageUri);
        notification.setDescription(messageDescription);

        long time = System.currentTimeMillis();
        notification.setTimeStamp(time);

        notificationDao.insertNotification(notification);
    }
    private void sendNotification(String title, String body, Bitmap image) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_EVENT);

        //TODO: set notification tap action to star a notification and start a service to mark notification as starred
        //TODO: open a layout snippet to show content of notification on notification click rather than opening the whole app

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
