package fr.sims.coachingproject.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by dfour on 15/02/2016.
 */
@Table(name="UserSportLevel")
public class UserSportLevel extends Model {

    @Column(name = "userId", notNull = true, uniqueGroups = {"group1"}, onUniqueConflicts = {Column.ConflictAction.IGNORE})
    public long mUserId;

    @Column(name = "sportLevel", notNull = true, uniqueGroups = {"group1"}, onUniqueConflicts = {Column.ConflictAction.IGNORE})
    public SportLevel mSportLevel;

    public UserSportLevel saveOrUpdate(){
        mSportLevel = mSportLevel.saveOrUpdate();
        this.save();
        return this;
    }

    public static List<UserSportLevel> getAllSportLevelByUserId(long id) {
        return new Select().from(UserSportLevel.class).where("userId == ?", id).execute();
    }

}
