package fr.sims.coachingproject.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.network.BlogPostListLoader;
import fr.sims.coachingproject.model.BlogPost;
import fr.sims.coachingproject.ui.activity.PostReadActivity;
import fr.sims.coachingproject.ui.adapter.BlogListAdapter;
import fr.sims.coachingproject.util.Const;

/**
 * Created by dfour on 14/03/2016.
 */
public class BlogListFragment extends GenericFragment implements LoaderManager.LoaderCallbacks<List<BlogPost>>,BlogListAdapter.OnItemClickListener {

    // TODO Put it in resources
    public static final String TITLE = "Blog";
    private static final String EXTRA_USER_ID = "fr.sims.coachingproject.extra.USER_ID";

    private RecyclerView mBlogPostRV;
    private BlogListAdapter mAdapter;
    private long mUserId;

    public static BlogListFragment newInstance(long userId){
        BlogListFragment fragment = new BlogListFragment();
        Bundle args = new Bundle();
        args.putLong(EXTRA_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void bindArguments(Bundle args) {
        super.bindArguments(args);
        mUserId = args.getLong(EXTRA_USER_ID, -1);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blog;
    }

    @Override
    protected void bindView(View view) {
        super.bindView(view);
        mBlogPostRV = (RecyclerView) view.findViewById(R.id.blog_list);
        mBlogPostRV.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new BlogListAdapter(getContext());
        mAdapter.setOnItemClickListener(this);
        mBlogPostRV.setAdapter(mAdapter);
    }


    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        getLoaderManager().initLoader(Const.Loaders.BLOG_POSTS_LOADER_ID, null, this);
    }


    @Override
    public android.support.v4.content.Loader<List<BlogPost>> onCreateLoader(int id, Bundle args) {
        return new BlogPostListLoader(getContext(), mUserId);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<BlogPost>> loader, List<BlogPost> data) {
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<BlogPost>> loader) { }

    @Override
    public void onItemClick(View view, int position) {
        long id = mAdapter.getItem(position).mIdDb;
        PostReadActivity.startActivity(getContext(), id);
    }
}
