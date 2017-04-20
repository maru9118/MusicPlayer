package com.example.user.music.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017-04-14.
 */

public class MusicService extends Service {
    private static final String TAG = MusicService.class.getSimpleName();

    public static final String ACTION_PLAY = "play";
    public static final String ACTION_RESUME = "resume";
    public static final String ACTION_NEXT = "next";
    public static final String ACTION_PREV = "prev";

    private MediaPlayer mMediaPlayer;

    private IBinder mBinder = new MusicBinder();

    private MediaMetadataRetriever mRetriever;

    private List<Uri> mSongList;
    private int mIndex = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        mSongList = new ArrayList<>();

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getLong(
                        cursor.getColumnIndexOrThrow(BaseColumns._ID)));

                mSongList.add(uri);
            }
            cursor.close();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            switch (action) {
                // TODO NPE 에러 발생 지점.. intent 확인 요망. (이런식으로 할 것 , 이슈 바로 해결할 것)
                case ACTION_PLAY:
                    playMusic((Uri) intent.getParcelableExtra("uri"));
                    break;

                case ACTION_RESUME:
                    clickResumeButton();
                    break;

                case ACTION_NEXT:
                    nextMusic();
                    break;

                case ACTION_PREV:
                    prevMusic();
                    break;
            }
        }
        return START_STICKY;
    }


    private void playMusic(final Uri uri) {
        try {

            mRetriever = new MediaMetadataRetriever();
            mRetriever.setDataSource(this, uri);

            if (mMediaPlayer != null) {
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
                    Log.d(TAG, "onPrepared: 플레이로 옴");
                }
            });

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    nextMusic();
//                    Log.d(TAG, "onCompletion: 컴플릿으로 옴");
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

    // 바인드 서비스
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            // MusicService 자체의 레퍼런스
            return MusicService.this;
        }

    }

    public MediaMetadataRetriever getMetaDataRetriever() {
        return mRetriever;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    private void nextMusic() {

        mIndex++;

        if (mIndex > mSongList.size() - 1) {
            mIndex = 0;
        }
        playMusic(mSongList.get(mIndex));
    }

    private void prevMusic() {

        mIndex--;

        if (mIndex < 0) {
            mIndex = mSongList.size() - 1;
        }
        playMusic(mSongList.get(mIndex));
    }

}
