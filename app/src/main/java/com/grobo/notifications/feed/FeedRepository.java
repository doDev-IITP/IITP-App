package com.grobo.notifications.feed;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.grobo.notifications.database.AppDatabase;

import java.util.List;

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

    FeedItem getFeedById(String id) {
        loadFeedById task = new loadFeedById(feedDao);
        try {
            return task.execute(id).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    long getMaxEventId() {
        maxEventIdTask task = new maxEventIdTask(feedDao);
        try {
            return task.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    int getFeedCount(String id) {
        feedCountTask task = new feedCountTask(feedDao);
        try {
            return task.execute(id).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
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

    private static class loadFeedById extends AsyncTask<String, Void, FeedItem> {
        private FeedDao mAsyncTaskDao;
        loadFeedById(FeedDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected FeedItem doInBackground(String... params) {
            return mAsyncTaskDao.loadFeedById(params[0]);
        }
    }

    private static class maxEventIdTask extends AsyncTask<Void, Void, Long> {
        private FeedDao mAsyncTaskDao;
        maxEventIdTask(FeedDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Long doInBackground(Void... params) {
            return mAsyncTaskDao.getMaxEventId();
        }
    }

    private static class feedCountTask extends AsyncTask<String, Void, Integer> {
        private FeedDao mAsyncTaskDao;
        feedCountTask(FeedDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Integer doInBackground(String... params) {
            return mAsyncTaskDao.feedCount(params[0]);
        }
    }
}
