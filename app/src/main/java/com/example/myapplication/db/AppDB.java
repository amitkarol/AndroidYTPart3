package com.example.myapplication.db;

import static com.example.myapplication.MyApplication.context;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.myapplication.Daos.CommentDao;
import com.example.myapplication.Daos.UserDao;
import com.example.myapplication.Daos.VideoDao;
import com.example.myapplication.converters.StringListConverter;
import com.example.myapplication.entities.Comment;
import com.example.myapplication.entities.User;
import com.example.myapplication.entities.Video;

@Database(entities = {User.class, Video.class, Comment.class}, version = 7)
@TypeConverters(StringListConverter.class)
public abstract class AppDB extends RoomDatabase {
    private static volatile AppDB INSTANCE;

    public abstract UserDao userDao();
    public abstract VideoDao videoDao();
    public abstract CommentDao CommentDao();

    public static AppDB getInstance() {
        if (INSTANCE == null) {
            synchronized (AppDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDB.class, "app_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
