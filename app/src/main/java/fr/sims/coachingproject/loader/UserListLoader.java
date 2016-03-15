package fr.sims.coachingproject.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;

/**
 * Created by Benjamin on 16/02/2016.
 */
public class UserListLoader extends AsyncTaskLoader<List<UserProfile>> {

    private String mKeywords;
    private long mSport;
    private long mLevel;
    private boolean mCoach;

    public UserListLoader(Context context, String keywords, long sport, long level, boolean coach) {
        super(context);
        mKeywords = keywords;
        mSport = sport;
        mLevel =level;
        mCoach = coach;
    }

    @Override
    public List<UserProfile> loadInBackground() {
        String request = null;
        boolean queryStarted = false;
        try {
            request = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.USER_PROFILE;

            if(mCoach){
                request+= "?" +  Const.WebServer.COACH_PARAMETER + "=true";
                queryStarted=true;
            }
            if(!mKeywords.isEmpty()){
                request += (queryStarted ? "&" : "?") + Const.WebServer.KEYWORDS_PARAMETER + "=" + URLEncoder.encode(mKeywords, "UTF-8");
                queryStarted=true;
            }
            if(mSport != -1){
                request +=  (queryStarted ? "&" : "?") + Const.WebServer.SPORT_PARAMETER + "=" + mSport;
                queryStarted = true;
            }
            if(mLevel != -1) {
                request += (queryStarted ? "&" : "?") + Const.WebServer.LEVEL_PARAMETER + "=" + mLevel;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        NetworkUtil.Response response = NetworkUtil.get(request, null);
        if(response.getReturnCode()==NetworkUtil.Response.UNKNOWN_HOST_ERROR){
            return null;
        }else {
            return Arrays.asList(UserProfile.parseList(response.getBody()));
        }

    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }


}
