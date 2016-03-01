package fr.sims.coachingproject.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.Message;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.SharedPrefUtil;


public class MessageAdapter extends ArrayAdapter {
    private List<Message> mMessageList;

    private Context mContext;

    private LayoutInflater mInflater;

    public MessageAdapter(Context ctxt) {
        super(ctxt, R.layout.message_item);

        mMessageList=new ArrayList<>();
        this.mContext = ctxt;
        mInflater = LayoutInflater.from( mContext );
    }

    public void setData(List<Message> dataset){
        mMessageList.clear();
        mMessageList.addAll(dataset);
        this.notifyDataSetChanged();
    }

    public void clearData(){
        mMessageList.clear();
        notifyDataSetChanged();
    }

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
    public View getView(int position, View convertView, ViewGroup parent) {

        Message message = getItem( position );

        /* create a new view of my layout and inflate it in the row */
        if(message.mSender.mIdDb== SharedPrefUtil.getConnectedUserId(mContext)){
            convertView = mInflater.inflate(R.layout.message_item_authored,null );
        }else{
            convertView = mInflater.inflate(R.layout.message_item,null );
            ImageView picture = (ImageView) convertView.findViewById(R.id.picture);
            if(message.mSender != null){
                Picasso.with(mContext).load(message.mSender.mPicture).into(picture);
            }
        }

        TextView content = (TextView) convertView.findViewById(R.id.content);
        content.setText(message.mContent);

        TextView time = (TextView) convertView.findViewById(R.id.time);
        SimpleDateFormat sdf=new SimpleDateFormat( "dd-MM-yy HH:mm:ss");
        time.setText(sdf.format(message.mTime));

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
        return mMessageList.size()==0;
    }
}
