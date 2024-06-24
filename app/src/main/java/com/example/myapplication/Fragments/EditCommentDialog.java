package com.example.myapplication.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.entities.Comment;

public class EditCommentDialog extends DialogFragment {
    private Comment comment;
    private EditCommentListener listener;

    public EditCommentDialog(Context context, Comment comment, EditCommentListener listener) {
        this.comment = comment;
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_comment);

        EditText commentEditText = dialog.findViewById(R.id.commentEditText);
        commentEditText.setText(comment.getText());

        Button saveButton = dialog.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            String updatedCommentText = commentEditText.getText().toString().trim();
            if (!updatedCommentText.isEmpty()) {
                comment.setText(updatedCommentText);
                listener.onCommentEdited(comment);
                dismiss();
            }
        });

        return dialog;
    }

    public interface EditCommentListener {
        void onCommentEdited(Comment updatedComment);
    }
}
