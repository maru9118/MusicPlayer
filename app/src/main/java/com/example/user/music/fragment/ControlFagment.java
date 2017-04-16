package com.example.user.music.fragment;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.music.R;
import com.example.user.music.service.MusicService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by user on 2017-04-09.
 */

public class ControlFagment extends Fragment implements View.OnClickListener {


//    private static final String TAG = ControlFagment.class.getSimpleName();

    private ImageView mAlbumArt;
    private TextView mTitleText;
    private TextView mArtistText;
    private Button mPlayButton;

    public ControlFagment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.control_frag, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mAlbumArt = (ImageView) view.findViewById(R.id.album_image);
        mTitleText = (TextView) view.findViewById(R.id.title_text);
        mArtistText = (TextView) view.findViewById(R.id.artist_text);

        mPlayButton = (Button) view.findViewById(R.id.play_btn);
        mPlayButton.setOnClickListener(this);

        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), MusicService.class);
        intent.setAction(MusicService.ACTION_RESUME);
        getActivity().startService(intent);
    }

    @Subscribe
    public void setData(MediaMetadataRetriever retriever) {
        if (retriever != null) {
            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

            byte albumImage[] = retriever.getEmbeddedPicture();

            if (albumImage != null) {
                Glide.with(this).load(albumImage).into(mAlbumArt);
            } else {
                Glide.with(this).load(R.mipmap.ic_launcher).into(mAlbumArt);
            }
            mTitleText.setText(title);
            mArtistText.setText(artist);
        }
    }

    @Subscribe
    public void updateButton(Boolean isPlaying) {
        mPlayButton.setText(isPlaying ? "중지" : "재생");
    }
}
