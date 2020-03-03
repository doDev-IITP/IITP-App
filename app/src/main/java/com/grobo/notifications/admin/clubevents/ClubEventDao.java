package com.grobo.notifications.admin.clubevents;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ClubEventDao {

    @Query("SELECT * FROM events ORDER BY date ASC")
    LiveData<List<ClubEventItem>> getAllEvents();

    @Query("SELECT * FROM events WHERE id = :eventId")
    ClubEventItem loadEventById(String eventId);

    @Query("SELECT * FROM events WHERE club_id = :clubId ORDER BY date ASC")
    LiveData<List<ClubEventItem>> loadEventsByClubId(String clubId);

    @Query("SELECT * FROM events WHERE date >= :start AND date < :end ORDER BY date ASC")
    LiveData<List<ClubEventItem>> getEventsByDate(long start, long end);

    @Insert(onConflict = REPLACE)
    void insertEvents(ClubEventItem clubEventItem);

//    @Query("DELETE FROM feed WHERE eventDate < :time and interested = 0")
//    void deleteOldFeed(long time);

    @Query("SELECT COUNT(*) FROM events WHERE id = :id")
    int eventCount(String id);

    @Query("DELETE FROM events")
    void deleteAllEvents();

    @Query("DELETE FROM events WHERE id LIKE :eventId")
    void deleteEventById(String eventId);

    @Query("DELETE FROM events WHERE club_id LIKE :clubId")
    void deleteEventByClubId(String clubId);
}
