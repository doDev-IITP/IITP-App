package com.grobo.notifications.feed;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface FeedDao {

    @Query("select * from feed ORDER BY eventId DESC")
    List<FeedItem> loadAllFeed();

    @Query("select id from feed ORDER BY eventId DESC")
    List<String> loadAllFeedIds();

    @Query("select * from feed where eventId = :eventId")
    FeedItem loadFeedByEventId(long eventId);

    @Query("select * from feed where id = :id")
    FeedItem loadFeedById(String id);

    @Insert(onConflict = REPLACE)
    void insertFeed(FeedItem feedItem);

    @Insert(onConflict = REPLACE)
    void insertOrReplaceFeed(FeedItem... feedList);

    @Query("DELETE FROM feed WHERE eventId < :time and interested = 0")
    void deleteOldFeed(long time);

    @Query("DELETE FROM feed WHERE interested = 0")
    void deleteAllFeed();

    @Query("delete from feed where eventId like :eventId")
    void deleteFeedByEventId(long eventId);

    @Query("SELECT COUNT(*) FROM feed where id = :id")
    int feedCount(String id);

    @Query("SELECT MAX(eventId) FROM feed")
    long getMaxEventId();
    
}
