package com.grobo.notifications.todolist;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.grobo.notifications.database.AppDatabase;

import java.util.List;

public class TodoViewModel extends AndroidViewModel {

    private TodoDao todoDao;
    private LiveData<List<Goal>> todoItems;

    public TodoViewModel(@NonNull Application application) {
        super( application );

        AppDatabase db = AppDatabase.getDatabase( application );
        todoDao = db.todoDao();
        todoItems = todoDao.loadAllTodo();
    }

    public LiveData<List<Goal>> getAllTodo() {
        return todoItems;
    }

    public void update(Goal goal) {
        new updateAsyncTask( todoDao ).execute( goal );
    }

    public void insert(Goal goal) {
        new insertAsyncTask( todoDao ).execute( goal );
    }

    public void deleteById(Goal goal) {
        new deleteAsyncTask( todoDao ).execute( goal );
    }

    private static class updateAsyncTask extends AsyncTask<Goal, Void, Void> {

        private TodoDao mAsyncTaskDao;

        updateAsyncTask(TodoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Goal... params) {
            mAsyncTaskDao.updateTodo( params[0] );
            return null;
        }
    }

    private static class insertAsyncTask extends android.os.AsyncTask<Goal, Void, Void> {

        private TodoDao mAsyncTaskDao;

        insertAsyncTask(TodoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Goal... params) {
            mAsyncTaskDao.insertTodo( params[0] );
            return null;
        }
    }

    private static class deleteAsyncTask extends android.os.AsyncTask<Goal, Void, Void> {

        private TodoDao mAsyncTaskDao;

        deleteAsyncTask(TodoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Goal... params) {
            //  mAsyncTaskDao.deleteTodoById(params[0]);
            mAsyncTaskDao.deleteTodo( params[0] );
            return null;
        }
    }


}
