package fr.sims.coachingproject.ui.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.Message;


public class MessageAdapter extends ArrayAdapter implements ListAdapter {
    private List<Message> mMessageList;

    private Context mContext;

    private LayoutInflater mInflater;

    public MessageAdapter(Context ctxt) {
        super(ctxt, R.layout.message_item);

        mMessageList=new ArrayList<Message>();
        this.mContext = ctxt;
        mInflater = LayoutInflater.from( mContext );
    }

    public void setData(List<Message> dataset){
        mMessageList=dataset;
        notifyDataSetChanged();
    }

    public void clearData(){
        mMessageList.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer){ }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) { }

    @Override
    public int getCount() {
        return mMessageList.size();
    }

    @Override
    public Message getItem(int position) {

        return mMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return mMessageList.get(position).mIdDb;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /* create a new view of my layout and inflate it in the row */
        convertView = mInflater.inflate(R.layout.message_item,null );

        Message message = getItem( position );

        TextView content = (TextView) convertView.findViewById(R.id.content);
        content.setText(message.mContent);

        TextView time = (TextView) convertView.findViewById(R.id.time);
        SimpleDateFormat sdf=new SimpleDateFormat( "dd-MM-yy HH:mm:ss");
        time.setText(sdf.format(message.mTime));

        ImageView picture = (ImageView) convertView.findViewById(R.id.picture);
        if(message.mSender != null){
            Picasso.with(mContext).load(message.mSender.mPicture).into(picture);
        }




        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.message_item;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return mMessageList.size()>0;
    }
}
