package fr.sims.coachingproject.loader.network;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.Arrays;
import java.util.List;

import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by damien on 2016/3/8.
 */
public class GroupMembersLoader extends AsyncTaskLoader<List<UserProfile>> {

    List<UserProfile> mUserList;
    private long mGroupId;
    private boolean mPending;

    public GroupMembersLoader(Context context, long id, boolean pending) {
        super(context);
        mGroupId = id;
        mPending = pending;
    }

    @Override
    public List<UserProfile> loadInBackground() {
        String request;
        if(mPending){
            NetworkUtil.Response isAdminResponse = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS+mGroupId+Const.WebServer.SEPARATOR
                    +Const.WebServer.IS_ADMIN+Const.WebServer.SEPARATOR,  SharedPrefUtil.getConnectedToken(getContext()));
            if(isAdminResponse.isSuccessful()){
                boolean isAdmin = Boolean.parseBoolean(isAdminResponse.getBody());
                if(isAdmin){
                    request = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS+mGroupId+Const.WebServer.SEPARATOR
                            +Const.WebServer.PENDING_MEMBERS+Const.WebServer.SEPARATOR;
                }else{
                    return null;
                }
            }else{
                return null;
            }
        }else{
            request = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS+mGroupId+Const.WebServer.SEPARATOR
                    +Const.WebServer.MEMBERS+Const.WebServer.SEPARATOR;

        }
        NetworkUtil.Response res = NetworkUtil.get(request,  SharedPrefUtil.getConnectedToken(getContext()));
        if(res.isSuccessful()) {
            return Arrays.asList(UserProfile.parseList(res.getBody()));
        }else{
            return null;
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mUserList == null) {
            forceLoad();
        } else {
            deliverResult(mUserList);
        }
    }

    @Override
    public void deliverResult(List<UserProfile> data) {
        mUserList = data;
        super.deliverResult(data);
    }
}
