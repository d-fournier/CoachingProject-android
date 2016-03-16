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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    public CoachingRelation mRelation;

    @Column(name = "to_group")
    public Group mGroup;

    @Column(name = "isPinned")
    @Expose
    @SerializedName("is_pinned")
    public boolean mIsPinned;


    private void bindProperties(Message message) {
        this.mContent = message.mContent;
        this.mSender = message.mSender;
        this.mRelation = message.mRelation;
        this.mGroup = message.mGroup;
        this.mTime = message.mTime;
        this.mIsPinned=message.mIsPinned;
    }

    public static Message[] parseList(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Message[] res = null;
        try {
            res = gson.fromJson(json, Message[].class);
            JSONArray messageArray=new JSONArray(json);
            for(int i=0; i<messageArray.length(); i++){
                JSONObject messageObject=messageArray.getJSONObject(i);
                try{
                    JSONObject relationObject=messageObject.getJSONObject("to_relation");
                    res[i].mRelation=CoachingRelation.parseItem(relationObject.toString());
                }catch(JSONException e){
                    res[i].mRelation=null;
                }
                try{
                    JSONObject groupObject=messageObject.getJSONObject("to_group");
                    res[i].mGroup=Group.parseItem(groupObject.toString());
                }catch(JSONException e){
                    res[i].mGroup=null;
                }
            }


        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static Message parseItem(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Message res = null;
        try {
            res = gson.fromJson(json, Message.class);
            JSONObject messageObject=new JSONObject(json);
            try{
                JSONObject relationObject=messageObject.getJSONObject("to_relation");
                res.mRelation=CoachingRelation.parseItem(relationObject.toString());
            }catch(JSONException e){
                res.mRelation=null;
            }
            try{
                JSONObject groupObject=messageObject.getJSONObject("to_group");
                res.mGroup=Group.parseItem(groupObject.toString());
            }catch(JSONException e){
                res.mGroup=null;
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public Message saveOrUpdate(){
        if(mGroup!=null){
            mGroup.mIsCurrentUserMember=true;
            mGroup.mIsCurrentUserPending=false;
            mGroup=mGroup.saveOrUpdate();
        }
        if(mRelation!=null){
            mRelation=mRelation.saveOrUpdate();
        }
        mSender=mSender.saveOrUpdate();

        Message res = new Select().from(Message.class).where("idDb = ?", mIdDb).executeSingle();
        if(res != null) {
            res.bindProperties(this);
            res.save();
        } else {
            this.save();
            res = this;
        }
        return res;
    }

    public static List<Message> getAllMessagesByRelationId(long id) {
        List<Message> res=new ArrayList<>();
        CoachingRelation rel=new Select().from(CoachingRelation.class).where("idDb == ?", id).executeSingle();
        if(rel!=null) {
            res = new Select()
                    .from(Message.class)
                    .where("relation == ?", rel.getId())
                    .orderBy("time DESC")
                    .execute();
        }

        return res;
    }

    public static Message getMessageById(long id){
        return new Select().from(Message.class).where("idDb == ?", id).executeSingle();
    }

    public static List<Message> getAllMessagesByGroupId(long id) {
        List<Message> res=new ArrayList<>();
        Group g=new Select().from(Group.class).where("idDb == ?", id).executeSingle();
        if(g!=null) {
            res = new Select()
                    .from(Message.class)
                    .where("to_group == ?", g.getId())
                    .orderBy("time DESC")
                    .execute();
        }

        return res;
    }

    public static void clear(){
        new Delete().from(Message.class).execute();
    }
}