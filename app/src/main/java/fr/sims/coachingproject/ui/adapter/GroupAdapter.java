package fr.sims.coachingproject.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.service.NetworkService;

/**
 * Created by Donovan on 16/03/2016.
 */
public class GroupAdapter extends SectionedRecyclerViewAdapter<GroupAdapter.ViewHolder> {


    private static final int LIST_INVITATIONS = 0;
    private List<Group> mInvitationsList;

    private static final int LIST_GROUP = 1;
    private List<Group> mGroupList;

    private OnGroupClickListener mListener = null;
    private long mCurrentUserId;
    private Context mCtx;

    public GroupAdapter(Context ctx) {
        mCtx = ctx;
        mGroupList = new ArrayList<>();
        mInvitationsList = new ArrayList<>();
    }

    @Override
    public int getSectionCount() {
        return 2;
    }

    @Override
    public int getItemCount(int section) {
        int total = 0;
        switch (section) {
            case LIST_INVITATIONS:
                total = mInvitationsList.size();
                break;
            case LIST_GROUP:
                total = mGroupList.size();
                break;
        }
        return total;
    }

    @Override
    public int getItemViewType(int section, int relativePosition, int absolutePosition) {
        return (section == LIST_INVITATIONS ? LIST_INVITATIONS : LIST_GROUP);
    }

    @Override
    public void onBindHeaderViewHolder(GroupAdapter.ViewHolder holder, int section) {
        HeaderViewHolder vh = (HeaderViewHolder) holder;
        int resId = -1;
        switch (section) {
            case LIST_GROUP:
                resId = R.string.groups;
                break;
            case LIST_INVITATIONS:
                resId = R.string.invitations;
                break;
        }
        vh.mTitle.setText(resId);
    }

    @Override
    public void onBindViewHolder(GroupAdapter.ViewHolder holder, int section, int relativePosition, int absolutePosition) {
        GroupViewHolder gvh = (GroupViewHolder) holder;
        final Group g = getItem(section, relativePosition);
        gvh.name.setText(g.mName);
        gvh.description.setText(g.mDescription);
        gvh.sport.setText(g.mSport.mName);
        gvh.members.setText(String.valueOf(g.mMembers));
        gvh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onGroupClick(v, g.mIdDb);
                }
            }
        });

        if(section == LIST_INVITATIONS) {
            final InvitationViewHolder ivh = (InvitationViewHolder) holder;
            ivh.mAcceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivh.mAcceptButton.setEnabled(false);
                    ivh.mRefuseButton.setEnabled(false);
                    NetworkService.startActionInvitationUserGroups(mCtx, g.mIdDb, true);
                }
            });
            ivh.mRefuseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivh.mAcceptButton.setEnabled(false);
                    ivh.mRefuseButton.setEnabled(false);
                    NetworkService.startActionInvitationUserGroups(mCtx, g.mIdDb, false);
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
        } else if(viewType == LIST_INVITATIONS){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_group_invitation, parent, false);
            vh = new InvitationViewHolder(v);
        } else if(viewType == LIST_GROUP){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_group, parent, false);
            vh = new GroupViewHolder(v);
        }
        return vh;
    }

    private Group getItem(int section, int relativePosition) {
        Group gr = null;
        switch (section) {
            case LIST_INVITATIONS:
                gr = mInvitationsList.get(relativePosition);
                break;
            case LIST_GROUP:
                gr = mGroupList.get(relativePosition);
                break;
        }
        return gr;
    }

    /*
        Data management
     */
    public void setGroups(List<Group> gList) {
        mGroupList.clear();
        mGroupList.addAll(gList);
        notifyDataSetChanged();
    }

    public void clearGroups() {
        mGroupList.clear();
        notifyDataSetChanged();
    }

    public void setInvitations(List<Group> gList) {
        mInvitationsList.clear();
        mInvitationsList.addAll(gList);
        notifyDataSetChanged();
    }

    public void clearInvitations() {
        mInvitationsList.clear();
        notifyDataSetChanged();
    }



    /*
        Listener
     */
    public void setOnGroupClickListener(OnGroupClickListener listener){
        mListener = listener;
    }

    public interface OnGroupClickListener {
        void onGroupClick(View view, long groupDbId);
    }


    /*
        ViewHolder
     */

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }
    static class GroupViewHolder extends ViewHolder {
        TextView name;
        TextView description;
        TextView sport;
        LinearLayout linear_layout;
        TextView members;
        public GroupViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.group_item_name);
            description = (TextView) itemView.findViewById(R.id.group_item_description);
            sport = (TextView) itemView.findViewById(R.id.group_item_sport);
            linear_layout = (LinearLayout) itemView.findViewById(R.id.group_item_linear_layout);
            members = (TextView) itemView.findViewById(R.id.group_item_members);
        }
    }

    static class InvitationViewHolder extends GroupViewHolder {
        protected ImageButton mAcceptButton;
        protected ImageButton mRefuseButton;
        public InvitationViewHolder(View itemView) {
            super(itemView);
            this.mAcceptButton = (ImageButton) itemView.findViewById(R.id.button_accept_invite);
            this.mRefuseButton = (ImageButton) itemView.findViewById(R.id.button_refuse_invite);
        }
    }
    protected class HeaderViewHolder extends ViewHolder {
        protected TextView mTitle;
        public HeaderViewHolder(View v) {
            super(v);
            this.mTitle = (TextView) v.findViewById(R.id.header);
        }
    }
}
