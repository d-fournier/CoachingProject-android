package fr.sims.coachingproject.loader.network;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import fr.sims.coachingproject.model.BlogPost;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by Benjamin on 16/02/2016.
 */
public class BlogPostLoader extends AsyncTaskLoader<BlogPost> {

    BlogPost mPost;
    private long mPostId;

    public BlogPostLoader(Context context, long postId) {
        super(context);
        mPost = null;
        mPostId = postId;
    }

    @Override
    public BlogPost loadInBackground() {
        String request = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.BLOG + mPostId;
        NetworkUtil.Response response = NetworkUtil.get(request, SharedPrefUtil.getConnectedToken(getContext()));
        if(response.isSuccessful())
            return BlogPost.parseItem(response.getBody());
        else
            return null;
}


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mPost == null)
            forceLoad();
        else
            deliverResult(mPost);
    }

    @Override
    public void deliverResult(BlogPost data) {
        mPost = data;
        super.deliverResult(data);
    }
}
