package fr.sims.coachingproject.loader;

import android.content.Context;
import android.content.SharedPreferences;

import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by dfour on 10/02/2016.
 */
public class UserLoader extends GenericLocalLoader<UserProfile> {

    private Context mCtx;
    private long mId;

    public UserLoader(Context context, long id) {
        super(context);

        mId = id;
        mCtx = getContext();
    }

    public UserLoader(Context context) {
        this(context, -1);
    }

    @Override
    protected String getBroadcastEvent() {
        return Const.BroadcastEvent.EVENT_USER_PROFILE_UPDATED;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public UserProfile loadInBackground() {
        if(mId == -1) {
            SharedPreferences settings = mCtx.getSharedPreferences(Const.SharedPref.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            mId = settings.getLong(Const.SharedPref.CURRENT_USER_ID, -1);
            if(mId == -1) {
                return null;
            }
        }
        // Try to load user in the database
        UserProfile up = UserProfile.getUserProfileById(mId);
        // If user not stored in the DB, load the user from the server
        if(up == null) {
            String request = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.USER_PROFILE + mId + Const.WebServer.SEPARATOR;
            NetworkUtil.Response response = NetworkUtil.get(request, SharedPrefUtil.getConnectedToken(getContext()));
            if(!response.getBody().isEmpty()){
                up = UserProfile.parseItem(response.getBody());
            }
        }

        return up;
    }
}