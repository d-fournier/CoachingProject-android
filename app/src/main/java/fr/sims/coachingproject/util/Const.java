package fr.sims.coachingproject.util;

/**
 * Created by dfour on 10/02/2016.
 */
public class Const {

    public static class SharedPref {
        public final static String SHARED_PREF_NAME = "CoachingProjectSharedPref";

        public final static String IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH";
        public final static String IS_GCM_TOKEN_SENT_TO_SERVER = "IS_GCM_TOKEN_SENT_TO_SERVER";

        public final static String CURRENT_USER_ID = "CURRENT_USER_ID";
        // TODO Move out from SharedPref
        public final static String CURRENT_TOKEN = "CURRENT_TOKEN";
    }

    public static class WebServer {
        public final static String DOMAIN_NAME = "https://coachingproject.herokuapp.com/";
        public final static String API = "api/";
        public final static String AUTH = "auth/";

        public final static String LOGIN = "login/";
        public final static String REGISTER = "register/";
        public final static String ME = "me/";
        public final static String USER_PROFILE = "users/";
        public final static String SPORTS = "sports/";
        public final static String GROUPS = "groups/";
        public final static String LEVELS = "levels/";
        public final static String COACHING_RELATION = "relations/";
        public final static String MESSAGES = "messages/";
        public static final String DEVICES = "devices/";
        public final static String SEPARATOR = "/";



        public final static String COACH_PARAMETER = "coach";
        public final static String SPORT_PARAMETER = "sport";
        public final static String LEVEL_PARAMETER = "level";
        public final static String KEYWORDS_PARAMETER = "keywords";
    }

    public static class Notification {
        public static class Type {
            public static final String MESSAGE_NEW = "message_new";
            public static final int MESSAGE_NEW_ID = 0;
            public static final String COACHING_RESPONSE = "coaching_response";
            public static final int COACHING_RESPONSE_ID = 1;
            public static final String COACHING_NEW = "coaching_new";
            public static final int COACHING_NEW_ID = 2;
            public static final String COACHING_END = "coaching_end";
            public static final int COACHING_END_ID = 3;
        }

        public static class Data {
            public static final String CONTENT = "content";
            public static final String TYPE = "type";

        }
    }

    public static class BroadcastEvent {
        public final static String EVENT_END_SERVICE_ACTION = "fr.sims.coachingproject.event.END_SERVICE_ACTION";
        public final static String EXTRA_ACTION_NAME = "fr.sims.coachingproject.extra.ACTION_NAME";

        public final static String EVENT_COACHING_RELATIONS_UPDATED = "fr.sims.coachingproject.event.COACHING_RELATIONS_UPDATED";
        public final static String EVENT_USER_PROFILE_UPDATED = "fr.sims.coachingproject.event.USER_PROFILE_UPDATED";
        public final static String EVENT_COACHING_RELATION_UPDATED = "fr.sims.coachingproject.event.COACHING_RELATION_UPDATED";
        public final static String EVENT_MESSAGES_UPDATED = "fr.sims.coachingproject.event.MESSAGES_UPDATED";
        public final static String EXTRA_ITEM_ID = "fr.sims.coachingproject.event.EXTRA_ITEM_ID";
        public static final String EVENT_GROUPS_UPDATED = "fr.sims.coachingproject.event.GROUPS_UPDATED";
    }
}
