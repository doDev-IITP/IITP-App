package com.grobo.notifications.admin.clubevents;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.grobo.notifications.database.AppDatabase;

import java.util.List;

public class ClubEventViewModel extends AndroidViewModel {

    private ClubEventDao dao;
    private LiveData<List<ClubEventItem>> allEvents;

    public ClubEventViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.eventDao();

        allEvents = dao.getAllEvents();
    }

    public LiveData<List<ClubEventItem>> getAllEvents() {
        return allEvents;
    }

    LiveData<List<ClubEventItem>> getAllClubEvents(String clubId) {
        return dao.loadEventsByClubId(clubId);
    }

    LiveData<List<ClubEventItem>> getAllDateEvents(long start, long end) {
        return dao.getEventsByDate(start, end);
    }

    public void insert(ClubEventItem clubEventItem) {
        new insertAsyncTask(dao).execute(clubEventItem);
    }

    private static class insertAsyncTask extends AsyncTask<ClubEventItem, Void, Void> {
        private ClubEventDao mAsyncTaskDao;

        insertAsyncTask(ClubEventDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(ClubEventItem... params) {
            mAsyncTaskDao.insertEvents(params[0]);
            return null;
        }
    }

    ClubEventItem getEventById(String id) {
        loadEventByIdTask task = new loadEventByIdTask(dao);
        try {
            return task.execute(id).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class loadEventByIdTask extends AsyncTask<String, Void, ClubEventItem> {
        private ClubEventDao mAsyncTaskDao;

        loadEventByIdTask(ClubEventDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected ClubEventItem doInBackground(String... params) {
            return mAsyncTaskDao.loadEventById(params[0]);
        }
    }

    public void deleteAllEvents() {
        new deleteAsyncTask(dao).execute();
    }

    private static class deleteAsyncTask extends AsyncTask<Void, Void, Void> {
        private ClubEventDao mAsyncTaskDao;

        deleteAsyncTask(ClubEventDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mAsyncTaskDao.deleteAllEvents();
            return null;
        }
    }

    public void deleteEventByClubId(String clubId) {
        new deleteByClubAsyncTask(dao).execute(clubId);
    }

    private static class deleteByClubAsyncTask extends AsyncTask<String, Void, Void> {
        private ClubEventDao mAsyncTaskDao;

        deleteByClubAsyncTask(ClubEventDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(String... params) {
            mAsyncTaskDao.deleteEventByClubId(params[0]);
            return null;
        }
    }

    public void deleteEventById(String id) {
        new deleteByClubAsyncTask(dao).execute(id);
    }

    private static class deleteEventByIdAsyncTask extends AsyncTask<String, Void, Void> {
        private ClubEventDao mAsyncTaskDao;

        deleteEventByIdAsyncTask(ClubEventDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(String... params) {
            mAsyncTaskDao.deleteEventById(params[0]);
            return null;
        }
    }


}
