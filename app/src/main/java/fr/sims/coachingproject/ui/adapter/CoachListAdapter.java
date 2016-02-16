package fr.sims.coachingproject.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.UserProfile;

/**
 * Created by dfour on 11/02/2016.
 */
public class CoachListAdapter extends RecyclerView.Adapter<CoachListAdapter.ViewHolder> {

    private List<UserProfile> mDatasetCr;
    private List<UserProfile> mDatasetLr;
    private List<UserProfile> mDatasetPendingCr;
    private List<UserProfile> mDatasetPendingLr;

    public static final int HEADER_COACH =0;
    public static final int LIST_COACH =1;
    public static final int HEADER_LEARNER=2;
    public static final int HEADER_REQUEST_COACH=3;
    public static final int HEADER_REQUEST_LEARNER=4;
    public static final int LIST_LEARNER =5;
    public static final int LIST_PENDING_LEARNER =6;
    public static final int LIST_PENDING_COACH =7;



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

    protected  class HeaderViewHolder extends ViewHolder{
        protected TextView mTitleTv;

        public HeaderViewHolder(View v) {super(v);
        this.mTitleTv = (TextView) v.findViewById(R.id.header);}
    }

    public CoachListAdapter() {
        mDatasetCr = new ArrayList<>();
        mDatasetLr = new ArrayList<>();
        mDatasetPendingCr = new ArrayList<>();
        mDatasetPendingLr = new ArrayList<>();
    }

    public CoachListAdapter(List<UserProfile> datasetCr, List<UserProfile> datasetLr,
                            List<UserProfile> datasetPendingCr, List<UserProfile> datasetPendingLr  ) {
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
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v;

        switch (viewType)
        {
            case HEADER_REQUEST_LEARNER :
            case HEADER_REQUEST_COACH :
            case HEADER_LEARNER :
            case HEADER_COACH :
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_header_item, parent, false);
            return new HeaderViewHolder(v);
            default :
            case LIST_PENDING_COACH :
            case LIST_PENDING_LEARNER :
            case LIST_LEARNER :
            case LIST_COACH : // normal list - Coach
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_coach, parent, false);
                return new CoachViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        CoachViewHolder cvh;
        HeaderViewHolder hvh;
        UserProfile cr;
        switch (vh.getItemViewType()) {
            case LIST_COACH:
                cvh = (CoachViewHolder) vh;
                    cr = mDatasetCr.get(position - 1);
                    // TODO Image Loader        vh.mPictureIV
                    cvh.mNameTV.setText(cr.mDisplayName);
                    cvh.mDescTV.setText(cr.mCity);
                break;
            case LIST_LEARNER:
                cvh = (CoachViewHolder) vh;
                    cr = mDatasetLr.get(position - (mDatasetCr.size() + 2));
                    // TODO Image Loader        vh.mPictureIV
                    cvh.mNameTV.setText(cr.mDisplayName);
                    cvh.mDescTV.setText(cr.mCity);
                break;
            case LIST_PENDING_COACH:
                cvh = (CoachViewHolder) vh;
                    cr = mDatasetPendingCr.get(position - (mDatasetCr.size() + mDatasetLr.size() + 3));
                    // TODO Image Loader        vh.mPictureIV
                    cvh.mNameTV.setText(cr.mDisplayName);
                    cvh.mDescTV.setText(cr.mCity);
                break;
            case LIST_PENDING_LEARNER:
                cvh = (CoachViewHolder) vh;
                    cr = mDatasetPendingLr.get(position - (getItemCount() - 1));
                    // TODO Image Loader        vh.mPictureIV
                    cvh.mNameTV.setText(cr.mDisplayName);
                    cvh.mDescTV.setText(cr.mCity);
                break;
            case HEADER_COACH:
                hvh = (HeaderViewHolder) vh;
                hvh.mTitleTv.setText("My trainer");
                break;
            case HEADER_LEARNER:
                hvh = (HeaderViewHolder) vh;
                hvh.mTitleTv.setText("My Learner");
                break;
            case HEADER_REQUEST_COACH:
                hvh = (HeaderViewHolder) vh;
                hvh.mTitleTv.setText("Pending trainer request ");
                break;
            default:
            case HEADER_REQUEST_LEARNER:
                hvh = (HeaderViewHolder) vh;
                hvh.mTitleTv.setText("My requests");
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

}
