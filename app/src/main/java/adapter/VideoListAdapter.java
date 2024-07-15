package adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.ThemeUtil;
import com.example.myapplication.UserPage;
import com.example.myapplication.ViewModels.UsersViewModel;
import com.example.myapplication.entities.Video;
import com.example.myapplication.entities.User;
import com.example.myapplication.utils.CurrentUser;
import com.example.myapplication.utils.ImageLoader;
import com.example.myapplication.videowatching;
import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    private List<Video> videoList;
    private List<Video> filteredVideoList;
    private Context context;
    private User loggedInUser;
    private UsersViewModel usersViewModel;

    public VideoListAdapter(List<Video> videoList, Context context) {
        this.videoList = videoList != null ? videoList : new ArrayList<>();
        this.filteredVideoList = new ArrayList<>(this.videoList);
        this.context = context;
        CurrentUser currentUser = CurrentUser.getInstance();
        loggedInUser = currentUser.getUser().getValue();

        // Initialize UsersViewModel
        usersViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(UsersViewModel.class);
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video video = filteredVideoList.get(position);

        // Fetch owner data
        usersViewModel.getUserByEmail(video.getOwner()).observe((LifecycleOwner) context, owner -> {
            if (owner != null) {
                holder.channelTextView.setText(owner.getDisplayName());

                if (owner.getPhoto() != null && !owner.getPhoto().isEmpty()) {
                    String baseUrl = context.getResources().getString(R.string.BaseUrl);
                    String photoUrl = baseUrl + owner.getPhoto();
                    Log.d("photo", photoUrl);
                    new ImageLoader.LoadImageTask(holder.userPhotoImageView, R.drawable.person).execute(photoUrl);
                } else {
                    holder.userPhotoImageView.setImageResource(R.drawable.person);
                }
            }
        });

        holder.titleTextView.setText(video.getTitle());

        // Load the video thumbnail
        if (video.getImg() != null && !video.getImg().isEmpty()) {
            String imageUrl = context.getResources().getString(R.string.BaseUrl) + video.getImg();
            new ImageLoader.LoadImageTask(holder.thumbnailImageView, R.drawable.dog1).execute(imageUrl);
        } else {
            holder.thumbnailImageView.setImageResource(video.getThumbnailResId());
        }

        // Navigate to video watching activity on item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, videowatching.class);
            intent.putExtra("video", video);
            intent.putExtra("user", loggedInUser);
            context.startActivity(intent);
        });

        // Navigate to user page on user photo click
        holder.userPhotoImageView.setOnClickListener(v -> {
            usersViewModel.getUserByEmail(video.getOwner()).observe((LifecycleOwner) context, owner -> {
                if (owner != null) {
                    Intent intent = new Intent(context, UserPage.class);
                    Log.d("owner", owner.getEmail());
                    intent.putExtra("user_email", owner.getEmail());
                    context.startActivity(intent);
                }
            });
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

}
