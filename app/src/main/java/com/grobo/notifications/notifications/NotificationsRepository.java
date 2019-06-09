package com.grobo.notifications.notifications;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.grobo.notifications.database.AppDatabase;

import java.util.List;

public class NotificationsRepository {

    private NotificationDao mNotificationDao;
    private LiveData<List<Notification>> mAllNotifications;

    NotificationsRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mNotificationDao = db.notificationDao();
        mAllNotifications = mNotificationDao.loadAllNotifications();
    }

    LiveData<List<Notification>> loadAllNotifications() {
        return mAllNotifications;
    }

    public void insert (Notification notification) {
        new insertAsyncTask(mNotificationDao).execute(notification);
    }
    public void delete (Notification notification) {
        new deleteAsyncTask(mNotificationDao).execute(notification);
    }

    private static class insertAsyncTask extends AsyncTask<Notification, Void, Void> {

        private NotificationDao mAsyncTaskDao;

        insertAsyncTask(NotificationDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Notification... params) {
            mAsyncTaskDao.insertNotification(params[0]);
            return null;
        }
    }
    private static class deleteAsyncTask extends AsyncTask<Notification, Void, Void> {

        private NotificationDao mAsyncTaskDao;

        deleteAsyncTask(NotificationDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Notification... params) {
            mAsyncTaskDao.deleteNotificationById(params[0].id);
            return null;
        }
    }
}
