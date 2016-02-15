package fr.sims.coachingproject.util;

/**
 * Created by dfour on 10/02/2016.
 */
public class Const {

    public static class SharedPref {
        public final static String SHARED_PREF_NAME = "CoachingProjectSharedPref";

        public final static String CURRENT_USER_ID = "CURRENT_USER_ID";
        // TODO Move out from SharedPref
        public final static String CURRENT_TOKEN = "CURRENT_TOKEN";
    }

    public static class WebServer {
        public final static String DOMAIN_NAME = "https://coachingproject.herokuapp.com/";
        public final static String API = "api/";

        public final static String USER_PROFILE = "users/";
        public final static String COACHING_RELATION = "relations/";
    }
}
