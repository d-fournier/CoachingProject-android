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
            Sport sp1 = new Sport();
            sp1.mId=1;
            sp1.mName="Football";
            sp1.save();

            SportLevel spLevel1 = new SportLevel();
            spLevel1.mId = 1;
            spLevel1.mRank = 5;
            spLevel1.mTitle = "Expert";
            spLevel1.mSport = sp1;
            spLevel1.save();

            Sport sp2 = new Sport();
            sp2.mId=2;
            sp2.mName="BasketBall";
            sp2.save();

            UserProfile me = new UserProfile();
            me.mId = 1;
            me.mName = "John Doe";
            me.mBirthday = "01/01/1990";
            me.mCity = "Villeurbanne (69100)";
            me.mIsCoach = false;
            me.mMail = "j.doe@example.com";
            me.mPicture = "https://i1.wp.com/www.techrepublic.com/bundles/techrepubliccore/images/icons/standard/icon-user-default.png";
            me.save();

            UserSportLevel uSpLvl1 = new UserSportLevel();
            uSpLvl1.mUserId = me.mId;
            uSpLvl1.mSportLevel = spLevel1;
            uSpLvl1.save();

            UserProfile coach = new UserProfile();
            coach.mId = 2;
            coach.mName = "Trainer";
            coach.mBirthday = "11/11/1990";
            coach.mCity = "Villeurbanne (69100)";
            coach.mIsCoach = true;
            coach.mMail = "trainer@example.com";
            coach.mPicture = "https://i1.wp.com/www.techrepublic.com/bundles/techrepubliccore/images/icons/standard/icon-user-default.png";
            coach.save();

            UserSportLevel uSpLvl2 = new UserSportLevel();
            uSpLvl1.mUserId = coach.mId;
            uSpLvl1.mSportLevel = spLevel1;
            uSpLvl1.save();

            CoachingRelation cr = new CoachingRelation();
            cr.mId = 1;
            cr.mCoach = coach;
            cr.mUser = me;
            cr.mDate = "10/02/2016";
            cr.mIsPending = true;
            cr.mComment = "Je suis à la recherche non pas de la vérité, mais simplement d'une aventure qui sorte un peu de la banalité !";
            cr.mSport = sp1;
            cr.save();

            SharedPreferences.Editor sharedPrefEditor = getSharedPreferences(Const.SharedPref.SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
            sharedPrefEditor.putLong(Const.SharedPref.CURRENT_USER_ID, 1);
            sharedPrefEditor.apply();
        }
    }
}
