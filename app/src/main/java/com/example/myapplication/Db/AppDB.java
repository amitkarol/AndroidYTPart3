package com.example.myapplication.Db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.myapplication.entities.Comment;
import com.example.myapplication.entities.User;
import com.example.myapplication.entities.Video;

// Define entities and database version
@Database(entities = {User.class, Video.class, Comment.class}, version = 1)
public abstract class AppDB extends RoomDatabase {
    // Define the DAO that works with the database
    public abstract User userDao();
    public abstract Video videoDao();
    public abstract Comment CommentDao();

}
