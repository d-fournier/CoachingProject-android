package fr.sims.coachingproject.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.UserProfile;

/**
 * Created by dfour on 11/02/2016.
 */
public class CoachListAdapter extends RecyclerView.Adapter<CoachListAdapter.CoachViewHolder> {

    private List<UserProfile> mDataset;



    protected static class CoachViewHolder extends RecyclerView.ViewHolder {
        protected ImageView mPictureIV;
        protected TextView mNameTV;
        protected TextView mDescTV;

        public CoachViewHolder(View v) {
            super(v);
        }
    }

    public CoachListAdapter() {
        mDataset = new ArrayList<>();
    }

    public CoachListAdapter(List<UserProfile> dataset) {
        mDataset = dataset;
    }

    public void setData(List<UserProfile> dataset){
        mDataset = dataset;
        notifyDataSetChanged();
    }

    public void clearData(){
        mDataset.clear();
        notifyDataSetChanged();
    }

    @Override
    public CoachViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_coach, parent, false);
        CoachViewHolder vh = new CoachViewHolder(v);
        vh.mPictureIV = (ImageView) v.findViewById(R.id.user_picture);
        vh.mNameTV = (TextView) v.findViewById(R.id.user_name);
        vh.mDescTV = (TextView) v.findViewById(R.id.user_description);
        return vh;
    }

    @Override
    public void onBindViewHolder(CoachViewHolder vh, int position) {
        UserProfile cr = mDataset.get(position);
// TODO Image Loader        vh.mPictureIV
        vh.mNameTV.setText(cr.mName);
        vh.mDescTV.setText(cr.mCity);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
