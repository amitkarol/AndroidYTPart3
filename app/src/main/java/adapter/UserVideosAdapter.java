package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class UserVideosAdapter extends RecyclerView.Adapter<UserVideosAdapter.UserVideoViewHolder> {

    private List<String> userVideos;

    public UserVideosAdapter(List<String> userVideos) {
        this.userVideos = userVideos;
    }

    @NonNull
    @Override
    public UserVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new UserVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserVideoViewHolder holder, int position) {
        String videoTitle = userVideos.get(position);
        holder.textViewVideoTitle.setText(videoTitle);
    }

    @Override
    public int getItemCount() {
        return userVideos.size();
    }

    static class UserVideoViewHolder extends RecyclerView.ViewHolder {

        TextView textViewVideoTitle;

        public UserVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewVideoTitle = itemView.findViewById(R.id.textViewVideoTitle);
        }
    }
}
