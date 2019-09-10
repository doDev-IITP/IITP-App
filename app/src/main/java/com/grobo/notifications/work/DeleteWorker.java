package com.grobo.notifications.work;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.grobo.notifications.database.AppDatabase;
import com.grobo.notifications.feed.FeedDao;

public class DeleteWorker extends Worker {

    public DeleteWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        deleteOldData();

        return Result.success();
    }

    private void deleteOldData() {
        FeedDao feedDao = AppDatabase.getDatabase(getApplicationContext()).feedDao();

        deleteAsyncTask task = new deleteAsyncTask(feedDao);
        task.execute(System.currentTimeMillis() - (10 * 24 * 60 * 60 * 1000));
    }

    private static class deleteAsyncTask extends AsyncTask<Long, Void, Void> {
        private FeedDao mAsyncTaskDao;

        deleteAsyncTask(FeedDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Long... params) {
            mAsyncTaskDao.deleteOldFeed(params[0]);
            return null;
        }
    }
}