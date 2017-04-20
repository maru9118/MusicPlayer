//package com.example.user.music;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.res.Resources;
//import android.database.Cursor;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.MediaController;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * Created by user on 2017-04-18.
// */
//
//public class MainActivity2 extends Activity implements
//        MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {
//    private MediaPlayer mediaPlayer;
//    private android.widget.MediaController mediaController;
//    private String audioFile;
//    public static final String MEDIA_PATH = new String("/sdcard/");
//    public ArrayList<Music> songs = new ArrayList<Music>();
//    public int current = 0;
//    String data;
//
//    public void onCreate(Bundle icicle) {
//        super.onCreate(icicle);
//        setContentView(R.layout.activity_main2);
//        updateSongList();
//        Bundle bundle = getIntent().getExtras();
//        data = bundle.getString("key");
//        Log.d("", "데이터를 받는가?:" + data);
//        //Toast.makeText(getApplicationContext(),""+data+Toast.LENGTH_LONG).show();
//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.setOnPreparedListener(this);
//    }
//
//    private class MusicInformation extends ArrayAdapter<Music> {
//        private ArrayList<Music> items;
//
//        public MusicInformation(Context context, int textViewResourceId, ArrayList<Music> items) {
//            super(context, textViewResourceId, items);
//            this.items = items;
//        }
//
//        @Override
//        public View getView(int position, View view, ViewGroup parent) {
//            View v = view;
//            if (v == null) {
//                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                v = vi.inflate(R.layout.row, null);
//            }
//            Music m = items.get(position);
//            if (m != null) {
//                ImageView imageview = (ImageView) v.findViewById(R.id.row_album_art);
//                TextView tt = (TextView) v.findViewById(R.id.row_artist);
//                TextView bt = (TextView) v.findViewById(R.id.row_title);
//                if (tt != null) {
//                    tt.setText(m.getGasu() + " - " + m.getJemok());
//                }
//                if (bt != null) {
//                    bt.setText(m.getAlbumName());
//                }
//                if (imageview != null) {
//                    imageview.setImageDrawable(m.getImage());
//                }
//            }
//            return v;
//        }
//    }
//
//    public void playsong(String Spath) {
//        File f = new File(Spath);
//        if (!f.exists()) {
//
//            return;
//        }
//        FileInputStream fis = null;
//
//        try {
//            fis = new FileInputStream(f);
//            mediaPlayer.reset();
//            mediaPlayer.setDataSource(fis.getFD(), 0, f.length());
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//            Toast.makeText(this, "Playing: " + Spath, Toast.LENGTH_SHORT).show();
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer arg0) {
//                    nextSong();
//                }
//            });
//        } catch (IOException e) {
//            Toast.makeText(this, "error: " + e, Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//    }
//
//    class Music {
//        private String gasu;
//        private String jemok;
//        private String albumName;
//        private Drawable drawimage;
//        private String data;
//
//        public Music(String _gasu, String _jemok, String _albumName, Drawable _image, String data) {
//            this.gasu = _gasu;
//            this.jemok = _jemok;
//            this.albumName = _albumName;
//            this.drawimage = _image;
//            this.data = data;
//        }
//
//        public String getGasu() {
//            return gasu;
//        }
//
//        public String getJemok() {
//            return jemok;
//        }
//
//        public String getAlbumName() {
//            return albumName;
//        }
//
//        public Drawable getImage() {
//            return drawimage;
//        }
//    }
//
//    public void onPrepared(MediaPlayer mediaPlayer) {
//        mediaController = new MediaController(this);
//        mediaController.setMediaPlayer(this);
//        mediaController.setAnchorView((android.widget.MediaController) findViewById(R.id.mediaController1));
//    }
//
//    public void updateSongList() {
//        Music[] music = new Music[10];
//        int cnt = 0;
//        Resources r = getResources();
//        BitmapDrawable mDefaultAlbumIcon = (BitmapDrawable) r.getDrawable(R.drawable.git);
//        String[] mCursorCols = new String[]{
//                MediaStore.Audio.Media._ID,
//                MediaStore.Audio.Media.ARTIST,
//                MediaStore.Audio.Media.TITLE,
//                MediaStore.Audio.Media.ALBUM,
//                MediaStore.Audio.Media.DATA
//        };
//        Cursor cur = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mCursorCols, null, null, null);
//        if (cur.moveToFirst()) {
//            String title, artist, album, data;
//            Drawable d;
//            int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
//            int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
//            int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
//            int dataColumn = cur.getColumnIndex(MediaStore.Audio.Media.DATA);
//
//            do {
//                title = cur.getString(titleColumn);
//                artist = cur.getString(artistColumn);
//                album = cur.getString(albumColumn);
//                data = cur.getString(dataColumn);
//
//                d = MusicUtils.getCachedArtwork(this, cnt + 1, mDefaultAlbumIcon);
//                songs.add(new Music(artist, title, album, d, data));
//                cnt++;
//            } while (cur.moveToNext());
//        }
//        MusicInformation songList = new MusicInformation(this, R.layout.row, songs);
//    }
//
//    private void nextSong() {
//        if (++current >= songs.size()) {
//            current = 0;
//        } else {
//            playsong(MEDIA_PATH + songs.get(current));
//            Log.d("", "다음 데이터를 받음?:" + MEDIA_PATH);
//            Toast.makeText(getApplicationContext(), "다음 곡을 재생합니다.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void start() {
//        playsong(data);
//
//    }
