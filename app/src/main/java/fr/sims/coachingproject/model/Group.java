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
 * Created by Benjamin on 01/03/2016.
 */
@Table(name = "UserGroup")
public class Group extends Model {

    @Column(name = "idDb", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    @Expose
    @SerializedName("id")
    public long mIdDb;

    @Column(name = "creation_date")
    @Expose
    @SerializedName("creation_date")
    public String mCreationDate;

    @Column(name = "name")
    @Expose
    @SerializedName("name")
    public String mName;

    @Column(name = "description")
    @Expose
    @SerializedName("description")
    public String mDescription;

    @Column(name = "members")
    @Expose
    @SerializedName("members")
    public int mMembers;

    @Column(name = "sport")
    @Expose
    @SerializedName("sport")
    public Sport mSport;

    @Column(name = "city")
    @Expose
    @SerializedName("city")
    public String mCity;

    @Column(name = "is_member")
    public boolean mIsCurrentUserMember;

    @Column(name = "is_pending")
    public boolean mIsCurrentUserPending;

    public Group() {

    }


    /* Json Builder */
    public static Group parseItem(String json) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Group res = null;
        try {
            res = gson.fromJson(json, Group.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static Group[] parseList(String json) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Group[] res = null;
        try {
            res = gson.fromJson(json, Group[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    public static List<Group> getAllGroups() {
        return new Select().from(Group.class).execute();
    }

    public static Group getGroupById(long id){
        return new Select()
                .from(Group.class)
                .where("idDb = ?", id)
                .executeSingle();
    }

    public Group saveOrUpdate() {
        mSport = mSport.saveOrUpdate();

        Group res = new Select().from(Group.class).where("idDb = ?", mIdDb).executeSingle();
        if (res != null) {
            res.bindProperties(this);
            res.save();
        } else {
            this.save();
            res = this;
        }
        return res;
    }


    private void bindProperties(Group g) {
        this.mCreationDate = g.mCreationDate;
        this.mSport = g.mSport;
        this.mMembers = g.mMembers;
        this.mName = g.mName;
        this.mDescription = g.mDescription;
        this.mCity = g.mCity;
        this.mIsCurrentUserMember = g.mIsCurrentUserMember;
        this.mIsCurrentUserPending = g.mIsCurrentUserPending;
    }

    public static void clear(){
        new Delete().from(Group.class).execute();
    }
}
