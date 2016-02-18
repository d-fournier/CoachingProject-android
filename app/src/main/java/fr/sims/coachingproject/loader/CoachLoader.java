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
public class CoachLoader extends AsyncTaskLoader<List<UserProfile>> {

    private String mKeywords;
    private long mSport;

    public CoachLoader(Context context, String keywords, long sport) {
        super(context);
        mKeywords = keywords;
        mSport = sport;
    }

    @Override
    public List<UserProfile> loadInBackground() {
        String request = null;
        try {
            request = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.USER_PROFILE + "?" +
                    Const.WebServer.COACH_PARAMETER + "=true";
            if(!mKeywords.isEmpty()){
                request += "&" + Const.WebServer.KEYWORDS_PARAMETER + "=" + URLEncoder.encode(mKeywords, "UTF-8");
            }
            if(mSport != -1){
                request +=  "&" + Const.WebServer.SPORT_PARAMETER + "=" + mSport;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String response = NetworkUtil.get(request, null);
        return Arrays.asList(UserProfile.parseList(response));
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }


}
