package com.example.user.music.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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


    private MusicService mService;
    private boolean mBound;

    private ImageButton mPlayBtn;
    private ImageButton mPrevBtn;
    private ImageButton mNextBtn;

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

        mPlayBtn = (ImageButton) view.findViewById(R.id.play_btn);
        mPrevBtn = (ImageButton) view.findViewById(R.id.prev_btn);
        mNextBtn = (ImageButton) view.findViewById(R.id.next_btn);

        mPlayBtn.setOnClickListener(this);
        mPrevBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);

        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), MusicService.class);

        switch (v.getId()) {
            case R.id.play_btn:
                intent.setAction(MusicService.ACTION_RESUME);
                break;

            case R.id.next_btn:
                intent.setAction(MusicService.ACTION_NEXT);
                break;

            case R.id.prev_btn:
                intent.setAction(MusicService.ACTION_PREV);
                break;
        }
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
//        mPlayButton.setText(isPlaying ? "중지" : "재생");

        if (isPlaying == null || isPlaying == true) {
            mPlayBtn.setImageResource(R.drawable.stop);
        } else {
            mPlayBtn.setImageResource(R.drawable.play);
        }

        setData(mService.getMetaDataRetriever());
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;

            mService = binder.getService();
            mBound = true;

            updateButton(mService.isPlaying());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(mService, "뮤직컨트롤러 터짐", Toast.LENGTH_SHORT).show();
        }
    };

}
