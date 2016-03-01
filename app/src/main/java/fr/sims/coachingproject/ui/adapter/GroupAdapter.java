package fr.sims.coachingproject.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.model.Group;

/**
 * Created by Benjamin on 01/03/2016.
 */
public class GroupAdapter extends RecyclerView.Adapter<CoachListAdapter.ViewHolder> {
    List<Group> mGroupList;
    Context mCtx;

    public GroupAdapter(Context ctx) {
        ctx = mCtx;
        mGroupList = new ArrayList<>();
    }

    @Override
    public CoachListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(CoachListAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mGroupList.size();
    }

    public void setData(List<Group> data){
        mGroupList.clear();
        mGroupList.addAll(data);
        notifyDataSetChanged();
    }

    public void clearData() {
        mGroupList.clear();
        notifyDataSetChanged();
    }


}
