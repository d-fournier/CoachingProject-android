package fr.sims.coachingproject.loader.network;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;

/**
 * Created by Benjamin on 01/03/2016.
 */
public class GroupSearchLoader extends AsyncTaskLoader<List<Group>> {

    private String mKeywords;
    private String mCity;
    private long mSport;

    public GroupSearchLoader(Context context, String keywords, long sport, String city) {
        super(context);
        mKeywords = keywords;
        mSport = sport;
        mCity = city;
    }

    @Override
    public List<Group> loadInBackground() {
        String request = null;
        try {
            request = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS + "?";
            boolean queryStarted = false;
            if (!mKeywords.isEmpty()) {
                request += Const.WebServer.KEYWORDS_PARAMETER + "=" + URLEncoder.encode(mKeywords, "UTF-8");
                queryStarted=true;
            }
            if (!mCity.isEmpty()) {
                request += queryStarted ? "&" : "";
                request += Const.WebServer.CITY_PARAMETER + "=" + URLEncoder.encode(mCity, "UTF-8");
                queryStarted=true;
            }
            if (mSport != -1) {
                request += queryStarted ? "&" : "";
                request += Const.WebServer.SPORT_PARAMETER + "=" + mSport;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        NetworkUtil.Response response = NetworkUtil.get(request, null);

        if(response.isSuccessful()){
            return Arrays.asList(Group.parseList(response.getBody()));
        }else{
            return null;
        }

    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
