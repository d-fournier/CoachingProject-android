package fr.sims.coachingproject.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.model.UserProfile;

/**
 * Created by Anthony Barbosa on 17/02/2016.
 */

public class SearchGroupListAdapter extends RecyclerView.Adapter<SearchGroupListAdapter.ViewHolder> {

    private List<Group> groupList;
    private OnItemClickListener mOnItemClickListener;
    private Context mCtx;

    public SearchGroupListAdapter(Context context) {
        mCtx = context;
        this.groupList = new ArrayList<>();
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView mSportTv;
        protected TextView mNameTV;
        protected TextView mDescTV;

        public ViewHolder(View v) {
            super(v);
            this.mSportTv = (TextView) v.findViewById(R.id.group_item_sport);
            this.mNameTV = (TextView) v.findViewById(R.id.group_item_name);
            this.mDescTV = (TextView) v.findViewById(R.id.group_item_description);
        }
    }


    public SearchGroupListAdapter() {
        this.groupList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_group, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder vh, int position) {
        Group group;
        group = groupList.get(position);
        vh.mSportTv.setText(group.mSport.mName);
        vh.mNameTV.setText(group.mName);
        vh.mDescTV.setText(group.mDescription);
    }

    public void setData(List<Group> dataset) {
        groupList = dataset;
        notifyDataSetChanged();
    }

    public void clearData() {
        groupList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onItemLongClick(View view, int position);
    }

}
