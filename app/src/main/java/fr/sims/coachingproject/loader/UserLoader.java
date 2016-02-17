package fr.sims.coachingproject.loader;

import android.content.Context;
import android.content.SharedPreferences;

import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.util.Const;

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
        return UserProfile.getUserProfileById(mId);
    }
}