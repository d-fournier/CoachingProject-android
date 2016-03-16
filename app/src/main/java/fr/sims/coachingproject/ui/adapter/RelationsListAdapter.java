package fr.sims.coachingproject.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.util.ImageUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by Donovan on 16/03/2016.
 */
public class RelationsListAdapter extends SectionedRecyclerViewAdapter<RelationsListAdapter.ViewHolder> {

    private long mCurrentUserId;
    /**
     * My Coach
     */
    private List<CoachingRelation> mCoachList;
    private static final int COACH_LIST = 0;
    /**
     * My Trainee
     */
    private List<CoachingRelation> mTraineeList;
    private static final int TRAINEE_LIST = 1;

    /**
     * Pending Request to coach
     */
    private List<CoachingRelation> mRequestToCoachList;
    private static final int REQUEST_TO_COACH_LIST = 2;
    /**
     * Pending Request from trainee
     */
    private List<CoachingRelation> mRequestFromTraineeList;
    private static final int REQUEST_FROM_TRAINEE_LIST = 3;


    private Context mCtx;
    private OnRelationClickListener mListener;

    protected class ViewHolder extends RecyclerView.ViewHolder {
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
        mCoachList = new ArrayList<>();
        mTraineeList = new ArrayList<>();
        mRequestFromTraineeList = new ArrayList<>();
        mRequestToCoachList = new ArrayList<>();

        mCurrentUserId = SharedPrefUtil.getConnectedUserId(mCtx);
    }

    @Override
    public int getSectionCount() {
        return 4;
    }

    @Override
    public int getItemCount(int section) {
        int total = 0;
        switch (section) {
            case COACH_LIST:
                total = mCoachList.size();
                break;
            case TRAINEE_LIST:
                total = mTraineeList.size();
                break;
            case REQUEST_FROM_TRAINEE_LIST:
                total = mRequestFromTraineeList.size();
                break;
            case REQUEST_TO_COACH_LIST:
                total = mRequestToCoachList.size();
                break;
        }
        return total;
    }

    @Override
    public void onBindHeaderViewHolder(ViewHolder viewHolder, int section) {
        HeaderViewHolder vh = (HeaderViewHolder) viewHolder;
        int resId;
        switch (section) {
            case COACH_LIST:
                resId = R.string.my_coachs;
                break;
            case TRAINEE_LIST:
                resId = R.string.my_trainees;
                break;
            case REQUEST_FROM_TRAINEE_LIST:
                resId = R.string.pending_trainee_request;
                break;
            default:
            case REQUEST_TO_COACH_LIST:
                resId = R.string.pending_coach_request;
                break;
        }
        vh.mTitleTv.setText(resId);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int section, final int relativePosition, int absolutePosition) {
        CoachViewHolder vh = (CoachViewHolder) viewHolder;
        final CoachingRelation cr = getItem(section, relativePosition);

        UserProfile partner = (section == COACH_LIST || section == REQUEST_TO_COACH_LIST) ? cr.mCoach : cr.mTrainee;
        ImageUtil.loadProfilePicture(mCtx, partner.mPicture, vh.mPictureIV);
        vh.mNameTV.setText(partner.mDisplayName);
        vh.mDescTV.setText(mCtx.getString(R.string.separator_strings, partner.mCity, cr.mSport.mName));

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null)
                    mListener.onRelationClick(v, cr.mIdDb);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh;
        if (viewType == VIEW_TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_header_item, parent, false);
            vh = new HeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_relation, parent, false);
            vh = new CoachViewHolder(v);
        }
        return vh;
    }

    private CoachingRelation getItem(int section, int relativePosition) {
        CoachingRelation cr = null;
        switch (section) {
            case COACH_LIST:
                cr = mCoachList.get(relativePosition);
                break;
            case TRAINEE_LIST:
                cr = mTraineeList.get(relativePosition);
                break;
            case REQUEST_FROM_TRAINEE_LIST:
                cr = mRequestFromTraineeList.get(relativePosition);
                break;
            case REQUEST_TO_COACH_LIST:
                cr = mRequestToCoachList.get(relativePosition);
                break;
        }
        return cr;
    }

    /*
        Data Management
     */
    public void setData(List<CoachingRelation> dataset) {
        clearData();
        mCurrentUserId = SharedPrefUtil.getConnectedUserId(mCtx);

        if(mCurrentUserId!=-1) {
            for (CoachingRelation relation : dataset) {
                if (relation.mActive) {
                    if (!relation.mIsPending) {
                        if (relation.mCoach.mIdDb != mCurrentUserId) {
                            mCoachList.add(relation);
                        } else {
                            mTraineeList.add(relation);
                        }
                    } else {
                        if (relation.mCoach.mIdDb != mCurrentUserId) {
                            mRequestToCoachList.add(relation);
                        } else {
                            mRequestFromTraineeList.add(relation);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        mCoachList.clear();
        mTraineeList.clear();
        mRequestFromTraineeList.clear();
        mRequestToCoachList.clear();
        notifyDataSetChanged();
    }

    /*
        Listener
     */
    public void setOnRelationClickListener(OnRelationClickListener listener){
        mListener = listener;
    }

    public interface OnRelationClickListener {
        void onRelationClick(View view, long relationIdDb);
    }
}
