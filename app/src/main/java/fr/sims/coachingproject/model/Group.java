package fr.sims.coachingproject.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin on 01/03/2016.
 */
@Table(name="UserGroup")
public class Group extends Model{

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
    public UserProfile[] mMembers;

    @Column(name = "sport")
    @Expose
    @SerializedName("sport")
    public Sport mSport;

    public Group(){

    }

    public Group saveOrUpdate(){
        mSport = mSport.saveOrUpdate();

        Group res = new Select().from(Group.class).where("idDb = ?", mIdDb).executeSingle();
        if(res != null) {
            res.bindProperties(this);
            res.save();
        } else {
            this.save();
            res = this;
        }
        saveMembers();
        return res;
    }

    private void saveMembers(){
        List<GroupMembers> groupMembers = new ArrayList<>();
        for(UserProfile member : mMembers) {
            GroupMembers gm = new GroupMembers();
            gm.mGroupId = mIdDb;
            gm.mUser = member;
            groupMembers.add(gm);
        }
        for(GroupMembers gm : groupMembers) {
            gm.saveOrUpdate();
        }
    }

    private void bindProperties(Group g) {
        this.mCreationDate = g.mCreationDate;
        this.mSport = g.mSport;
        this.mMembers = g.mMembers;
        this.mName= g.mName;
        this.mDescription = g.mDescription;
    }

    /* Json Builder */
    public static Group parseItem(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Group res = null;
        try {
            res = gson.fromJson(json, Group.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }


    public static Group[] parseList(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Group[] res = null;
        try {
            res = gson.fromJson(json, Group[].class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static List<Group> getAllGroups() {
        List<Group> groupList =  new Select().from(Group.class).execute();
        for(Group g : groupList){
            try {
                List<GroupMembers> list = GroupMembers.getAllUsersByGroupId(g.mIdDb);
                int size = list.size();
                g.mMembers = new UserProfile[size];
                for (int i = 0; i < size; i++) {
                    g.mMembers[i] = list.get(i).mUser;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return groupList;
    }

}
