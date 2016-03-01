package fr.sims.coachingproject.model.fakejson;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Benjamin on 16/02/2016.
 */
public class LoginResponse {

        @Expose
        @SerializedName("auth_token")
        public String token;


        public LoginResponse() {
        }

        public static LoginResponse fromJson(String response) {
            return new Gson().fromJson(response,LoginResponse.class);
        }


}
