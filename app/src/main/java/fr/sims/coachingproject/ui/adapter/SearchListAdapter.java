package fr.sims.coachingproject.ui.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import fr.sims.coachingproject.model.UserProfile;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.activity.SearchActivity;

/**
 * Created by Anthony Barbosa on 17/02/2016.
 */

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {

    private List<UserProfile> userList;

    private List<UserProfile> filteredUserList;

    public SearchListAdapter(Context context) {
        this.userList = new ArrayList<>();
        this.filteredUserList = new ArrayList<>();
    }



    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView mPictureIV;
        protected TextView mNameTV;
        protected TextView mDescTV;

        public ViewHolder(View v) {
            super(v);
            this.mPictureIV = (ImageView) v.findViewById(R.id.user_picture);
            this.mNameTV = (TextView) v.findViewById(R.id.user_name);
            this.mDescTV = (TextView) v.findViewById(R.id.user_description);
        }
    }


    public SearchListAdapter() {
        this.userList = new ArrayList<>();
        this.filteredUserList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_coach, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        UserProfile user;
        user = filteredUserList.get(position);
        vh.mNameTV.setText(user.mDisplayName);
        vh.mDescTV.setText(user.mCity);


    }

    public void setData(List<UserProfile> dataset) {
        userList = dataset;
        notifyDataSetChanged();
    }

    public void clearData() {
        userList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return filteredUserList.size();
    }


    public void setFilter(CharSequence cs) {
        filteredUserList = new ArrayList<>();
        final String filterPattern = cs.toString().toLowerCase();
        for ( UserProfile user : userList) {
            final String text = user.mDisplayName.toLowerCase();
            if(!filterPattern.isEmpty()) {
                if (text.contains(filterPattern))
                    filteredUserList.add(user);
            }
        }
        notifyDataSetChanged();
    }
}
