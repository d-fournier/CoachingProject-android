package fr.sims.coachingproject;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.activeandroid.ActiveAndroid;

import java.util.List;

import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.util.Const;

/**
 * Created by dfour on 10/02/2016.
 */
public class CoachingProjectApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);

        // TODO Remove when connection is ready
        List<UserProfile> list = UserProfile.getAllUserProfile();
        if(list.size() == 0) {
            UserProfile up = new UserProfile();
            up.mId = 1;
            up.mName = "John Doe";
            up.mBirthDate = "01/01/1990";
            up.mCity = "Villeurbanne (69100)";
            up.mIsCoach = false;
            up.mMail = "j.doe@example.com";
            up.mPicture = "https://i1.wp.com/www.techrepublic.com/bundles/techrepubliccore/images/icons/standard/icon-user-default.png";
            up.save();

            SharedPreferences.Editor sharedPrefEditor = getSharedPreferences(Const.SharedPref.SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
            sharedPrefEditor.putLong(Const.SharedPref.CURRENT_USER_ID, 1);
            sharedPrefEditor.apply();
        }
    }
}
