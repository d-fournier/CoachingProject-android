package fr.sims.coachingproject.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dfour on 10/02/2016.
 */

@Table(name="CoachingRelation", id="_id")
public class CoachingRelation extends Model {

    @Column(name = "id", unique = true)
    @Expose
    @SerializedName("id")
    public long mId;

    @Column(name = "coach", notNull = true)
    @Expose
    @SerializedName("coach")
    public UserProfile mCoach;

    @Column(name = "trainee", notNull = true)
    @Expose
    @SerializedName("trainee")
    public UserProfile mTrainee;

    @Column(name = "date", notNull = true)
    @Expose
    @SerializedName("date")
    public String mDate;

    @Column(name = "comment")
    @Expose
    @SerializedName("comment")
    public String mComment;

    @Column(name = "sport", notNull = true)
    @Expose
    @SerializedName("sport")
    public Sport mSport;

    @Column(name= "isPending", notNull = true)
    public boolean mIsPending;

    @Column(name = "isAccepted", notNull = true)
    public boolean mIsAccepted;


    /* Json Builder */
    public static CoachingRelation parseItem(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        CoachingRelation res = null;
        try {
            res = gson.fromJson(json, CoachingRelation.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static CoachingRelation[] parseList(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        CoachingRelation[] res = null;
        try {
            res = gson.fromJson(json, CoachingRelation[].class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    /* Database Request */
    public static List<CoachingRelation> getAllUserCoachingRelation(long id) {
        return new Select()
                .from(CoachingRelation.class)
                .where("coach == ?", id).or("user == ?", id)
                .execute();
    }
}
