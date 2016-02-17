package fr.sims.coachingproject.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.Arrays;
import java.util.List;

import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;

/**
 * Created by Benjamin on 16/02/2016.
 */
public class CoachLoader extends AsyncTaskLoader<List<UserProfile>> {

    public CoachLoader(Context context) {
        super(context);
    }

    @Override
    public List<UserProfile> loadInBackground() {
        String request = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.USER_PROFILE + "?" +
                Const.WebServer.COACH_PARAMETER + "=true";
        String response = NetworkUtil.get(request,null);
        return Arrays.asList(UserProfile.parseList(response));
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }


}
