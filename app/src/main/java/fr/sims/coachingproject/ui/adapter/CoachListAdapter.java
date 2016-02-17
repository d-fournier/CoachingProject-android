package fr.sims.coachingproject.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.activity.MainActivity;

/**
 * Created by dfour on 11/02/2016.
 */
public class CoachListAdapter extends RecyclerView.Adapter<CoachListAdapter.ViewHolder> {

    private List<UserProfile> mDatasetCr;
    private List<UserProfile> mDatasetLr;
    private List<UserProfile> mDatasetPendingCr;
    private List<UserProfile> mDatasetPendingLr;
    private Context mCtx;

    public static final int HEADER_COACH = 0;
    public static final int LIST_COACH = 1;
    public static final int HEADER_LEARNER = 2;
    public static final int HEADER_REQUEST_COACH = 3;
    public static final int HEADER_REQUEST_LEARNER = 4;
    public static final int LIST_LEARNER = 5;
    public static final int LIST_PENDING_LEARNER = 6;
    public static final int LIST_PENDING_COACH = 7;

    private OnItemClickListener mOnItemClickListener;

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View v) {
            super(v);
        }
    }


    protected class CoachViewHolder extends ViewHolder {
        protected ImageView mPictureIV;
        protected TextView mNameTV;
        protected TextView mDescTV;

        public CoachViewHolder(View v) {
            super(v);
            this.mPictureIV = (ImageView) v.findViewById(R.id.user_picture);
            this.mNameTV = (TextView) v.findViewById(R.id.user_name);
            this.mDescTV = (TextView) v.findViewById(R.id.user_description);
        }


    }

    protected  class HeaderViewHolder extends ViewHolder {
        protected TextView mTitleTv;

        public HeaderViewHolder(View v) {super(v);
        this.mTitleTv = (TextView) v.findViewById(R.id.header);}
    }

    public CoachListAdapter(Context ctx) {
        mCtx = ctx;
        mDatasetCr = new ArrayList<>();
        mDatasetLr = new ArrayList<>();
        mDatasetPendingCr = new ArrayList<>();
        mDatasetPendingLr = new ArrayList<>();
    }

    public CoachListAdapter(Context ctx,List<UserProfile> datasetCr, List<UserProfile> datasetLr,
                            List<UserProfile> datasetPendingCr, List<UserProfile> datasetPendingLr  ) {
        mCtx = ctx;
        mDatasetCr = datasetCr;
        mDatasetLr = datasetLr;
        mDatasetPendingLr = datasetPendingLr;
        mDatasetPendingCr = datasetPendingCr;
    }

    public void setDataCr(List<UserProfile> dataset){
        mDatasetCr = dataset;
        notifyDataSetChanged();
    }

    public void setDataLr(List<UserProfile> dataset)
    {
        mDatasetLr = dataset;
        notifyDataSetChanged();
    }

    public void setDataPendingCr(List<UserProfile> dataset){
        mDatasetPendingCr = dataset;
        notifyDataSetChanged();
    }

    public void setDataPendingLr(List<UserProfile> dataset)
    {
        mDatasetPendingLr = dataset;
        notifyDataSetChanged();
    }

    public void clearData(){
        mDatasetCr.clear();
        mDatasetLr.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View v;

        switch (viewType)
        {
            case HEADER_REQUEST_LEARNER :
            case HEADER_REQUEST_COACH :
            case HEADER_LEARNER :
            case HEADER_COACH :
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_header_item, parent, false);
                return new HeaderViewHolder(v);
            default :
            case LIST_PENDING_COACH :
            case LIST_PENDING_LEARNER :
            case LIST_LEARNER :
            case LIST_COACH : // normal list - Coach
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_coach, parent, false);

                return new CoachViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        final CoachViewHolder cvh;
        HeaderViewHolder hvh;
        UserProfile cr;
        switch (vh.getItemViewType()) {
            case LIST_COACH:
                cvh = (CoachViewHolder) vh;
                    cr = mDatasetCr.get(position - 1);
                    Picasso.with(mCtx).load(cr.mPicture).into(cvh.mPictureIV);
                    cvh.mNameTV.setText(cr.mDisplayName);
                    cvh.mDescTV.setText(cr.mCity);

                if(mOnItemClickListener != null) {
                    /**
                     * 这里加了判断，itemViewHolder.itemView.hasOnClickListeners()
                     * 目的是减少对象的创建，如果已经为view设置了click监听事件,就不用重复设置了
                     * 不然每次调用onBindViewHolder方法，都会创建两个监听事件对象，增加了内存的开销
                     */
                    if(!cvh.itemView.hasOnClickListeners()) {
                        cvh.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int pos = cvh.getAdapterPosition();
                                mOnItemClickListener.onItemClick(v, pos);
                            }
                        });
                        cvh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                int pos = cvh.getAdapterPosition();
                                mOnItemClickListener.onItemLongClick(v, pos);
                                return true;
                            }
                        });
                    }
                }

                break;
            case LIST_LEARNER:
                cvh = (CoachViewHolder) vh;
                    cr = mDatasetLr.get(position - (mDatasetCr.size() + 2));
                    Picasso.with(mCtx).load(cr.mPicture).into(cvh.mPictureIV);
                    cvh.mNameTV.setText(cr.mDisplayName);
                    cvh.mDescTV.setText(cr.mCity);
                break;
            case LIST_PENDING_COACH:
                cvh = (CoachViewHolder) vh;
                    cr = mDatasetPendingCr.get(position - (mDatasetCr.size() + mDatasetLr.size() + 3));
                    Picasso.with(mCtx).load(cr.mPicture).into(cvh.mPictureIV);
                    cvh.mNameTV.setText(cr.mDisplayName);
                    cvh.mDescTV.setText(cr.mCity);
                break;
            case LIST_PENDING_LEARNER:
                cvh = (CoachViewHolder) vh;
                    cr = mDatasetPendingLr.get(position - (getItemCount() - 1));
                    Picasso.with(mCtx).load(cr.mPicture).into(cvh.mPictureIV);
                    cvh.mNameTV.setText(cr.mDisplayName);
                    cvh.mDescTV.setText(cr.mCity);
                break;
            case HEADER_COACH:
                hvh = (HeaderViewHolder) vh;
                hvh.mTitleTv.setText(R.string.my_coach);
                break;
            case HEADER_LEARNER:
                hvh = (HeaderViewHolder) vh;
                hvh.mTitleTv.setText(R.string.my_trainee);
                break;
            case HEADER_REQUEST_COACH:
                hvh = (HeaderViewHolder) vh;
                hvh.mTitleTv.setText(R.string.pending_coach_request);
                break;
            default:
            case HEADER_REQUEST_LEARNER:
                hvh = (HeaderViewHolder) vh;
                hvh.mTitleTv.setText(R.string.pending_trainee_request);
                break;
        }

    }

    @Override
    public int getItemViewType(int position)
    {
       if(position==0)
           return HEADER_COACH;
       else if( position > 0 && position <= mDatasetCr.size())
           return LIST_COACH;
        else if (position == mDatasetCr.size()+1)
           return HEADER_LEARNER;
       else if( position > mDatasetCr.size()+1 && position <= mDatasetCr.size() + (1 + mDatasetLr.size()))
           return LIST_LEARNER;
        else if(position == mDatasetCr.size() + (mDatasetLr.size() + 2) )
           return HEADER_REQUEST_COACH;
       else if( position >  mDatasetCr.size() + (mDatasetLr.size() + 2) && position <= (mDatasetCr.size() +
               mDatasetLr.size() + mDatasetPendingCr.size() +2) )
           return LIST_PENDING_COACH;
        else if(position == mDatasetCr.size() + mDatasetLr.size() + mDatasetPendingCr.size() + 3)
           return HEADER_REQUEST_LEARNER;
       else
           return LIST_PENDING_LEARNER;

    }
    @Override
    public int getItemCount() {
       return mDatasetCr.size()+ mDatasetLr.size() +
               mDatasetPendingCr.size() + mDatasetPendingLr.size() + 4;
    }


    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    /**
     * 处理item的点击事件和长按事件
     */
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
        public void onItemLongClick(View view, int position);
    }



}
