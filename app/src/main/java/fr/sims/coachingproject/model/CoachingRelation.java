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

    // TODO isAccepted = true / false => No answer means ... ?
    @Column(name= "isPending")
    public boolean mIsPending;

    @Column(name = "isAccepted")
    public boolean mIsAccepted;

    // TODO IsClosed Sprint 1 ?
    @Column(name = "isClosed")
    public boolean mIsClosed;

    public static List<CoachingRelation> getAllCoachingRelation(){
        return new Select().from(CoachingRelation.class).execute();
    }

    public static List<CoachingRelation> getAllUserCoachingRelation(long id, boolean isCurrentUserCoach ,boolean isPending) {
        From query = new Select().from(CoachingRelation.class).where("isPending == ?", isPending);
        if(isCurrentUserCoach) {
            query.where("coach == ?", id);
        } else {
            query.where("user == ?", id);
        }
        return query.execute();
    }
}
