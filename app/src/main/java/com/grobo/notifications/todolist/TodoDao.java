package com.grobo.notifications.todolist;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface TodoDao {

    @Query("SELECT * FROM todo ORDER BY dateAdded DESC")
    List<Goal> loadAllTodo();

    @Insert(onConflict = REPLACE)
    void insertTodo(Goal goal);

    @Query("DELETE FROM todo WHERE id LIKE :id")
    void deleteTodoById(int id);

    @Query("SELECT COUNT(*) FROM todo WHERE isChecked = 0")
    int todoCount();

}
