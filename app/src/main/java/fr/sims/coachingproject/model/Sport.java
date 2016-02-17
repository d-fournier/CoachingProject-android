package fr.sims.coachingproject.model;

import android.database.sqlite.SQLiteConstraintException;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    public static List<Sport> getAllSport() {
        return new Select().from(Sport.class).execute();
    }

}
