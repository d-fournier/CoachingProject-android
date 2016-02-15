package fr.sims.coachingproject.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by dfour on 15/02/2016.
 */
@Table(name="SportLevel", id="_id")
public class SportLevel extends Model {

    @Column(name = "id", unique = true)
    public long mId;

    @Column(name = "title")
    public String mTitle;

    @Column(name = "rank")
    public int mRank;

    @Column(name = "sport", notNull = true)
    public Sport mSport;
}
