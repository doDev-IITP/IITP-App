package com.grobo.notifications.feed;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.grobo.notifications.database.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class FeedRepository {

    private FeedDao feedDao;
    private LiveData<List<FeedItem>> allFeed;

    FeedRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        feedDao = db.feedDao();
        allFeed = feedDao.loadAllFeed();
    }

    LiveData<List<FeedItem>> loadAllFeed() {
        return allFeed;
    }

    public void insert(FeedItem feedItem) {
        new insertAsyncTask(feedDao).execute(feedItem);
    }

    FeedItem getFeedById(int id) {

        loadFeedById task = new loadFeedById(feedDao);
        try {
            return task.execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class insertAsyncTask extends AsyncTask<FeedItem, Void, Void> {

        private FeedDao mAsyncTaskDao;

        insertAsyncTask(FeedDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final FeedItem... params) {
            mAsyncTaskDao.insertFeed(params[0]);
            return null;
        }
    }

    private static class loadFeedById extends AsyncTask<Integer, Void, FeedItem> {

        private FeedDao mAsyncTaskDao;

        loadFeedById(FeedDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected FeedItem doInBackground(Integer... params) {
            return mAsyncTaskDao.loadFeedById(params[0]);
        }

    }
}
