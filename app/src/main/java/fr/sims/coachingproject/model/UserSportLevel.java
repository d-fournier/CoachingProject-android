package fr.sims.coachingproject.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by dfour on 15/02/2016.
 */
@Table(name="UserSportLevel")
public class UserSportLevel extends Model {

    @Column(name = "userId")
    public long mUserId;

    @Column(name = "sportLevel")
    public SportLevel mSportLevel;

    public static List<SportLevel> getAllSportLevelByUserId(long id) {
        return new Select("sportLevel").from(UserSportLevel.class).where("userId == ?", id).execute();
    }

}
