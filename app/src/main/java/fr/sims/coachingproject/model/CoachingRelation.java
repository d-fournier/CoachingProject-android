package fr.sims.coachingproject.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by dfour on 10/02/2016.
 */

@Table(name="CoachingRelation", id="_id")
public class CoachingRelation extends Model {

    @Column(name = "id", unique = true)
    public long mId;

    @Column(name = "coach", notNull = true)
    public UserProfile mCoach;

    @Column(name = "user", notNull = true)
    public UserProfile mUser;

    @Column(name = "date", notNull = true)
    public String mDate;

    @Column(name = "comment")
    public String mComment;

    @Column(name = "sport", notNull = true)
    public Sport mSport;

    @Column(name= "isPending", notNull = true)
    public boolean mIsPending;

    @Column(name = "isAccepted", notNull = true)
    public boolean mIsAccepted;

    public static List<CoachingRelation> getAllUserCoachingRelation(long id) {
        return new Select()
                .from(CoachingRelation.class)
                .where("coach == ?", id).or("user == ?", id)
                .execute();
    }
    public static List<CoachingRelation> getAllCoachingRelation() {
        return new Select()
                .from(CoachingRelation.class)
                .execute();
    }
}
