package fr.sims.coachingproject.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

import fr.sims.coachingproject.util.Const;

/**
 * Created by dfour on 10/02/2016.
 */

@Table(name="UserProfile", id="_id")
public class UserProfile extends Model{

    @Column(name = "id")
    public long mId;

    @Column(name = "name")
    public String mName;

    @Column(name = "email")
    public String mMail;

    @Column(name = "picture")
    public String mPicture;

    @Column(name = "birthdate")
    public String mBirthDate;

    @Column(name = "city")
    public String mCity;

    @Column(name = "isCoach")
    public boolean mIsCoach;


    public static UserProfile getCurrentUserProfile(Context ctx) {
        SharedPreferences settings = ctx.getSharedPreferences(Const.SharedPref.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        long currentUserId = settings.getLong(Const.SharedPref.CURRENT_USER_ID, -1);

        if(currentUserId == -1) {
            return null;
        } else {
            return new Select().from(UserProfile.class).where("id = ?", currentUserId).executeSingle();
        }
    }

    public static List<UserProfile> getAllUserProfile() {
        return new Select().from(UserProfile.class).execute();
    }
}
