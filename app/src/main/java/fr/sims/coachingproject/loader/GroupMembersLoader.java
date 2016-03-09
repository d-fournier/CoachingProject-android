package fr.sims.coachingproject.loader;

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
    long mGroupId;

    public GroupMembersLoader(Context context, long id) {
        super(context);
        mGroupId = id;
    }

    @Override
    public List<UserProfile> loadInBackground() {
        String request = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS+mGroupId+Const.WebServer.SEPARATOR+Const.WebServer.MEMBERS;
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
