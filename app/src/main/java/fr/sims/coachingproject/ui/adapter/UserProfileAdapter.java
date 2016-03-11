package fr.sims.coachingproject.ui.adapter;

import android.content.Context;
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
import fr.sims.coachingproject.util.ImageUtil;

/**
 * Created by Anthony Barbosa on 17/02/2016.
 */

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.ViewHolder> {

    private List<UserProfile> userList;
    private OnItemClickListener mOnItemClickListener;
    private Context mCtx;

    public UserProfileAdapter(Context context) {
        mCtx=context;
        this.userList = new ArrayList<>();
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_user, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder vh, int position) {
        UserProfile user;
        user = userList.get(position);

        ImageUtil.loadProfilePicture(mCtx, user.mPicture, vh.mPictureIV);
        vh.mNameTV.setText(user.mDisplayName);
        vh.mDescTV.setText(user.mCity);

        if (mOnItemClickListener != null) {
            if (!vh.itemView.hasOnClickListeners()) {
                vh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = vh.getAdapterPosition();
                        mOnItemClickListener.onItemClick(v, pos);
                    }
                });
                vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = vh.getAdapterPosition();
                        mOnItemClickListener.onItemLongClick(v, pos);
                        return true;
                    }
                });
            }
        }


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
    public int getItemCount() {
        return userList.size();
    }

    public long getItemId(int position){
        return userList.get(position).mIdDb;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onItemLongClick(View view, int position);
    }

}
