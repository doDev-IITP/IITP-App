package com.grobo.notifications.clubs;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.grobo.notifications.database.AppDatabase;

import java.util.List;

public class ClubViewModel extends AndroidViewModel {

    private ClubDao clubDao;
    private LiveData<List<ClubItem>> allClubs;

    public ClubViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getDatabase(application);
        clubDao = db.clubDao();
        allClubs = clubDao.loadAllClubs();
    }

    LiveData<List<ClubItem>> getAllClubs() {
        return allClubs;
    }


    public void insert(ClubItem clubItem) {
        new insertAsyncTask(clubDao).execute(clubItem);
    }

    ClubItem getClubById(String id) {
        loadClubById task = new loadClubById(clubDao);
        try {
            return task.execute(id).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete () {
        new deleteAsyncTask(clubDao).execute();
    }

    private static class insertAsyncTask extends AsyncTask<ClubItem, Void, Void> {

        private ClubDao mAsyncTaskDao;
        insertAsyncTask(ClubDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ClubItem... params) {
            mAsyncTaskDao.insertClub(params[0]);
            return null;
        }
    }

    private static class loadClubById extends AsyncTask<String, Void, ClubItem> {

        private ClubDao mAsyncTaskDao;
        loadClubById(ClubDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected ClubItem doInBackground(String... params) {
            return mAsyncTaskDao.loadClubById(params[0]);
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Void, Void, Void> {

        private ClubDao mAsyncTaskDao;

        deleteAsyncTask(ClubDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mAsyncTaskDao.deleteAllClubs();
            return null;
        }
    }

}
