package com.grobo.notifications.work;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.grobo.notifications.database.AppDatabase;
import com.grobo.notifications.feed.FeedDao;
import com.grobo.notifications.feed.FeedItem;
import com.grobo.notifications.utils.utils;

public class FeedWorker extends Worker {

    private Context context;

    public FeedWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        return updateFeed();
    }

    private Result updateFeed() {
        final FeedDao feedDao = AppDatabase.getDatabase(getApplicationContext()).feedDao();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        new insertAsyncTask(feedDao, db).execute();

        if (utils.isNetworkConnected(context)) return Result.success();
        return Result.failure();
    }

    private static class insertAsyncTask extends AsyncTask<Void, Void, Void> {

        private FeedDao mAsyncTaskDao;
        private FirebaseFirestore db;
        Query query;

        insertAsyncTask(FeedDao dao, FirebaseFirestore firebaseFirestore) {
            mAsyncTaskDao = dao;
            db = firebaseFirestore;
        }

        @Override
        protected final Void doInBackground(Void... params) {

            FeedItem latest = mAsyncTaskDao.loadFeedById(mAsyncTaskDao.getMaxId());
            if (latest != null) {
                Log.e("maxid", String.valueOf(latest.getId()));
                Log.e("maxid", String.valueOf(latest.getEventId()));

                query = db.collection("feed").whereGreaterThan("timeStamp", latest.getEventId());
            } else {
                query = db.collection("feed");
            }

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshots = task.getResult();
                        for (DocumentSnapshot snapshot : snapshots) {
                            Log.e("snapshots", snapshot.toString());
                            FeedItem item = snapshot.toObject(FeedItem.class);

                            if (!snapshot.contains("delete") && mAsyncTaskDao.feedCount(item.getEventId()) == 0) {
                                mAsyncTaskDao.insertFeed(item);
                            } else {
                                if (snapshot.contains("delete") && snapshot.get("delete").equals(true)){
                                    mAsyncTaskDao.deleteFeedByEventId(item.getEventId());
                                } else {
                                    int id = mAsyncTaskDao.loadFeedByEventId(item.getEventId()).getId();
                                    item.setId(id);
                                    mAsyncTaskDao.insertOrReplaceFeed(item);
                                }
                            }
                        }
                    } else {
                        Log.e("tag", "get failed with ", task.getException());
                    }
                }
            });

            return null;
        }

    }
}
