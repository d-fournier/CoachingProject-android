package fr.sims.coachingproject.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.Message;
import fr.sims.coachingproject.util.ImageUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.CommonViewHolder> {
    private List<Message> mMessageList;
    private Context mContext;
    private long mCurrentUserId;
    private SimpleDateFormat mDateFormat;

    private static final int MESSAGE_ITEM_AUTHORED  = 0;
    private static final int MESSAGE_ITEM           = 1;

    protected static class CommonViewHolder extends RecyclerView.ViewHolder {

        protected TextView mContent;
        protected TextView mDateTV;

        public CommonViewHolder(View v) {
            super(v);
            this.mContent = (TextView) v.findViewById(R.id.message_content);
            this.mDateTV = (TextView) v.findViewById(R.id.message_time);
            v.setLongClickable(true);
        }
    }

    protected static class OtherUserViewHolder extends CommonViewHolder {
        protected ImageView mPictureIV;

        public OtherUserViewHolder(View v) {
            super(v);
            mPictureIV = (ImageView) v.findViewById(R.id.message_user_picture);
        }
    }

    public MessageAdapter(Context context, boolean isPinned) {
        mMessageList=new ArrayList<>();
        this.mContext = context;
        mCurrentUserId = SharedPrefUtil.getConnectedUserId(context);
        mDateFormat = new SimpleDateFormat("dd-MMMM HH:mm");
    }

    public void setData(List<Message> dataset){
        mMessageList.clear();
        if(mCurrentUserId!=-1)
        mMessageList.addAll(dataset);
        this.notifyDataSetChanged();
    }

    public void clearData(){
        mMessageList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position).mSender.mIdDb== mCurrentUserId) {
            return MESSAGE_ITEM_AUTHORED;
        } else {
            return MESSAGE_ITEM;
        }
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v;
        final CommonViewHolder vh;
        switch(viewType){
            case MESSAGE_ITEM:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_item, parent, false);
                vh = new OtherUserViewHolder(v);
                break;
            case MESSAGE_ITEM_AUTHORED:
            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_item_authored, parent, false);
                vh = new CommonViewHolder(v);
                break;
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(final CommonViewHolder vh, int position) {
        Message message = getItem(position);

        vh.mContent.setText(message.mContent);
        vh.mDateTV.setText(mDateFormat.format(message.mTime));

        if(getItemViewType(position) == MESSAGE_ITEM)
            ImageUtil.loadProfilePicture(mContext, message.mSender.mPicture, ((OtherUserViewHolder) vh).mPictureIV);

    }

    public Message getItem(int position) {
        return mMessageList.get(position);
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public long getItemId(int position) {
        return mMessageList.get(position).mIdDb;
    }

}
