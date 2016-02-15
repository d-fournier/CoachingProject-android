package fr.sims.coachingproject.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by dfour on 15/02/2016.
 */
@Table(name="Sport", id="_id")
public class Sport extends Model {

    @Column(name = "id", unique = true)
    public long mId;

    @Column(name = "name")
    public String mName;

}
