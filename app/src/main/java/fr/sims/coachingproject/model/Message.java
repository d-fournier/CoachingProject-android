package fr.sims.coachingproject.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

@Table(name="Message")
public class Message extends Model {

    @Column(name = "idDb", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    @Expose
    @SerializedName("id")
    public long mIdDb;

    @Column(name = "time")
    @Expose
    @SerializedName("time")
    public Date mTime;

    @Column(name = "content")
    @Expose
    @SerializedName("content")
    public String mContent;

    @Column(name = "sender")
    @Expose
    @SerializedName("from_user")
    public UserProfile mSender;

    @Column(name = "relation")
    @Expose
    @SerializedName("to_relation")
    public CoachingRelation mRelation;

    private void bindProperties(Message message) {
        this.mContent = message.mContent;
        this.mSender = message.mSender;
        this.mRelation = message.mRelation;
        this.mTime = message.mTime;
    }

    public static Message[] parseList(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Message[] res = null;
        try {
            res = gson.fromJson(json, Message[].class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public Message saveOrUpdate(){
        mSender.saveOrUpdate();
        mRelation.saveOrUpdate();

        Message res = (Message) new Select().from(Message.class).where("idDb = ?", mIdDb).execute().get(0);
        if(res != null) {
            res.bindProperties(this);
            res.save();
        } else {
            this.save();
            res = this;
        }
        return res;
    }

    public static List<Message> getAllMessages() {
        return new Select()
                .from(Message.class)
                .execute();
    }
}