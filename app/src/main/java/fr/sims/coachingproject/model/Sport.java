package fr.sims.coachingproject.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dfour on 15/02/2016.
 */
@Table(name="Sport")
public class Sport extends Model {

    @Column(name = "idDb", unique = true)
    @Expose
    @SerializedName("id")
    public long mIdDb;

    @Column(name = "name")
    @Expose
    @SerializedName("name")
    public String mName;

    public Sport saveOrUpdate(){
        Sport res = new Select().from(Sport.class).where("idDb = ?", mIdDb).executeSingle();
        if(res != null) {
            res.bindProperties(this);
            res.save();
        } else {
            this.save();
            res = this;
        }
        return res;
    }

    private void bindProperties(Sport sport) {
        this.mName = sport.mName;
    }

    public static Sport[] parseList(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Sport[] res = null;
        try {
            res = gson.fromJson(json, Sport[].class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public String toString() {
        return this.mName;
    }

    public long getmIdDb() {
        return mIdDb;
    }

    public static Sport getSportById(long id){
        return new Select().from(Sport.class).where("idDb = ?", id).executeSingle();
    }

    public List<SportLevel> getLevels(){
        return new Select().from(SportLevel.class).where("sport == ?", mIdDb).execute();
    }

}
