package com.example.myapplication.entities;

import android.os.Build;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.List;

public class VideoManager {
    private static VideoManager instance;
    private List<Video> videoList;

    private VideoManager() {
        videoList = new ArrayList<Video>();
        initializeSampleVideos();
    }

    public static synchronized VideoManager getInstance() {
        if (instance == null) {
            instance = new VideoManager();
        }
        return instance;
    }

    private void initializeSampleVideos() {
        UserManager userManager = UserManager.getInstance();

        String maayanEmail = "maayan@gmail.com";
        String idanEmail = "idan@gmail.com";
        String hemiEmail = "hemi@gmail.com";
        String amitEmail = "amit@gmail.com";

        int[] videoResources = {R.raw.video1, R.raw.video2, R.raw.video3, R.raw.policesnail, R.raw.newrules,
                R.raw.disney, R.raw.lionking, R.raw.basketballplayer, R.raw.london, R.raw.eras};

        String[] videoUrls = new String[videoResources.length];
        for (int i = 0; i < videoResources.length; i++) {
            videoUrls[i] = "android.resource://" + "com.example.myapplication" + "/" + videoResources[i];
        }

        int[] photoResources = {R.drawable.dior, R.drawable.snailicecream, R.drawable.paris, R.drawable.policesnail,
                R.drawable.newrules, R.drawable.disney, R.drawable.lion, R.drawable.basketball,
                R.drawable.london, R.drawable.eras};

        String[] photoUrls = new String[photoResources.length];
        for (int i = 0; i < photoResources.length; i++) {
            photoUrls[i] = "android.resource://" + "com.example.myapplication" + "/" + photoResources[i];
        }

        String[] titles = {"Dior gallery - Paris", "Mr. Snail - Ice cream Guy", "Paris - breakfast at Carette",
                "Mr. snail - police officer", "New rules - Dua Lipa", "Disneyland",
                "Lions king Musical London", "Mr. Snail basketball Player", "Leaving London",
                "You Need To Calm Down - The Eras Tour"};

        String[] descriptions = {"So beautiful", "Mr. Snail's ice cream is the BEST!", "Paris was delicious",
                "Mr. snail is the BEST police officer", "Best Clip Ever", "Disney was magical",
                "Hakuna Matata!", "Mr. Snail is the BEST basketball Player", "So sad to leave london",
                "This night was sparkling"};

        String[] owners = {maayanEmail, hemiEmail, maayanEmail, hemiEmail, amitEmail, amitEmail, amitEmail, hemiEmail, maayanEmail, maayanEmail};

        int[] views = {1000, 1213, 12323, 2324, 2324, 242, 242, 224, 23132, 2434};
        int[] likes = {3, 23, 23, 232, 343, 242, 4423, 244, 12, 2342};

        for (int i = 0; i < titles.length; i++) {
            Video newVideo = new Video(titles[i], descriptions[i], photoUrls[i],
                    photoResources[i], videoUrls[i], owners[i], views[i], likes[i]);
            videoList.add(newVideo);

            User owner = userManager.getUserByEmail(owners[i]);
            if (owner != null) {
                owner.getVideos().add(newVideo);
                // Adding comments to each video
                Comment comment = new Comment(owner.getEmail(), owner.getDisplayName(), "Great video!", owner.getPhotoUri());
                newVideo.addComment(comment);
            }
        }
    }

    public List<Video> getVideoList() {
        return new ArrayList<>(videoList);
    }

    public Video getVideoById(int id) {
        for (Video video : videoList) {
            if (video.getId() == id) {
                return video;
            }
        }
        return null;
    }

    public Video getVideoByTitle(String title) {
        for (Video video : videoList) {
            if (video.getTitle().equals(title)) {
                return video;
            }
        }
        return null;
    }

    public void addVideo(Video newVideo) {
        videoList.add(newVideo);
    }

    public void updateVideo(Video updatedVideo, Video originVideo) {
        removeVideo(originVideo);
        videoList.add(updatedVideo);
    }

    public void removeVideo(Video videoToRemove) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            videoList.removeIf(video -> video.getId() == videoToRemove.getId());
        }
    }

    public void clearVideos() {
        videoList.clear();
        initializeSampleVideos();
    }
}
