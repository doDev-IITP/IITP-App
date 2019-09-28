package com.grobo.notifications.notifications;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface NotificationDao {

    @Query("select * from notification ORDER BY id DESC")
    LiveData<List<Notification>> loadAllNotifications();

    @Query("select * from notification where id = :id")
    Notification loadNotificationById(int id);

    @Query("select * from notification where timeStamp = :time")
    Notification loadNotificationByTimestamp(long time);

    @Insert(onConflict = REPLACE)
    void insertNotification(Notification notification);

    @Insert(onConflict = IGNORE)
    void insertOrReplaceNotification(Notification... notifications);

    @Query("delete from notification where id like :id")
    void deleteNotificationById(int id);

    @Query("DELETE FROM notification")
    void deleteAll();
}
