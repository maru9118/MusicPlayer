package com.example.user.music.fragment;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.user.music.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by user on 2017-04-09.
 */

public class PlayerFragment extends Fragment {

    private ImageView mAlbumMain;

    public PlayerFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_frag, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mAlbumMain = (ImageView) view.findViewById(R.id.album_main);

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

    @Subscribe
    public void setMusicData(MediaMetadataRetriever retriever) {
        if (retriever != null) {
            byte albumImage[] = retriever.getEmbeddedPicture();

            if (albumImage != null) {
                Glide.with(this).load(albumImage).into(mAlbumMain);
            } else {
                Glide.with(this).load(R.mipmap.ic_launcher).into(mAlbumMain);
            }
        }
    }
}
