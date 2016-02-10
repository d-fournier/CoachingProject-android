package fr.sims.coachingproject.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by dfour on 10/02/2016.
 */

@Table(name="UserProfile", id="_id")
public class UserProfile extends Model{

    @Column(name = "id", unique = true)
    public long mId;

    @Column(name = "name")
    public String mName;

    @Column(name = "email")
    public String mMail;

    @Column(name = "picture")
    public String mPicture;

    @Column(name = "birthday")
    public String mBirthday;

    @Column(name = "city")
    public String mCity;

    @Column(name = "isCoach")
    public boolean mIsCoach;


    public static UserProfile getUserProfileById(long id) {
        return new Select().from(UserProfile.class).where("id = ?", id).executeSingle();
    }

    public static List<UserProfile> getAllUserProfile() {
        return new Select().from(UserProfile.class).execute();
    }
}
