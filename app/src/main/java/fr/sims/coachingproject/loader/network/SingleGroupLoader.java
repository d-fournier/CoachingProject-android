package fr.sims.coachingproject.loader.network;

import android.content.Context;

import fr.sims.coachingproject.loader.local.GenericLocalLoader;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by Benjamin on 10/03/2016.
 */
public class SingleGroupLoader extends GenericLocalLoader<Group> {

    private long mId;

    public SingleGroupLoader(Context context, long id) {
        super(context);
        mId = id;
    }


    @Override
    public Group loadInBackground() {
            final Group g = Group.getGroupById(mId);
            if (g == null) {
                NetworkUtil.Response res = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS + mId+ Const.WebServer.SEPARATOR,
                        SharedPrefUtil.getConnectedToken(getContext()));
                if (res.isSuccessful()) {
                    return Group.parseItem(res.getBody());
                }else{
                    return null;
                }

            } else {
                return g;
            }

    }

    @Override
    protected String getBroadcastEvent() {
        return null;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }


}
