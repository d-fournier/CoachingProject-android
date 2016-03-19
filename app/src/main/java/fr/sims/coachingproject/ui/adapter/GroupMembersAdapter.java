package fr.sims.coachingproject.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.util.ImageUtil;

/**
 * Created by Donovan on 19/03/2016.
 */
public class GroupMembersAdapter extends SectionedRecyclerViewAdapter<GroupMembersAdapter.ViewHolder> {

    private List<UserProfile> mMembersList;
    private static final int MEMBERS_LIST = 0;

    private List<UserProfile> mPendingMembersList;
    private static final int PENDING_MEMBERS_LIST = 1;

    private OnUserClickListener mListener;

    private Context mCtx;
    /**
     * Group Id
     * Used to send accept/refuse request for the members
     */
    private long mGroupId;

    public GroupMembersAdapter(Context ctx, long groupId) {
        mCtx = ctx;
        mGroupId=groupId;
        mMembersList = new ArrayList<>();
        mPendingMembersList = new ArrayList<>();
    }


    @Override
    public int getSectionCount() {
        return 2;
    }

    @Override
    public int getItemCount(int section) {
        int total = 0;
        switch (section) {
            case MEMBERS_LIST:
                total = mMembersList.size();
                break;
            case PENDING_MEMBERS_LIST:
                total = mPendingMembersList.size();
                break;
        }
        return total;
    }

    @Override
    public int getItemViewType(int section, int relativePosition, int absolutePosition) {
        return section;
    }

    @Override
    public void onBindHeaderViewHolder(ViewHolder holder, int section) {
        HeaderViewHolder vh = (HeaderViewHolder) holder;
        int resId = -1;
        switch (section) {
            case MEMBERS_LIST:
                resId = R.string.members;
                break;
            case PENDING_MEMBERS_LIST:
                resId = R.string.pending_members;
                break;
        }
        vh.mTitle.setText(resId);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int section, int relativePosition, int absolutePosition) {
        MemberViewHolder mvh = (MemberViewHolder) holder;
        final UserProfile up = getItem(section, relativePosition);
        mvh.mNameTV.setText(up.mDisplayName);
        mvh.mDescTV.setText(up.mCity);
        ImageUtil.loadProfilePicture(mCtx, up.mPicture, mvh.mPictureIV);

        mvh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onUserClick(v, up.mIdDb);
                }
            }
        });

        if(section == PENDING_MEMBERS_LIST) {
            final PendingMemberViewHolder pmvh = (PendingMemberViewHolder) holder;
            pmvh.mAcceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pmvh.mAcceptButton.setEnabled(false);
                    pmvh.mRefuseButton.setEnabled(false);
                    NetworkService.startActionAcceptUserGroups(mCtx, new long[]{up.mIdDb}, mGroupId, true);
                }
            });
            pmvh.mRefuseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pmvh.mRefuseButton.setEnabled(false);
                    pmvh.mAcceptButton.setEnabled(false);
                    NetworkService.startActionAcceptUserGroups(mCtx,new long[]{up.mIdDb},mGroupId,false);
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh = null;
        View v;
        if(viewType == VIEW_TYPE_HEADER) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_header_item, parent, false);
            vh = new HeaderViewHolder(v);
        } else if(viewType == MEMBERS_LIST){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_user, parent, false);
            vh = new MemberViewHolder(v);
        } else if(viewType == PENDING_MEMBERS_LIST){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_pending_member, parent, false);
            vh = new PendingMemberViewHolder(v);
        }
        return vh;
    }


    /*
        Data management
     */
    public UserProfile getItem(int section, int position) {
        UserProfile up = null;
        switch (section) {
            case MEMBERS_LIST:
                up = mMembersList.get(position);
                break;
            case PENDING_MEMBERS_LIST:
                up = mPendingMembersList.get(position);
                break;
        }
        return up;
    }

    public void clearMembers() {
        mMembersList.clear();
        notifyDataSetChanged();
    }

    public void setMembers(List<UserProfile> members) {
        mMembersList.clear();
        mMembersList.addAll(members);
        notifyDataSetChanged();
    }

    public void clearPendingMembers() {
        mPendingMembersList.clear();
        notifyDataSetChanged();
    }

    public void setPendingMembers(List<UserProfile> pendingMembers) {
        mPendingMembersList.clear();
        mPendingMembersList.addAll(pendingMembers);
        notifyDataSetChanged();
    }

    /*
        View Holder
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    protected class HeaderViewHolder extends ViewHolder {
        protected TextView mTitle;

        public HeaderViewHolder(View v) {
            super(v);
            this.mTitle = (TextView) v.findViewById(R.id.header);
        }
    }

    protected class MemberViewHolder extends ViewHolder {
        protected ImageView mPictureIV;
        protected TextView mNameTV;
        protected TextView mDescTV;

        public MemberViewHolder(View v) {
            super(v);
            this.mPictureIV = (ImageView) v.findViewById(R.id.user_picture);
            this.mNameTV = (TextView) v.findViewById(R.id.user_name);
            this.mDescTV = (TextView) v.findViewById(R.id.user_description);
        }
    }

    protected class PendingMemberViewHolder extends MemberViewHolder {
        protected ImageButton mAcceptButton;
        protected ImageButton mRefuseButton;

        public PendingMemberViewHolder(View v) {
            super(v);
            this.mAcceptButton = (ImageButton) v.findViewById(R.id.button_accept_pending);
            this.mRefuseButton = (ImageButton) v.findViewById(R.id.button_refuse_pending);
        }
    }

    /*
        Listener
     */
    public void setOnUserClickListener(OnUserClickListener mOnUserClickListener) {
        this.mListener = mOnUserClickListener;
    }

    public interface OnUserClickListener {
        void onUserClick(View view, long userDbId);
    }

}
