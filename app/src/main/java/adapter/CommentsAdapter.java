package adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Fragments.EditCommentDialog;
import com.example.myapplication.R;
import com.example.myapplication.entities.Comment;
import com.example.myapplication.entities.user;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private FragmentActivity activity;
    private user loggedInUser;

    public CommentsAdapter(FragmentActivity activity, List<Comment> commentList, user loggedInUser) {
        this.activity = activity;
        this.commentList = commentList;
        this.loggedInUser = loggedInUser;
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
        holder.userTextView.setText(comment.getUser().getEmail());
        holder.commentTextView.setText(comment.getText());
        holder.timestampTextView.setText(comment.getTimestamp());

        // Set user image
        if (comment.getPhotoUri() != null) {
            holder.userImageView.setImageURI(Uri.parse(comment.getPhotoUri()));
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

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(activity)
                .setTitle("Delete Comment")
                .setMessage("Are you sure you want to delete this comment?")
                .setPositiveButton("Yes", (dialog, which) -> deleteComment(position))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteComment(int position) {
        commentList.remove(position);
        notifyItemRemoved(position);
    }

    private void showEditCommentDialog(int position) {
        Comment comment = commentList.get(position);
        EditCommentDialog dialog = new EditCommentDialog(activity, comment, updatedComment -> {
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
}
