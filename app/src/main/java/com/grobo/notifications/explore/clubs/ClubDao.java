package com.grobo.notifications.explore.clubs;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ClubDao {

    @Query("select * from clubs ORDER BY followed DESC")
    List<ClubItem> loadAllClubs();

    @Query("select * from clubs where id = :id")
    ClubItem loadClubById(int id);

    @Query("SELECT * FROM clubs WHERE followed = 1 ORDER BY name ASC")
    List<ClubItem> loadGoingEvents();

    @Insert(onConflict = REPLACE)
    void insertClub(ClubItem clubItem);

    @Insert(onConflict = REPLACE)
    void insertOrReplaceFeed(ClubItem... feedList);

}
