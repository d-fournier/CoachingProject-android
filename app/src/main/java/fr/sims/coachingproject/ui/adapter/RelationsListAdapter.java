package fr.sims.coachingproject.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.util.SharedPrefUtil;

// TODO Header ???

/**
 * Created by dfour on 11/02/2016.
 */
public class RelationsListAdapter extends RecyclerView.Adapter<RelationsListAdapter.ViewHolder> {

    private long mCurrentUserId;

    /**
     * My Coach
     */
    private List<CoachingRelation> mDatasetCr;
    /**
     * My Trainee
     */
    private List<CoachingRelation> mDatasetLr;
    /**
     * Pending Trainer Request
     */
    private List<CoachingRelation> mDatasetPendingCr;
    /**
     * Pending Trainee request
     */
    private List<CoachingRelation> mDatasetPendingLr;
    private List<CoachingRelation> mDatasetRelations;
    private Context mCtx;

    private static final int HEADER_COACH = 0;
    private static final int LIST_COACH = 1;
    private static final int HEADER_LEARNER = 2;
    private static final int HEADER_REQUEST_COACH = 3;
    private static final int HEADER_REQUEST_LEARNER = 4;
    private static final int LIST_LEARNER = 5;
    private static final int LIST_PENDING_LEARNER = 6;
    private static final int LIST_PENDING_COACH = 7;

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

    protected class HeaderViewHolder extends ViewHolder {
        protected TextView mTitleTv;

        public HeaderViewHolder(View v) {
            super(v);
            this.mTitleTv = (TextView) v.findViewById(R.id.header);
        }
    }

    public RelationsListAdapter(Context ctx) {
        mCtx = ctx;
        mDatasetCr = new ArrayList<>();
        mDatasetLr = new ArrayList<>();
        mDatasetPendingCr = new ArrayList<>();
        mDatasetPendingLr = new ArrayList<>();
        mDatasetRelations = new ArrayList<>();

        mCurrentUserId = SharedPrefUtil.getConnectedUserId(mCtx);
    }

    public void setData(List<CoachingRelation> dataset) {
        clearData();

        mCurrentUserId = SharedPrefUtil.getConnectedUserId(mCtx);

        for (CoachingRelation relation : dataset) {
           if(relation.mActive) {
                if (!relation.mIsPending) {
                    if (relation.mCoach.mIdDb != mCurrentUserId) {
                        mDatasetCr.add(relation);
                    } else {
                        mDatasetLr.add(relation);
                    }
                } else {
                    if (relation.mCoach.mIdDb != mCurrentUserId) {
                        mDatasetPendingCr.add(relation);
                    } else {
                        mDatasetPendingLr.add(relation);
                    }
                }
           }
        }

        mDatasetRelations.addAll(mDatasetCr);
        mDatasetRelations.addAll(mDatasetLr);
        mDatasetRelations.addAll(mDatasetPendingCr);
        mDatasetRelations.addAll(mDatasetPendingLr);

        notifyDataSetChanged();
    }

    public void clearData() {
        mDatasetCr.clear();
        mDatasetLr.clear();
        mDatasetPendingCr.clear();
        mDatasetPendingLr.clear();
        mDatasetRelations.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case HEADER_REQUEST_LEARNER:
            case HEADER_REQUEST_COACH:
            case HEADER_LEARNER:
            case HEADER_COACH:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.group_header_item, parent, false);
                return new HeaderViewHolder(v);
            default:
            case LIST_PENDING_COACH:
            case LIST_PENDING_LEARNER:
            case LIST_LEARNER:
            case LIST_COACH: // normal list - Coach
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_relation, parent, false);
                final CoachViewHolder cvh = new CoachViewHolder(v);
                cvh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            int pos = cvh.getAdapterPosition();
                            mOnItemClickListener.onItemClick(v, pos);
                        }
                    }
                });
                cvh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOnItemClickListener != null) {
                            int pos = cvh.getAdapterPosition();
                            mOnItemClickListener.onItemLongClick(v, pos);
                            return true;
                        } else
                            return false;
                    }
                });

                return cvh;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        int itemViewType = vh.getItemViewType();

        if (itemViewType == LIST_COACH || itemViewType == LIST_LEARNER
                || itemViewType == LIST_PENDING_COACH || itemViewType == LIST_PENDING_LEARNER) {

            final CoachViewHolder cvh = (CoachViewHolder) vh;
            CoachingRelation cr = getItem(itemViewType, position);
            UserProfile partner = (itemViewType == LIST_COACH || itemViewType == LIST_PENDING_COACH) ? cr.mCoach : cr.mTrainee;

            Picasso.with(mCtx).load(partner.mPicture).into(cvh.mPictureIV);
            cvh.mNameTV.setText(partner.mDisplayName);
            cvh.mDescTV.setText(mCtx.getString(R.string.separator_strings, partner.mCity, cr.mSport.mName));
        } else {
            HeaderViewHolder hvh = (HeaderViewHolder) vh;
            int resId;
            switch (itemViewType) {
                case HEADER_COACH:
                    resId = R.string.my_coachs;
                    break;
                case HEADER_LEARNER:
                    resId = R.string.my_trainees;
                    break;
                case HEADER_REQUEST_COACH:
                    resId = R.string.pending_coach_request;
                    break;
                default:
                case HEADER_REQUEST_LEARNER:
                    resId = R.string.pending_trainee_request;
                    break;
            }
            hvh.mTitleTv.setText(resId);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER_COACH;
        else if (position > 0 && position <= mDatasetCr.size())
            return LIST_COACH;
        else if (position == mDatasetCr.size() + 1)
            return HEADER_LEARNER;
        else if (position > mDatasetCr.size() + 1 && position <= mDatasetCr.size() + (1 + mDatasetLr.size()))
            return LIST_LEARNER;
        else if (position == mDatasetCr.size() + (mDatasetLr.size() + 2))
            return HEADER_REQUEST_COACH;
        else if (position > mDatasetCr.size() + (mDatasetLr.size() + 2) && position <= (mDatasetCr.size() +
                mDatasetLr.size() + mDatasetPendingCr.size() + 2))
            return LIST_PENDING_COACH;
        else if (position == mDatasetCr.size() + mDatasetLr.size() + mDatasetPendingCr.size() + 3)
            return HEADER_REQUEST_LEARNER;
        else
            return LIST_PENDING_LEARNER;
    }

    private CoachingRelation getItem(int type, int position) {
        CoachingRelation cr;
        switch (type) {
            case LIST_COACH:
                cr = mDatasetCr.get(position - 1);
                break;
            case LIST_LEARNER:
                cr = mDatasetLr.get(position - (mDatasetCr.size() + 2));
                break;
            case LIST_PENDING_COACH:
                cr = mDatasetPendingCr.get(position - (mDatasetCr.size() + mDatasetLr.size() + 3));
                break;
            case LIST_PENDING_LEARNER:
                cr = mDatasetPendingLr.get(position - (mDatasetCr.size() + mDatasetLr.size() + mDatasetPendingCr.size() + 4));
                break;
            default:
                cr = null;
        }
        return cr;
    }

    public long getRelationId(int position) {
        int index;
        if (position <= mDatasetCr.size()) {
            index = position - 1;
        } else if (position <= (mDatasetCr.size() + mDatasetLr.size() + 1)) {
            index = position - 2;
        } else if (position <= (mDatasetCr.size() + mDatasetLr.size() + mDatasetPendingCr.size() + 2)) {
            index = position - 3;
        } else {
            index = position - 4;
        }
        CoachingRelation rel = mDatasetRelations.get(index);
        return rel.mIdDb;
    }

    @Override
    public int getItemCount() {
        return mDatasetCr.size() + mDatasetLr.size() +
                mDatasetPendingCr.size() + mDatasetPendingLr.size() + 4;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    /**
     * 处理item的点击事件和长按事件
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

}
