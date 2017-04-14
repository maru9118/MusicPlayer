package com.example.user.music.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by user on 2017-04-14.
 */

public class MusicService extends Service {
    public static String ACTION_PLAY = "play";
    public static String ACTION_RESUME = "resume";

    private MediaPlayer mMediaPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (ACTION_PLAY.equals(action)) {
            playMusic((Uri) intent.getParcelableExtra("uri"));
        } else if (ACTION_RESUME.equals(action)) {
            clickResumeButton();
        }
        return START_STICKY;
    }

    private void playMusic(Uri uri) {
        mMediaPlayer = new MediaPlayer();

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(this, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
    }

    private void clickResumeButton() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
    }

    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }
}
