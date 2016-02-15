package fr.sims.coachingproject;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.activeandroid.ActiveAndroid;

import java.util.List;

import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.model.SportLevel;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.model.UserSportLevel;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by dfour on 10/02/2016.
 */
public class CoachingProjectApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);

        SharedPrefUtil.putConnectedUserId(this, 1);
        SharedPrefUtil.putConnectedToken(this, "5fb6c2bb21d2df27979c47d27a586f46e2f9127b");}
}
