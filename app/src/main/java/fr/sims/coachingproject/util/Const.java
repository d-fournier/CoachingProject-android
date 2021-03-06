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

        public final static String LEVELS_SPORTS_LOADED = "LEVELS_SPORTS_LOADED";

        public final static String NOTIFICATION_CONTENT = "NOTIFICATION_CONTENT_";
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
        public static final String BLOG = "blog/";
        public final static String SEPARATOR = "/";

        public final static String BLOG_EXTENSION = "blog";
        public final static String USER_GROUPS = "my_groups";
        public final static String MEMBERS = "members";
        public final static String IS_ADMIN = "is_admin";
        public final static String PENDING_MEMBERS = "pending_members";
        public final static String JOIN = "join";
        public final static String INVITE = "invite";
        public final static String ACCEPT_JOIN = "accept_join";
        public final static String ACCEPT_INVITE = "accept_invite";
        public static final String MY_INVITATIONS = "my_invitations";
        public static final String MY_JOINS = "my_joins";
        public static final String USER_STATUS = "user_status";
        public static final String LEAVE = "leave";



        public final static String COACH_PARAMETER = "coach";
        public final static String SPORT_PARAMETER = "sport";
        public final static String LEVEL_PARAMETER = "level";
        public final static String KEYWORDS_PARAMETER = "keywords";
        public final static String CITY_PARAMETER = "city";

        public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
        public static final String TYPE_AUTOCOMPLETE = "/autocomplete";
        public static final String OUT_JSON = "/json";

        public static final String GOOGLE_API_KEY = "AIzaSyA_p6pnmI9pQ13LIRiTaqtIiLxyJuBdFNg";
        public static final int PICK_IMAGE_REQUEST = 1;
        public static final int PICK_IMAGE_AFTER_KITKAT_REQUEST = 2;



    }

    public static class Loaders{
        public final static int RELATION_LIST_LOADER_ID = 0;
        public final static int GROUP_LOADER_ID = 1;
        public final static int SPORT_LOADER_ID = 2;
        public final static int LEVEL_LOADER_ID = 3;
        public final static int MESSAGE_LOADER_ID = 4;
        public final static int USER_LOADER_ID = 5;
        public final static int RELATION_LOADER_ID = 6;
        public final static int COACH_LOADER_ID = 7;
        public final static int GROUP_MEMBERS_LOADER_ID = 8;
        public static final int GROUP_PENDING_MEMBERS_LOADER_ID = 9;
        public static final int INVITATION_LOADER_ID = 10;
        public final static int BLOG_POSTS_LOADER_ID = 11;
        public final static int BLOG_POST_LOADER_ID = 12;
        public final static int JOIN_LOADER_ID = 13;


    }

    public static class Notification {
        public static class Type {
            public static final String MESSAGE_NEW = "message_new";
            public static final String COACHING_RESPONSE = "coaching_response";
            public static final String COACHING_NEW = "coaching_new";
            public static final String COACHING_END = "coaching_end";
            public static final String GROUP_JOIN = "group_join";
            public static final String GROUP_INVITE = "group_invite";
            public static final String GROUP_JOIN_ACCEPTED = "group_join_accepted";


        }

        public static class Data {
            public static final String CONTENT = "content";
            public static final String TYPE = "type";
            public static final String DISPLAY_NAME = "username";
        }

        public static class Tag {
            public static final String RELATION = "RELATION_";
            public static final String GROUP = "GROUP_";
        }

        public static class Id {
            public static final int MESSAGE = 0;
            public static final int RELATION = 1;
            public static final int GROUP = 2;

        }
    }

    public static class Database{
        public final static String DATABASE_NAME = "coachingproject.db";
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
        public static final String EVENT_GROUP_MEMBERS_UPDATED = "fr.sims.coachingproject.event.GROUP_MEMBERS_UPDATED";
        public static final String EVENT_INVITATIONS_UPDATED = "fr.sims.coachingproject.event.INVITATIONS_UPDATED";
        public static final String EVENT_SPORTS_UPDATED = "fr.sims.coachingproject.event.SPORTS_UPDATED";
        public static final String EVENT_LEVELS_UPDATED = "fr.sims.coachingproject.event.LEVELS_UPDATED";
    }
}
