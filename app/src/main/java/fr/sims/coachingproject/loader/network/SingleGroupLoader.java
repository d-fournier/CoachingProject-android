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

    private static final String MEMBER = "MEM";
    private static final String ADMIN = "ADM";
    private static final String INVITED = "INV";
    private static final String PENDING = "PEN";

    private long mId;


    public SingleGroupLoader(Context context, long id) {
        super(context);
        mId = id;
    }


    @Override
    public Group loadInBackground() {
        final Group g = Group.getGroupById(mId);

        if (g == null) {
            NetworkUtil.Response response_group = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS + mId + Const.WebServer.SEPARATOR,
                    SharedPrefUtil.getConnectedToken(getContext()));
            if (response_group.isSuccessful()) {
                Group gr = Group.parseItem(response_group.getBody());
                if (checkUserStatus(gr)) {
                    return gr;
                } else {
                    return null;
                }
            } else {
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


    public boolean checkUserStatus(Group g) {
        NetworkUtil.Response response_user_status = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS + mId
                        + Const.WebServer.SEPARATOR + Const.WebServer.USER_STATUS + Const.WebServer.SEPARATOR,
                SharedPrefUtil.getConnectedToken(getContext()));
        if (response_user_status.isSuccessful() || response_user_status.getReturnCode()==401) { //401 User Not Connected -> Normal behavior
            String user_status = response_user_status.getBody().replace("\"", "");
            g.mIsCurrentUserMember = user_status.equals(MEMBER) || user_status.equals(ADMIN);
            g.mIsCurrentUserPending = user_status.equals(PENDING) || user_status.equals(INVITED);
            return true;
        } else {
            return false;
        }
    }

}
