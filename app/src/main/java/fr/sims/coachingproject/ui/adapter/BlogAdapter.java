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
import fr.sims.coachingproject.model.BlogPost;

/**
 * Created by dfour on 13/03/2016.
 */
public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {

    private static final int POST_TYPE = 0;
    private static final int HEADER_TYPE = 1;

    private View mHeader;
    private List<BlogPost> mBlogPostList;
    private Context mCtx;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class BlogPostViewHolder extends BlogAdapter.ViewHolder {
        private ImageView mPicture;
        private TextView mTitle;
        private TextView mShortDescription;

        public BlogPostViewHolder(View itemView) {
            super(itemView);
            mPicture = (ImageView) itemView.findViewById(R.id.post_picture);
            mTitle = (TextView) itemView.findViewById(R.id.post_title);
            mShortDescription = (TextView) itemView.findViewById(R.id.post_desc);
        }
    }

    public BlogAdapter(Context ctx) {
        mBlogPostList = new ArrayList<>();
        mCtx = ctx;
    }

    @Override
    public BlogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh;
        switch (viewType) {
            case HEADER_TYPE:
                vh = new ViewHolder(mHeader);
                break;
            case POST_TYPE:
            default:
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_blog_post, parent, false);
                vh = new BlogPostViewHolder(v);
                break;
        }
        return vh;

    }

    @Override
    public void onBindViewHolder(BlogAdapter.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case POST_TYPE:
            default:
                BlogPost bp = getItem(position);
                BlogPostViewHolder bvh = (BlogPostViewHolder) holder;
                bvh.mTitle.setText(bp.mTitle);
                bvh.mShortDescription.setText(bp.mShortDescription);
                if(bp.mPicture != null) {
                    bvh.mPicture.setVisibility(View.VISIBLE);
                    Picasso.with(mCtx).load(bp.mPicture).into(bvh.mPicture);
                } else {
                    bvh.mPicture.setVisibility(View.GONE);
                }
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mHeader != null ? mBlogPostList.size() + 1 : mBlogPostList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mHeader != null && position == 0)
            return HEADER_TYPE;
        else
            return POST_TYPE;
    }

    public BlogPost getItem(int position) {
        if(mHeader != null && position <= mBlogPostList.size())
            return mBlogPostList.get(position - 1);
        else if(position < mBlogPostList.size())
            return mBlogPostList.get(position);
        else
            return null;
    }

    public void setData(List<BlogPost> data) {
        mBlogPostList.clear();
        mBlogPostList.addAll(data);
        this.notifyDataSetChanged();
    }

    public void setHeader(View view){
        mHeader = view;
        this.notifyDataSetChanged();
    }
}
