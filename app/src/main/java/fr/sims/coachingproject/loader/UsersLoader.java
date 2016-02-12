package fr.sims.coachingproject.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import fr.sims.coachingproject.model.UserProfile;

/**
 * Created by dfour on 10/02/2016.
 */
public class UsersLoader extends AsyncTaskLoader<List<UserProfile>> {

    public UsersLoader(Context context) {
        super(context);
    }

    @Override
    public List<UserProfile> loadInBackground() {
        return UserProfile.getAllUserProfile();
    }
}