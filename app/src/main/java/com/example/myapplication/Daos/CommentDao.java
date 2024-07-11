package com.example.myapplication.Daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.entities.Comment;

import java.util.List;
@Dao
public interface CommentDao {
    @Query("SELECT * FROM Comment")
    List<Comment> index();

    @Query("SELECT * FROM Comment WHERE id = :id")
    Comment get(int id);

    @Insert
    void insert(Comment... Comments);

    @Update
    void update(Comment... Comments);

    @Delete
    void delete(Comment... Comments);

    @Query("DELETE FROM Comment WHERE 1")
    void deleteAll();

    @Query("SELECT * FROM Comment WHERE videoId = :videoId")
    List<Comment> getCommentsByVideoId(String videoId);
}
