package com.grobo.notifications.explore.clubs;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.grobo.notifications.database.AppDatabase;

import java.util.List;

public class ClubViewModel extends AndroidViewModel {

    private ClubDao clubDao;

    public ClubViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getDatabase(application);
        clubDao = db.clubDao();
    }

    public void insert(ClubItem clubItem) {
        new insertAsyncTask(clubDao).execute(clubItem);
    }

    ClubItem getClubById(int id) {
        loadClubById task = new loadClubById(clubDao);
        try {
            return task.execute(id).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    List<ClubItem> getAllClubs(){
        loadAllClubs task = new loadAllClubs(clubDao);

        try {
            return task.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    private static class loadClubById extends AsyncTask<Integer, Void, ClubItem> {

        private ClubDao mAsyncTaskDao;
        loadClubById(ClubDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected ClubItem doInBackground(Integer... params) {
            return mAsyncTaskDao.loadClubById(params[0]);
        }
    }

    private static class loadAllClubs extends AsyncTask<Void, Void, List<ClubItem>> {

        private ClubDao mAsyncTaskDao;
        loadAllClubs(ClubDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<ClubItem> doInBackground(Void... params) {
            return mAsyncTaskDao.loadAllClubs();
        }
    }

}
