package com.grobo.notifications.todolist;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.grobo.notifications.database.AppDatabase;

import java.util.List;

public class TodoViewModel extends AndroidViewModel {

    private TodoDao todoDao;

    public TodoViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getDatabase(application);
        todoDao = db.todoDao();
    }

    public List<Goal> getAllTodo() {

        try {
            return new getAsyncTask(todoDao).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Goal goal) {
        new insertAsyncTask(todoDao).execute(goal);
    }

    public void deleteById (int id) {
        new deleteAsyncTask(todoDao).execute(id);
    }

    private static class getAsyncTask extends AsyncTask<Void, Void, List<Goal>> {

        private TodoDao mAsyncTaskDao;
        getAsyncTask(TodoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<Goal> doInBackground(final Void... params) {
            return mAsyncTaskDao.loadAllTodo();
        }
    }

    private static class insertAsyncTask extends android.os.AsyncTask<Goal, Void, Void> {

        private TodoDao mAsyncTaskDao;
        insertAsyncTask(TodoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Goal... params) {
            mAsyncTaskDao.insertTodo(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Integer, Void, Void> {

        private TodoDao mAsyncTaskDao;

        deleteAsyncTask(TodoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            mAsyncTaskDao.deleteTodoById(params[0]);
            return null;
        }
    }


}
