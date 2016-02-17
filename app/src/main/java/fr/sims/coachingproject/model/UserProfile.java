package fr.sims.coachingproject.model;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dfour on 10/02/2016.
 */

@Table(name="UserProfile")
public class UserProfile extends Model{

    @Column(name = "idDb", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    @Expose
    @SerializedName("id")
    public long mIdDb;

    @Column(name = "displayName")
    @Expose
    @SerializedName("displayName")
    public String mDisplayName;

    @Column(name = "picture")
    public String mPicture = "http://p7.storage.canalblog.com/77/74/402370/21923602.jpg";

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

    @Expose
    @SerializedName("levels")
    public SportLevel[] mSportsList = null;

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
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        UserProfile[] res = null;
        try {
            res = gson.fromJson(json, UserProfile[].class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    /* Database Request */
    public UserProfile saveOrUpdate(){
        UserProfile res = new Select().from(UserProfile.class).where("idDb = ?", mIdDb).executeSingle();
        if(res != null) {
            res.bindProperties(this);
            res.save();
        } else {
            this.save();
            res = this;
        }

        saveSportLevel();
        return res;
    }

    private void bindProperties(UserProfile up) {
        this.mDisplayName = up.mDisplayName;
        this.mSportsList = up.mSportsList;
        this.mPicture = up.mPicture;
        this.mBirthdate = up.mBirthdate;
        this.mCity = up.mCity;
        this.mIsCoach = up.mIsCoach;
    }

    private void saveSportLevel(){
        List<UserSportLevel> userSportLevel = new ArrayList<>();
        for(SportLevel sportLevel : mSportsList) {
            UserSportLevel usl = new UserSportLevel();
            usl.mUserId = mIdDb;
            usl.mSportLevel = sportLevel;
            userSportLevel.add(usl);
        }
        for(UserSportLevel usl : userSportLevel) {
            usl.saveOrUpdate();
        }
    }

    public static UserProfile getUserProfileById(long id) {
        UserProfile up = new Select().from(UserProfile.class).where("idDb = ?", id).executeSingle();
        try {
            List<UserSportLevel> list = UserSportLevel.getAllSportLevelByUserId(id);
            int size = list.size();
            up.mSportsList = new SportLevel[size];
            for (int i = 0; i < size; i++) {
                up.mSportsList[i] = list.get(i).mSportLevel;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return up;
    }
}
