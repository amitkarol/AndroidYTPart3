package com.example.myapplication.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.myapplication.converters.CommentConverter;
import com.example.myapplication.converters.StringListConverter;
import com.example.myapplication.converters.UserConverter;

@Entity
public class Video implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private String img;
    private int thumbnailResId;
    private String video;
    @TypeConverters(UserConverter.class)
    private String owner;
    private int views;
    private int likes;
    @TypeConverters(StringListConverter.class)
    private List<String> likedBy;
    @TypeConverters(CommentConverter.class)
    private List<Comment> comments;

    @Ignore
    public Video(String title, String description, String img, int thumbnailResId, String video, String owner, int views, int likes) {
        this.title = title;
        this.description = description;
        this.img = img;
        this.thumbnailResId = thumbnailResId;
        this.video = video;
        this.owner = owner;
        this.views = views;
        this.likes = likes;
        this.likedBy = new ArrayList<String>();
        this.comments = new ArrayList<>();
    }

    public Video(String title, String description, String img, String video, String owner) {
        this.title = title;
        this.description = description;
        this.img = img;
        this.thumbnailResId = 0;
        this.video = video;
        this.owner = owner;
        this.views = 0;
        this.likes = 0;
        this.likedBy = new ArrayList<String>();
        this.comments = new ArrayList<>();
    }


    // Copy constructor
    @Ignore
    public Video(Video original) {
        this.id = original.id;
        this.title = original.title;
        this.description = original.description;
        this.img = original.img;
        this.thumbnailResId = original.thumbnailResId;
        this.video = original.video;
        this.owner = original.owner;
        this.likes = original.likes;
        this.views = original.views;
        this.comments = new ArrayList<>(original.comments);
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getThumbnailResId() {
        return thumbnailResId;
    }

    public void setThumbnailResId(int thumbnailResId) {
        this.thumbnailResId = thumbnailResId;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) { // Add this method
        this.owner = owner;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> userLikes) {
        this.likedBy = userLikes;
    }

    public List<Comment> getComments() {
        return comments;
    }
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void incrementViewCount() {
        this.views++;
    }

    public void likeVideo(String username) {
        if (!likedBy.contains(username)) {
            likedBy.add(username);
            this.likes++;
        }
    }

    public void unlikeVideo(String username) {
        if (likedBy.contains(username)) {
            likedBy.add(username);
            this.likes--;
        }
    }

    public boolean hasLiked(String username) {
        return likedBy.contains(username);
    }


    @Override
    public String toString() {
        return "video{" +
                "id='" + id + '\'' +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", thumbnailUrl='" + img + '\'' +
                ", thumbnailResId=" + thumbnailResId +
                ", videoUrl='" + video + '\'' +
                ", user=" + owner + // Add this line
                ", viewCount=" + views +
                ", likeCount=" + likes +
                ", userLikes=" + likedBy +
                ", comments=" + comments +
                '}';
    }
}
