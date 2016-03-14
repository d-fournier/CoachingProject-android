package fr.sims.coachingproject.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sims.coachingproject.model.BlogPost;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by Benjamin on 16/02/2016.
 */
public class BlogPostListLoader extends AsyncTaskLoader<List<BlogPost>> {

    List<BlogPost> mPostList;
    private long mUserId;

    public BlogPostListLoader(Context context, long userId) {
        super(context);
        mPostList = null;
        mUserId = userId;
    }

    @Override
    public List<BlogPost> loadInBackground() {
        String request = Const.WebServer.DOMAIN_NAME + Const.WebServer.API;
        if (mUserId != -1) {
            request += Const.WebServer.USER_PROFILE + mUserId + Const.WebServer.SEPARATOR + Const.WebServer.BLOG_EXTENSION + Const.WebServer.SEPARATOR;
            NetworkUtil.Response response = NetworkUtil.get(request, SharedPrefUtil.getConnectedToken(getContext()));
            if(response.isSuccessful())
                return Arrays.asList(BlogPost.parseList(response.getBody()));
            else
                return null;
        }else
            return new ArrayList<>();
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mPostList == null)
            forceLoad();
        else
            deliverResult(mPostList);
    }

    @Override
    public void deliverResult(List<BlogPost> data) {
        mPostList = data;
        super.deliverResult(data);
    }
}
