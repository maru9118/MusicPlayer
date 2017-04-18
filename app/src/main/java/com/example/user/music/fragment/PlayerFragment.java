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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.music.R;
import com.example.user.music.service.MusicService;

import java.util.Locale;

/**
 * Created by user on 2017-04-09.
 */

public class PlayerFragment extends Fragment {

    private static final String TAG = PlayerFragment.class.getSimpleName();

    private ImageView mAlbumMain;
    private TextView mTitleView;
    private TextView mArtistView;

    private TextView mCurrentText;
    private TextView mDurationText;

    private SeekBar mSeekBar;

    private MusicService mService;

    private boolean mBound;

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
        mTitleView = (TextView) view.findViewById(R.id.title_text);
        mArtistView = (TextView) view.findViewById(R.id.artist_text);

        mCurrentText = (TextView) view.findViewById(R.id.current_text);
        mDurationText = (TextView) view.findViewById(R.id.duration_text);

        mSeekBar = (SeekBar) view.findViewById(R.id.seekBar);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mService.getMediaPlayer().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    private void updateData(boolean isPlaying) {

        if (isPlaying) {
            MediaMetadataRetriever retriever = mService.getMetaDataRetriever();

            if (retriever != null) {
                String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                int duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

                int min = duration / 1000 / 60;
                int sec = duration / 1000 % 60;

                byte albumImage[] = retriever.getEmbeddedPicture();

                if (albumImage != null) {
                    Glide.with(this).load(albumImage).into(mAlbumMain);
                } else {
                    Glide.with(this).load(R.mipmap.ic_launcher).into(mAlbumMain);
                }
                mTitleView.setText(title);
                mArtistView.setText(artist);

                mDurationText.setText(String.format(String.format(Locale.KOREA, "%d:%02d", min, sec)));

                mSeekBar.setMax(duration);
            }
        }
        updateTime();
    }

    public void updateTime() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                // while문으로 음악 실행중일때 계속 진행
                while (mService.isPlaying()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mSeekBar.setProgress(mService.getMediaPlayer().getCurrentPosition());
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Binder와 연결될 것이며 IBinder 타입으로 넘어오는 것을 캐스팅 하여 사용
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;

            mService = binder.getService();
            mBound = true;

            updateData(mService.isPlaying());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(mService, "터짐", Toast.LENGTH_SHORT).show();
        }
    };

}
