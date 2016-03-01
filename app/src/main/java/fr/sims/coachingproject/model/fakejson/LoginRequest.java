package fr.sims.coachingproject.model.fakejson;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Benjamin on 16/02/2016.
 */
public class LoginRequest {

    @Expose
    @SerializedName("username")
    public String username;

    @Expose
    @SerializedName("password")
    public String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }


}
