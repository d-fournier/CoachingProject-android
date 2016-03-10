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
import fr.sims.coachingproject.model.UserProfile;

/**
 * Created by Benjamin on 10/03/2016.
 */
public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.ViewHolder> {


    private static final int HEADER_MEMBERS = 0;
    private static final int LIST_MEMBERS = 1;
    private static final int HEADER_PENDING_MEMBERS = 2;
    private static final int LIST_PENDING_MEMBERS = 3;
    private List<UserProfile> mMembersList;
    private List<UserProfile> mPendingMembersList;
    private Context mCtx;
    private OnItemClickListener mOnItemClickListener;

    public GroupMembersAdapter(Context ctx) {
        mCtx = ctx;
        mMembersList = new ArrayList<>();
        mPendingMembersList = new ArrayList<>();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case HEADER_MEMBERS:
            case HEADER_PENDING_MEMBERS:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.group_header_item, parent, false);
                return new HeaderViewHolder(v);
            default:
            case LIST_MEMBERS:
            case LIST_PENDING_MEMBERS:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_user, parent, false);
                final MemberViewHolder mvh = new MemberViewHolder(v);
                mvh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            int pos = mvh.getAdapterPosition();
                            mOnItemClickListener.onItemClick(v, pos);
                        }
                    }
                });
                return mvh;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int itemViewType = holder.getItemViewType();

        if (itemViewType == LIST_MEMBERS || itemViewType == LIST_PENDING_MEMBERS) {

            final MemberViewHolder mvh = (MemberViewHolder) holder;
            UserProfile up = getItem(itemViewType, position);

            Picasso.with(mCtx).load(up.mPicture).into(mvh.mPictureIV);
            mvh.mNameTV.setText(up.mDisplayName);
            mvh.mDescTV.setText(up.mCity);
        } else {
            HeaderViewHolder hvh = (HeaderViewHolder) holder;
            int resId;
            switch (itemViewType) {
                case HEADER_MEMBERS:
                    resId = R.string.members;
                    break;
                default:
                case HEADER_PENDING_MEMBERS:
                    resId = R.string.pending_members;
                    break;
            }
            hvh.mTitle.setText(resId);
        }
    }

    private UserProfile getItem(int type, int position) {
        UserProfile up;
        switch (type) {
            case LIST_MEMBERS:
                up = mMembersList.get(position - 1);
                break;
            case LIST_PENDING_MEMBERS:
                up = mPendingMembersList.get(position - (mMembersList.size() + 2));
                break;
            default:
                up = null;
        }
        return up;
    }

    public long getMemberId(int position) {
        if (position <= mMembersList.size())
            return mMembersList.get(position - 1).mIdDb;
        else if (position <= (mMembersList.size() + mPendingMembersList.size() + 1)) {
            return mPendingMembersList.get(position - mMembersList.size() - 2).mIdDb;
        }
        return -1;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER_MEMBERS;
        else if (position > 0 && position <= mMembersList.size())
            return LIST_MEMBERS;
        else if (position == mMembersList.size() + 1)
            return HEADER_PENDING_MEMBERS;
        else
            return LIST_PENDING_MEMBERS;
    }

    @Override
    public int getItemCount() {
        return mMembersList.size() + mPendingMembersList.size() + 2;//2 Headers
    }

    public void clearMembers() {
        mMembersList.clear();
        notifyDataSetChanged();
    }

    public void setMembers(List<UserProfile> members) {
        mMembersList.addAll(members);
        notifyDataSetChanged();
    }

    public void clearPendingMembers() {
        mPendingMembersList.clear();
        notifyDataSetChanged();
    }

    public void setPendingMembers(List<UserProfile> pendingMembers) {
        mPendingMembersList.addAll(pendingMembers);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

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
}
