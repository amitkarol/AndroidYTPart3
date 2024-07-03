package com.example.myapplication.entities;

import android.os.Build;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VideoManager {
    private static VideoManager instance;
    private Set<Video> videoSet;

    private VideoManager() {
        videoSet = new HashSet<>();
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

        User maayan = userManager.validateUser("maayan@gmail.com", "Haha1234!");
        User idan = userManager.validateUser("idan@gmail.com", "Blabla1234!");
        User hemi = userManager.validateUser("hemi@gmail.com", "1234Haha!");
        User amit = userManager.validateUser("amit@gmail.com", "amit1234!");

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

        User[] owners = {maayan, hemi, maayan, hemi, amit, amit, amit, hemi, maayan, maayan};

        int[] views = {1000, 1213, 12323, 2324, 2324, 242, 242, 224, 23132, 2434};
        int[] likes = {3, 23, 23, 232, 343, 242, 4423, 244, 12, 2342};

        for (int i = 0; i < titles.length; i++) {
            Video newVideo = new Video(i + 1, titles[i], descriptions[i], photoUrls[i],
                    photoResources[i], videoUrls[i], owners[i], views[i], likes[i]);
            videoSet.add(newVideo);

            owners[i].getVideos().add(newVideo);
            // Adding comments to each video
            Comment comment = new Comment(owners[i], "Great video!", owners[i].getPhotoUri());
            newVideo.addComment(comment);
        }
    }

    public List<Video> getVideoList() {
        return new ArrayList<>(videoSet);
    }

    public Video getVideoById(int id) {
        for (Video video : videoSet) {
            if (video.getId() == id) {
                return video;
            }
        }
        return null;
    }

    public Video getVideoByTitle(String title) {
        for (Video video : videoSet) {
            if (video.getTitle().equals(title)) {
                return video;
            }
        }
        return null;
    }

    public void addVideo(Video newVideo) {
        videoSet.add(newVideo);
    }

    public void updateVideo(Video updatedVideo, Video originVideo) {
        removeVideo(originVideo);
        videoSet.add(updatedVideo);
    }

    public void removeVideo(Video videoToRemove) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            videoSet.removeIf(video -> video.getId() == videoToRemove.getId()); // Ensure removal by ID
        }
    }

    public void clearVideos() {
        videoSet.clear();
        initializeSampleVideos();
    }
}
