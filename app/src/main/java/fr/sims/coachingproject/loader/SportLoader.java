package fr.sims.coachingproject.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;

/**
 * Created by Benjamin on 16/02/2016.
 */
public class SportLoader extends AsyncTaskLoader<List<Sport>> {

    List<Sport> mSportList;


    public SportLoader(Context context) {
        super(context);
        mSportList = null;
    }

    @Override
    public List<Sport> loadInBackground() {
        String request = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.SPORTS ;
        String response = NetworkUtil.get(request,null);
        return Arrays.asList(Sport.parseList(response));
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if(mSportList == null) {
            forceLoad();
        }else{
            deliverResult(mSportList);
        }
    }

    @Override
    public void deliverResult(List<Sport> data) {
        mSportList = data;
        super.deliverResult(data);
    }
}
