package com.example.user.music.fragment;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.music.R;
import com.example.user.music.service.MusicService;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by user on 2017-04-12.
 */

public class MusicFragment extends Fragment {

//    private static final String TAG = MusicFragment.class.getSimpleName();

    public MusicFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_frag, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView musicList = (ListView) view.findViewById(R.id.music_list);

        Cursor cursor = getActivity().getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        MyAdapter adapter = new MyAdapter(getActivity(), cursor);
        musicList.setAdapter(adapter);
    }

    private class MyAdapter extends CursorAdapter {

        public MyAdapter(Context context, Cursor c) {
            super(context, c, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View convertView = LayoutInflater.from(context)
                    .inflate(R.layout.music_item, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_item);
            viewHolder.titleText = (TextView) convertView.findViewById(R.id.title_item);
            viewHolder.artistText = (TextView) convertView.findViewById(R.id.artist_item);

            convertView.setTag(viewHolder);

            return convertView;
        }

        @Override
        public void bindView(android.view.View view, Context context, Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();

            // cursor에서 ID 번째의 uri 획득
            final Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getLong(
                    cursor.getColumnIndexOrThrow(BaseColumns._ID)));

            // 미디어 정보를 얻어오는 객체
            final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mContext, uri);

            byte albumImage[] = retriever.getEmbeddedPicture();
            if (null != albumImage) {
                Glide.with(context).load(albumImage).into(viewHolder.imageView);
            } else {
                Glide.with(context).load(R.mipmap.ic_launcher).into(viewHolder.imageView);
            }

            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

//            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

            viewHolder.titleText.setText(title);
            viewHolder.artistText.setText(artist);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MusicService.class);
                    intent.setAction(MusicService.ACTION_PLAY);
                    intent.putExtra("uri", uri);
                    mContext.startService(intent);

                    /**
                     * {@link ControlFagment#setData(MediaMetadataRetriever)}
                     * {@link PlayerFragment#setMusicData(MediaMetadataRetriever)}
                     * **/
                    EventBus.getDefault().post(retriever);

                }
            });
        }
    }

    private class ViewHolder {
        ImageView imageView;
        TextView titleText;
        TextView artistText;
    }
}