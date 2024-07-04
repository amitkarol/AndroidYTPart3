package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ThemeUtil;
import com.example.myapplication.UserPage;
import com.example.myapplication.entities.UserManager;
import com.example.myapplication.entities.Video;
import com.example.myapplication.entities.User;
import com.example.myapplication.videowatching;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    private List<Video> videoList;
    private List<Video> filteredVideoList;
    private Context context;
    private User loggedInUser;

    public VideoListAdapter(List<Video> videoList, Context context, User loggedInUser) {
        this.videoList = videoList != null ? videoList : new ArrayList<>();
        this.filteredVideoList = new ArrayList<>(this.videoList);
        this.context = context;
        this.loggedInUser = loggedInUser;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        ImageView userPhotoImageView;
        TextView titleTextView;
        TextView channelTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            userPhotoImageView = itemView.findViewById(R.id.userPhotoImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            channelTextView = itemView.findViewById(R.id.channelTextView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video video = filteredVideoList.get(position);

        holder.titleTextView.setText(video.getTitle());
        holder.channelTextView.setText(video.getOwner());

        // Load the video thumbnail
        if (video.getImg() != null && !video.getImg().isEmpty()) {
            loadImageFromUri(holder.thumbnailImageView, Uri.parse(video.getImg()), R.drawable.dog1);
        } else {
            holder.thumbnailImageView.setImageResource(video.getThumbnailResId());
        }

        // Load the user photo
        UserManager userManager = UserManager.getInstance();
        User owner = userManager.getUserByEmail(video.getOwner());
        if (owner != null && owner.getPhotoUri() != null && !owner.getPhotoUri().isEmpty()) {
            loadImageFromUri(holder.userPhotoImageView, Uri.parse(owner.getPhotoUri()), R.drawable.person);
        } else {
            holder.userPhotoImageView.setImageResource(R.drawable.person);
        }

        // Navigate to video watching activity on item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, videowatching.class);
            intent.putExtra("title", video.getTitle());
            intent.putExtra("user", loggedInUser);
            context.startActivity(intent);
        });

        // Navigate to user page on user photo click
        holder.userPhotoImageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserPage.class);
            intent.putExtra("user", video.getOwner());
            context.startActivity(intent);
        });

        boolean isNightMode = ThemeUtil.isNightMode(context);
        ThemeUtil.changeTextColor(holder.itemView, isNightMode);
        ThemeUtil.changeBackgroundColor(holder.itemView, isNightMode);
    }

    @Override
    public int getItemCount() {
        return filteredVideoList.size();
    }

    public void filter(String query) {
        filteredVideoList.clear();
        if (query.isEmpty()) {
            filteredVideoList.addAll(videoList);
        } else {
            for (Video video : videoList) {
                if (video.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredVideoList.add(video);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void refreshTheme() {
        notifyDataSetChanged();
    }

    public void setVideos(List<Video> newVideoList) {
        this.videoList.clear();
        if (newVideoList != null) {
            this.videoList.addAll(newVideoList);
        }
        filter("");
    }

    private void loadImageFromUri(ImageView imageView, Uri uri, int placeholderResId) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                ImageDecoder.Source source = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    source = ImageDecoder.createSource(context.getContentResolver(), uri);
                }
                Bitmap bitmap = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    bitmap = ImageDecoder.decodeBitmap(source);
                }
                imageView.setImageBitmap(bitmap);
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            imageView.setImageResource(placeholderResId);
        }
    }
}
