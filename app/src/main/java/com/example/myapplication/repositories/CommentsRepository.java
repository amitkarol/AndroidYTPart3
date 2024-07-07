package com.example.myapplication.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.Daos.CommentDao;
import com.example.myapplication.db.AppDB;
import com.example.myapplication.entities.Comment;

import java.util.List;

public class CommentsRepository {
    private CommentDao dao;
    private static CommentListData commentListData;

    public CommentsRepository() {
        AppDB db = AppDB.getInstance();
        dao = db.CommentDao();
        commentListData = new CommentListData(dao);
    }

    static class CommentListData extends MutableLiveData<List<Comment>> {

        private final CommentDao commentDao;

        public CommentListData(CommentDao commentDao) {
            super();
            this.commentDao = commentDao;
        }

        @Override
        protected void onActive() {
            super.onActive();
            new Thread(() -> {
                List<Comment> comments = commentDao.index();
                postValue(comments);
            }).start();
        }
    }

    public LiveData<List<Comment>> getAll() {
        return commentListData;
    }
}
