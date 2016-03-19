package fr.sims.coachingproject.loader.network;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import fr.sims.coachingproject.loader.local.GenericLocalLoader;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by Benjamin on 19/03/2016.
 */
public class JoinLoader extends GenericLocalLoader<List<Group>> {

    public JoinLoader(Context context) {
        super(context);
    }

    @Override
    protected String getBroadcastEvent() {
        return Const.BroadcastEvent.EVENT_INVITATIONS_UPDATED;
    }

    @Override
    public List<Group> loadInBackground() {
        NetworkUtil.Response res = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS
                        + Const.WebServer.MY_JOINS + Const.WebServer.SEPARATOR,
                SharedPrefUtil.getConnectedToken(getContext()));
        if (res.isSuccessful()) {
            return Arrays.asList(Group.parseList(res.getBody()));
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