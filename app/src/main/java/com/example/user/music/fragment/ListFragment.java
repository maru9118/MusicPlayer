package com.example.user.music.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.music.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017-04-10.
 */

public class ListFragment extends Fragment {

    public ListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_frag, container, false);

        List<String> testData = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            testData.add("가수" + i);
        }

        ListView listView = (ListView) view.findViewById(R.id.list_view);
        MusicListAdapter adapter = new MusicListAdapter(testData);
        listView.setAdapter(adapter);

        return view;
    }

    private class MusicListAdapter extends BaseAdapter {

        private List<String> mData;

        public MusicListAdapter(List<String> list) {
            mData = list;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

                viewHolder.mTitleText = (TextView) convertView.findViewById(R.id.title_item);
                viewHolder.mSingerText = (TextView) convertView.findViewById(R.id.singer_item);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String data = mData.get(position);
            viewHolder.mTitleText.setText(data);
            viewHolder.mSingerText.setText(data);

            return convertView;
        }

        private class ViewHolder {
            private TextView mTitleText;
            private TextView mSingerText;
        }
    }
}
