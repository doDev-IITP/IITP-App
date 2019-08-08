package com.grobo.notifications.work;

import android.content.Context;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.grobo.notifications.database.AppDatabase;
import com.grobo.notifications.feed.FeedDao;

public class DeleteWorker extends Worker {

    private Context context;

    public DeleteWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        deleteOldData();

        return Result.success();
    }

    private void deleteOldData() {

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        FeedDao feedDao = db.feedDao();
        feedDao.deleteOldFeed(System.currentTimeMillis() - (10*24*60*60*1000));
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong("last_feed_update_time", 0).apply();

    }
}