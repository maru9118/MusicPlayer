package com.example.user.music.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

/**
 * Created by user on 2017-04-14.
 */

public class MusicService extends Service {
//    private static final String TAG = MusicService.class.getSimpleName();

    public static String ACTION_PLAY = "play";
    public static String ACTION_RESUME = "resume";

    private MediaPlayer mMediaPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            if (!TextUtils.isEmpty(action)) {
                // TODO NPE 에러 발생 지점.. intent 확인 요망. (이런식으로 할 것 , 이슈 바로 해결할 것)
                if (ACTION_PLAY.equals(action)) {
                    playMusic((Uri) intent.getParcelableExtra("uri"));
                } else if (ACTION_RESUME.equals(action)) {
                    clickResumeButton();
                }
            }
        }

        return START_STICKY;
    }

    private void playMusic(Uri uri) {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(this, uri);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();

                    /**
                     *{@link com.example.user.music.fragment.ControlFagment#updateButton(Boolean)}
                     * **/
                    EventBus.getDefault().post(isPlaying());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    private void clickResumeButton() {
        if (mMediaPlayer == null) {
            return;
        }

        if (isPlaying()) {
            mMediaPlayer.pause();
//            Log.d(TAG, "clickResumeButton: 중지");
        } else {
            mMediaPlayer.start();
//            Log.d(TAG, "clickResumeButton: 시작");
        }

        /**
         * {@link com.example.user.music.fragment.ControlFagment#updateButton(Boolean)}
         * */
        EventBus.getDefault().post(isPlaying());

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
