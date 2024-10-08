package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.entities.Video;

public class ShareFragment extends DialogFragment {

    private String videoLink;

    public ShareFragment(Video currentVideo) {
        this.videoLink = "myapp://video?id=" + currentVideo.get_id();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        TextView shareLinkText = view.findViewById(R.id.shareLinkText);
        Button copyLinkButton = view.findViewById(R.id.copyLinkButton);

        shareLinkText.setText(videoLink);
        copyLinkButton.setOnClickListener(v -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Video Link", videoLink);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Link copied to clipboard", Toast.LENGTH_SHORT).show();

        });

        return view;
    }
}
