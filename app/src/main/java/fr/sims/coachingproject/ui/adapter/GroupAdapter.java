package fr.sims.coachingproject.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.Group;

/**
 * Created by Benjamin on 01/03/2016.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private static final int LIST_GROUP_TYPE = 0;

    private List<Group> mGroupList;

    public GroupAdapter() {
        mGroupList = new ArrayList<>();
    }


    public void setData(List<Group> gList) {
        mGroupList.addAll(gList);
        notifyDataSetChanged();
    }

    public void clearData() {
        mGroupList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh;
        switch (viewType) {
            case LIST_GROUP_TYPE:
            default:
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group, parent, false);
                vh = new ViewHolder(v);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Group g = mGroupList.get(position);
        holder.name.setText(g.mName);
        holder.description.setText(g.mDescription);
        holder.sport.setText(g.mSport.mName);
        holder.members.setText(String.valueOf(g.mMembers.length));
    }

    @Override
    public int getItemCount() {
        return mGroupList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return LIST_GROUP_TYPE;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        TextView sport;
        TextView members;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.group_item_name);
            description = (TextView) itemView.findViewById(R.id.group_item_description);
            sport = (TextView) itemView.findViewById(R.id.group_item_sport);
            members = (TextView) itemView.findViewById(R.id.group_item_members);

        }
    }
}
