package fr.sims.coachingproject.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.Group;

/**
 * Created by Benjamin on 01/03/2016.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> implements View.OnClickListener {

    private static final int LIST_GROUP_TYPE = 0;

    private List<Group> mGroupList;

    public GroupAdapter() {
        mGroupList = new ArrayList<>();
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;


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

                LinearLayout ll = (LinearLayout) v.findViewById(R.id.group_item_linear_layout);
                ll.setOnClickListener(this);

                break;
        }
        return vh;
    }

    @Override
    public void onClick (View v){
        // use getTag() to pass clicked item IdDb
        if (mOnItemClickListener != null)
            mOnItemClickListener.onItemClick(v, v.getTag().toString());
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Group g = mGroupList.get(position);
        holder.name.setText(g.mName);
        holder.description.setText(g.mDescription);
        holder.sport.setText(g.mSport.mName);
        holder.linear_layout.setTag(g.mIdDb);
        holder.members.setText(String.valueOf(g.mMembers));
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
        LinearLayout linear_layout;
        TextView members;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.group_item_name);
            description = (TextView) itemView.findViewById(R.id.group_item_description);
            sport = (TextView) itemView.findViewById(R.id.group_item_sport);
            linear_layout = (LinearLayout) itemView.findViewById(R.id.group_item_linear_layout);
            members = (TextView) itemView.findViewById(R.id.group_item_members);
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , String data);
    }

}
