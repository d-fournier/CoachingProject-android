package fr.sims.coachingproject.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Benjamin on 01/03/2016.
 */
@Table(name="Group")
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
    public List<UserProfile> mMembers;

    @Column(name = "sport")
    @Expose
    @SerializedName("sport")
    public Sport mSport;

    public Group saveOrUpdate(){
        for(UserProfile up : mMembers){
            up.saveOrUpdate();
        }
        mSport = mSport.saveOrUpdate();

        Group res = new Select().from(Group.class).where("idDb = ?", mIdDb).executeSingle();
        if(res != null) {
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

}
