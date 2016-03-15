package fr.sims.coachingproject.loader;

import android.content.Context;

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
                NetworkUtil.Response response_group = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS + mId+ Const.WebServer.SEPARATOR,
                        SharedPrefUtil.getConnectedToken(getContext()));
                if (response_group.isSuccessful()) {
                    Group gr =  Group.parseItem(response_group.getBody());
                    NetworkUtil.Response response_is_member = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS + mId
                                    + Const.WebServer.SEPARATOR + Const.WebServer.IS_MEMBER + Const.WebServer.SEPARATOR,
                            SharedPrefUtil.getConnectedToken(getContext()));
                    if(response_is_member.isSuccessful()){
                        gr.mIsCurrentUserMember = Boolean.valueOf(response_is_member.getBody());
                        return gr;
                    }else{
                        return null;
                    }
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
