package adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Fragments.EditCommentDialog;
import com.example.myapplication.R;
import com.example.myapplication.ViewModels.CommentsViewModel;
import com.example.myapplication.entities.Comment;
import com.example.myapplication.entities.User;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private FragmentActivity activity;
    private User loggedInUser;
    private Context context;
    private CommentsViewModel commentsViewModel;

    public CommentsAdapter(FragmentActivity activity, List<Comment> commentList, User loggedInUser) {
        this.activity = activity;
        this.commentList = new ArrayList<>(commentList != null ? commentList : new ArrayList<>());
        this.loggedInUser = loggedInUser;
        this.context = activity.getApplicationContext();
        this.commentsViewModel = new ViewModelProvider(activity).get(CommentsViewModel.class);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.userTextView.setText(comment.getUserName());
        holder.commentTextView.setText(comment.getText());
        holder.timestampTextView.setText(comment.getDate());

        // Set user image using AsyncTask
        if (comment.getProfilePic() != null) {
            String imageUrl = context.getResources().getString(R.string.BaseUrl) + comment.getProfilePic();
            Log.d("commentpic", imageUrl);
            new LoadImageTask(holder.userImageView, R.drawable.person).execute(imageUrl);
        } else {
            holder.userImageView.setImageResource(R.drawable.person);
        }

        holder.deleteButton.setOnClickListener(v -> {
            if (loggedInUser.getEmail().equals("testuser@example.com")) {
                redirectToLogin();
                return;
            }
            showDeleteConfirmationDialog(holder.getAdapterPosition());
        });

        holder.editButton.setOnClickListener(v -> {
            if (loggedInUser.getEmail().equals("testuser@example.com")) {
                redirectToLogin();
                return;
            }
            showEditCommentDialog(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void setComments(List<Comment> newCommentList) {
        commentList.clear();
        if (newCommentList != null) {
            commentList.addAll(newCommentList);
        }
        notifyDataSetChanged();
    }

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(activity)
                .setTitle("Delete Comment")
                .setMessage("Are you sure you want to delete this comment?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    String commentId = commentList.get(position).get_id();
                    commentsViewModel.deleteComment(loggedInUser.getEmail(), String.valueOf(commentList.get(position).getVideoId()), commentId);
                    commentList.remove(position);
                    notifyItemRemoved(position);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showEditCommentDialog(int position) {
        Comment comment = commentList.get(position);
        EditCommentDialog dialog = new EditCommentDialog(activity, comment, updatedComment -> {
            String commentId = comment.get_id();
            commentsViewModel.editComment(loggedInUser.getEmail(), String.valueOf(commentList.get(position).getVideoId()), commentId, updatedComment.getText());
            commentList.set(position, updatedComment);
            notifyItemChanged(position);
        });
        dialog.show(activity.getSupportFragmentManager(), "EditCommentDialog");
    }

    private void redirectToLogin() {
        Intent loginIntent = new Intent(activity, com.example.myapplication.login.class);
        activity.startActivity(loginIntent);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView userTextView;
        public TextView commentTextView;
        public TextView timestampTextView;
        public ImageView userImageView;
        public ImageButton editButton;
        public ImageButton deleteButton;

        public CommentViewHolder(View view) {
            super(view);
            userTextView = view.findViewById(R.id.userTextView);
            commentTextView = view.findViewById(R.id.commentTextView);
            timestampTextView = view.findViewById(R.id.timestampTextView);
            userImageView = view.findViewById(R.id.userImageView);
            editButton = view.findViewById(R.id.editButton);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }

    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;
        private int placeholderResId;

        public LoadImageTask(ImageView imageView, int placeholderResId) {
            this.imageView = imageView;
            this.placeholderResId = placeholderResId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageView.setImageResource(placeholderResId);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                // Resize the image to a square
                int width = result.getWidth();
                int height = result.getHeight();
                int newSize = Math.min(width, height);
                Bitmap resizedBitmap = Bitmap.createBitmap(result, 0, 0, newSize, newSize);
                // Rotate the image if needed
                Matrix matrix = new Matrix();
                matrix.postRotate(90); // Rotate 90 degrees if needed
                Bitmap rotatedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight(), matrix, true);
                imageView.setImageBitmap(rotatedBitmap);
            } else {
                imageView.setImageResource(placeholderResId);
            }
        }
    }
}
