package fr.sims.coachingproject.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dfour on 13/03/2016.
 */
public class BlogPost {

    @Expose
    @SerializedName("id")
    public long mIdDb;

    @Expose
    @SerializedName("title")
    public String mTitle;

    @Expose
    @SerializedName("description")
    public String mShortDescription;

    @Expose
    @SerializedName("content")
    public String mContent;

    @Expose
    @SerializedName("picture")
    public String mPicture;

    @Expose
    @SerializedName("sport")
    public Sport mSport;

    @Expose
    @SerializedName("date")
    public String mDate;

    @Expose
    @SerializedName("last_modification_date")
    public String mLastModificationDate;

    @Expose
    @SerializedName("author")
    public UserProfile mAuthor;

    public BlogPost() { }

    public static BlogPost parseItem(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        BlogPost res = null;
        try {
            res = gson.fromJson(json, BlogPost.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static BlogPost[] parseList(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        BlogPost[] res = null;
        try {
            res = gson.fromJson(json, BlogPost[].class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

}
