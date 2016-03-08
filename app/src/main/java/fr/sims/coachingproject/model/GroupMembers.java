package fr.sims.coachingproject.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Benjamin on 07/03/2016.
 */
@Table(name="GroupMembers")
public class GroupMembers extends Model {

    @Column(name = "groupId", notNull = true, uniqueGroups = {"group1"}, onUniqueConflicts = {Column.ConflictAction.IGNORE})
    public long mGroupId;

    @Column(name = "userId", notNull = true, uniqueGroups = {"group1"}, onUniqueConflicts = {Column.ConflictAction.IGNORE})
    public UserProfile mUser;

    public GroupMembers saveOrUpdate(){
        mUser = mUser.saveOrUpdate();
        this.save();
        return this;
    }

    public static List<GroupMembers> getAllUsersByGroupId(long id) {
        return new Select().from(GroupMembers.class).where("groupId == ?", id).execute();
    }

}