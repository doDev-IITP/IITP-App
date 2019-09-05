package com.grobo.notifications.todolist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface TodoDao {

    @Query("SELECT * FROM todo ORDER BY timestamp DESC")
    LiveData<List<Goal>> loadAllTodo();

    @Insert(onConflict = REPLACE)
    void insertTodo(Goal goal);

    @Update
    void updateTodo(Goal goal);

    @Query("DELETE FROM todo WHERE id LIKE :id")
    void deleteTodoById(int id);

    @Delete
    void deleteTodo(Goal goal);

    @Query("SELECT COUNT(*) FROM todo WHERE checked = 0")
    int todoCount();

    @Query("SELECT * FROM todo WHERE name LIKE '%' || :text || '%'")
    LiveData<List<Goal>> getTodoForSearch(String text);

}
