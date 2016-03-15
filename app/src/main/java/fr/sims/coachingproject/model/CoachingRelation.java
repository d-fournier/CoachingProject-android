package fr.sims.coachingproject.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
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

@Table(name="CoachingRelation")
public class CoachingRelation extends Model {

    @Column(name = "idDb", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    @Expose
    @SerializedName("id")
    public long mIdDb;

    @Column(name = "date")
    @Expose
    @SerializedName("date")
    public String mDate;

    @Column(name = "comment")
    @Expose
    @SerializedName("comment")
    public String mComment;

    @Column(name = "coach")
    @Expose
    @SerializedName("coach")
    public UserProfile mCoach;

    @Column(name = "trainee")
    @Expose
    @SerializedName("trainee")
    public UserProfile mTrainee;

    @Column(name = "sport")
    @Expose
    @SerializedName("sport")
    public Sport mSport;

    @Expose
    @SerializedName("requestStatus")
    public Boolean mRequestStatus;

    @Column(name= "active")
    @Expose
    @SerializedName("active")
    public boolean mActive;

    @Column(name= "isPending")
    public boolean mIsPending;

    @Column(name = "isAccepted")
    public boolean mIsAccepted;





    public CoachingRelation saveOrUpdate(){
        mCoach = mCoach.saveOrUpdate();
        mTrainee = mTrainee.saveOrUpdate();
        mSport = mSport.saveOrUpdate();

        CoachingRelation res = new Select().from(CoachingRelation.class).where("idDb = ?", mIdDb).executeSingle();
        if(res != null) {
            res.bindProperties(this);
            res.save();
        } else {
            this.save();
            res = this;
        }
        return res;
    }

    private void bindProperties(CoachingRelation cr) {
        this.mRequestStatus = cr.mRequestStatus;
        this.mSport = cr.mSport;
        this.mTrainee = cr.mTrainee;
        this.mCoach= cr.mCoach;
        this.mComment = cr.mComment;
        this.mDate= cr.mDate;
        this.mIsPending = cr.mIsPending;
        this.mIsAccepted= cr.mIsAccepted;
        this.mActive =cr.mActive;
    }

    /* Json Builder */
    public static CoachingRelation parseItem(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        CoachingRelation res = null;
        try {
            res = gson.fromJson(json, CoachingRelation.class);
            if(res.mRequestStatus == null) {
                res.mIsPending = true;
                res.mIsAccepted = false;
            } else {
                res.mIsPending = false;
                res.mIsAccepted = res.mRequestStatus;
            }
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
            for(CoachingRelation cr : res) {
                if(cr.mRequestStatus == null) {
                    cr.mIsPending = true;
                    cr.mIsAccepted = false;
                } else {
                    cr.mIsPending = false;
                    cr.mIsAccepted = cr.mRequestStatus;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    /* Database Request */
    public static List<CoachingRelation> getAllCoachingRelation() {
        return new Select()
                .from(CoachingRelation.class)
                .execute();
    }
    public static CoachingRelation getCoachingRelationById(long id){
        return new Select()
                .from(CoachingRelation.class)
                .where("idDb = ?", id)
                .executeSingle();
    }

    public static void clear(){
        new Delete().from(CoachingRelation.class).execute();
    }
}
