package fr.sims.coachingproject;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by dfour on 10/02/2016.
 */
public class CoachingProjectApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
