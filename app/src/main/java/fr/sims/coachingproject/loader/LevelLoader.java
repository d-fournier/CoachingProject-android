package fr.sims.coachingproject.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sims.coachingproject.model.SportLevel;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;

/**
 * Created by Benjamin on 16/02/2016.
 */
public class LevelLoader extends AsyncTaskLoader<List<SportLevel>> {

    List<SportLevel> mLevelsList;
    private long mSport;

    public LevelLoader(Context context, long sport) {
        super(context);
        mLevelsList = null;
        mSport = sport;
    }

    @Override
    public List<SportLevel> loadInBackground() {
        String request = Const.WebServer.DOMAIN_NAME + Const.WebServer.API;
        if (mSport != -1) {
            request += Const.WebServer.SPORTS + mSport + Const.WebServer.SEPARATOR + Const.WebServer.LEVELS;
            NetworkUtil.Response response = NetworkUtil.get(request, null);
            if(response.getReturnCode()==NetworkUtil.Response.UNKNOWN_HOST_ERROR){
                return null;
            }else {
                return Arrays.asList(SportLevel.parseList(response.getBody()));
            }
        }else{
            return new ArrayList<>();
        }
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mLevelsList == null) {
            forceLoad();
        } else {
            deliverResult(mLevelsList);
        }
    }

    @Override
    public void deliverResult(List<SportLevel> data) {
        mLevelsList = data;
        super.deliverResult(data);
    }
}
