package fr.sims.coachingproject.ui.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.LinkedList;

import fr.sims.coachingproject.model.Message;

/**
 * Created by damien on 2016/2/14.
 */
public abstract class MessageAdapter extends BaseAdapter {
    private LinkedList<Message> mData;
    private Context mContext;

    public MessageAdapter(LinkedList<Message> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



}
