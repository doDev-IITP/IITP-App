package com.grobo.notifications.work;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.grobo.notifications.R;
import com.grobo.notifications.notifications.NotificationId;

import java.io.IOException;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;

import static com.grobo.notifications.utils.utils.createNotificationChannel;

public class MailSyncWorker extends Worker {

    private Context context;

    public MailSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        syncMail(context);
        return Result.success();
    }

    private static void syncMail(Context context) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                syncFunction(context);
                return null;
            }
        }.execute();
    }

    private static void syncFunction(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String receivingHost = "mail.iitp.ac.in";
        String userName = "1801ee03@iitp.ac.in";
        String password = "Iamironman!1055";

        Properties props2 = System.getProperties();
        props2.setProperty("mail.store.protocol", "imaps");
        Session session2 = Session.getDefaultInstance(props2, null);

        Flags seen = new Flags(Flags.Flag.SEEN);

        try {
            Store store = session2.getStore("imaps");
            store.connect(receivingHost, userName, password);

            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            if (folder.hasNewMessages()) {
                Log.e("mailssss", "new mail " + folder.getNewMessageCount());
            }

            int count = folder.getMessageCount();

            Message[] messages = folder.getMessages(count - folder.getNewMessageCount() -9, count);

            Log.e("maillen", "message length  " + messages.length);

            for (int i = messages.length - 1; i >= 0; i--) {
                try {
                    Message message = messages[i];

                        if (!message.getFlags().contains(seen) && message.getReceivedDate().getTime() > System.currentTimeMillis() - 31 * 60 *1000) {
                            sendNotification(context, message.getSubject(), ((Multipart) message.getContent()).getBodyPart(0).getContent().toString());
                        }

                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }
            }

            folder.close(true);
            store.close();

        } catch (MessagingException e) {
            Log.e("mail error", e.toString());
            if (e instanceof AuthenticationFailedException)
                Log.e("login error", "enter correct user data");
        }
    }

    private static void sendNotification(Context context, String title, String body) {

        createNotificationChannel(context);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, context.getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.baseline_dashboard_24)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NotificationId.getID(), notificationBuilder.build());
    }
}