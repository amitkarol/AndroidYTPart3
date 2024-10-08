package com.example.myapplication.Daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.entities.Video;

import java.util.List;
@Dao
public interface VideoDao {
    @Query("SELECT * FROM Video")
    List<Video> index();

    @Query("SELECT * FROM Video WHERE _id = :id")
    Video get(String id);

    @Query("SELECT * FROM Video WHERE Video.owner = :id")
    List<Video> getUserVideos(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Video... Videos);

    @Update
    void update(Video... Videos);

    @Delete
    void delete(Video... Videos);
}

