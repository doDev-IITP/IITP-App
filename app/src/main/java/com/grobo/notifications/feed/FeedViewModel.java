package com.grobo.notifications.feed;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.grobo.notifications.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FeedViewModel extends AndroidViewModel {

    private FeedDao feedDao;

    public FeedViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        feedDao = db.feedDao();
    }

    public List<FeedItem> loadAllFeeds() {
        LoadAllFeedTask task = new LoadAllFeedTask(feedDao);
        try {
            return task.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<String> loadAllFeedIds() {
        LoadAllFeedIdsTask task = new LoadAllFeedIdsTask(feedDao);
        try {
            return task.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void insert(FeedItem feedItem) {
        new insertAsyncTask(feedDao).execute(feedItem);
    }

    public long getMaxFeedId() {
        try {
            return new maxEventIdTask(feedDao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public FeedItem getFeedById(String id) {
        loadFeedById task = new loadFeedById(feedDao);
        try {
            return task.execute(id).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    public void deleteAllFeeds() {
        new DeleteAllFeedsTask(feedDao).execute();
    }

    private static class LoadAllFeedTask extends AsyncTask<Void, Void, List<FeedItem>> {
        private FeedDao mAsyncTaskDao;

        LoadAllFeedTask(FeedDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<FeedItem> doInBackground(Void... voids) {
            return mAsyncTaskDao.loadAllFeed();
        }
    }

    private static class LoadAllFeedIdsTask extends AsyncTask<Void, Void, List<String>> {
        private FeedDao mAsyncTaskDao;

        LoadAllFeedIdsTask(FeedDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return mAsyncTaskDao.loadAllFeedIds();
        }
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

    private static class DeleteAllFeedsTask extends AsyncTask<Void, Void, Void> {
        private FeedDao mAsyncTaskDao;

        DeleteAllFeedsTask(FeedDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mAsyncTaskDao.deleteAllFeed();
            return null;
        }
    }
}
