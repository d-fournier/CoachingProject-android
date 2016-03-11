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
 * Created by Benjamin on 01/03/2016.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {


    private static final int HEADER_GROUP = 0;
    private static final int LIST_GROUP = 1;
    private static final int HEADER_INVITATIONS = 2;
    private static final int LIST_INVITATIONS = 3;

    private List<Group> mGroupList;
    private List<Group> mInvitationsList;
    private OnItemClickListener mOnItemClickListener = null;
    private Context mCtx;

    public GroupAdapter(Context ctx) {
        mCtx = ctx;
        mGroupList = new ArrayList<>();
        mInvitationsList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {

            case LIST_GROUP:
            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group, parent, false);
                final GroupViewHolder gvh = new GroupViewHolder(v);
                gvh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            int pos = gvh.getAdapterPosition();
                            mOnItemClickListener.onItemClick(v, pos);
                        }
                    }
                });
                return gvh;
            case LIST_INVITATIONS:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_invitation, parent, false);
                final InvitationViewHolder ivh = new InvitationViewHolder(v);
                ivh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            int pos = ivh.getAdapterPosition();
                            mOnItemClickListener.onItemClick(v, pos);
                        }
                    }
                });
                return ivh;
            case HEADER_GROUP:
            case HEADER_INVITATIONS:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.group_header_item, parent, false);
                return new HeaderViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int itemViewType = holder.getItemViewType();

        if (itemViewType == LIST_GROUP) {//Liste des groupes
            final GroupViewHolder gvh = (GroupViewHolder) holder;
            final Group g = getItem(itemViewType, position);
            gvh.name.setText(g.mName);
            gvh.description.setText(g.mDescription);
            gvh.sport.setText(g.mSport.mName);
            gvh.members.setText(String.valueOf(g.mMembers));
        } else if (itemViewType == LIST_INVITATIONS) {//Liste des invitations
            final InvitationViewHolder ivh = (InvitationViewHolder) holder;
            final Group g = getItem(itemViewType, position);
            ivh.name.setText(g.mName);
            ivh.description.setText(g.mDescription);
            ivh.sport.setText(g.mSport.mName);
            ivh.members.setText(String.valueOf(g.mMembers));
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

        } else {//Headers
            HeaderViewHolder hvh = (HeaderViewHolder) holder;
            int resId;
            switch (itemViewType) {
                case HEADER_GROUP:
                    resId = R.string.groups;
                    break;
                default:
                case HEADER_INVITATIONS:
                    resId = R.string.invitations;
                    break;
            }
            hvh.mTitle.setText(resId);
        }

    }

    @Override
    public int getItemCount() {
        return mGroupList.size() + mInvitationsList.size() + 2;
    }


    public void setGroups(List<Group> gList) {
        mGroupList.addAll(gList);
        notifyDataSetChanged();
    }

    public void clearGroups() {
        mGroupList.clear();
        notifyDataSetChanged();
    }

    public void setInvitations(List<Group> gList) {
        mInvitationsList.addAll(gList);
        notifyDataSetChanged();
    }

    public void clearInvitations() {
        mInvitationsList.clear();
        notifyDataSetChanged();
    }

    private Group getItem(int type, int position) {
        Group g;
        switch (type) {
            case LIST_INVITATIONS:
                g = mInvitationsList.get(position - 1);
                break;
            case LIST_GROUP:
                g = mGroupList.get(position - (mInvitationsList.size() + 2));
                break;
            default:
                g = null;
        }
        return g;
    }

    public long getGroupId(int position) {
        if (position <= mInvitationsList.size())
            return mInvitationsList.get(position - 1).mIdDb;
        else if (position <= (mInvitationsList.size() + mGroupList.size() + 1)) {
            return mGroupList.get(position - mInvitationsList.size() - 2).mIdDb;
        }
        return -1;
    }

    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER_INVITATIONS;
        else if (position > 0 && position <= mInvitationsList.size())
            return LIST_INVITATIONS;
        else if (position == mInvitationsList.size() + 1)
            return HEADER_GROUP;
        else
            return LIST_GROUP;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
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

    static class InvitationViewHolder extends ViewHolder {
        protected ImageButton mAcceptButton;
        protected ImageButton mRefuseButton;
        TextView name;
        TextView description;
        TextView sport;
        LinearLayout linear_layout;
        TextView members;

        public InvitationViewHolder(View itemView) {

            super(itemView);
            name = (TextView) itemView.findViewById(R.id.group_item_name);
            description = (TextView) itemView.findViewById(R.id.group_item_description);
            sport = (TextView) itemView.findViewById(R.id.group_item_sport);
            linear_layout = (LinearLayout) itemView.findViewById(R.id.group_item_linear_layout);
            members = (TextView) itemView.findViewById(R.id.group_item_members);
            this.mAcceptButton = (ImageButton) itemView.findViewById(R.id.button_accept_invite);
            this.mRefuseButton = (ImageButton) itemView.findViewById(R.id.button_refuse_invite);
        }
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

}
