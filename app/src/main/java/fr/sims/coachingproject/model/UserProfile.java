package fr.sims.coachingproject.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dfour on 10/02/2016.
 */

@Table(name="UserProfile", id="_id")
public class UserProfile extends Model{

    public UserProfile(){}

    @Column(name = "id", unique = true)
    @Expose
    @SerializedName("id")
    public long mId;

    @Column(name = "displayName")
    @Expose
    @SerializedName("displayName")
    public String mDisplayName;

    @Column(name = "birthdate")
    @Expose
    @SerializedName("birthdate")
    public String mBirthdate;

    @Column(name = "city")
    @Expose
    @SerializedName("city")
    public String mCity;

    @Column(name = "isCoach")
    @Expose
    @SerializedName("isCoach")
    public boolean mIsCoach;


    public List<SportLevel> mSportsList = null;

    /* Json Builder */
    public static UserProfile parseItem(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        UserProfile res = null;
        try {
            res = gson.fromJson(json, UserProfile.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static UserProfile[] parseList(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, UserProfile[].class);
    }

    /* Database Request */
    public static UserProfile getUserProfileById(long id) {
        UserProfile up = new Select().from(UserProfile.class).where("id = ?", id).executeSingle();
       // up.mSportsList = UserSportLevel.getAllSportLevelByUserId(id);
        return up;
    }

    public static List<UserProfile> getAllUserProfile() {
        return new Select().from(UserProfile.class).execute();
    }

    public static void deleteUserProfileById(long id) {
        long[]ids = {id};
        deleteUserProfileByIds(ids);
    }


    public static void deleteUserProfileByIds(long[] ids) {
        int length = ids.length;
        if(length > 0) {
            String inList = "(" + ids[0];
            for(int i = 1 ; i < length ; i++)
                inList += ","+ids[i];
            inList += ")";
            new Delete().from(UserProfile.class).where("id IN "+inList).execute();
        }
    }
}
