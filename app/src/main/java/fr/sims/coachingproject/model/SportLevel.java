package fr.sims.coachingproject.model;

import android.database.sqlite.SQLiteConstraintException;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dfour on 15/02/2016.
 */
@Table(name="SportLevel")
public class SportLevel extends Model {

    @Column(name = "idDb", unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    @Expose
    @SerializedName("id")
    public long mIdDb;

    @Column(name = "title")
    @Expose
    @SerializedName("title")
    public String mTitle;

    @Column(name = "rank")
    @Expose
    @SerializedName("rank")
    public int mRank;

    @Column(name = "sport", notNull = true)
    @Expose
    @SerializedName("sport")
    public Sport mSport;

    public SportLevel saveOrUpdate(){
        mSport = mSport.saveOrUpdate();
        SportLevel res = new Select().from(SportLevel.class).where("idDb = ?", mIdDb).executeSingle();
        if(res != null) {
            res.bindProperties(this);
            res.save();
        } else {
            this.save();
            res = this;
        }
        return res;
    }

    private void bindProperties(SportLevel sl) {
        this.mSport = sl.mSport;
        this.mTitle = sl.mTitle;
        this.mRank = sl.mRank;
    }
}
