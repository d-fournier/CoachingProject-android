package fr.sims.coachingproject.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

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