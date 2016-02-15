package fr.sims.coachingproject;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.activeandroid.ActiveAndroid;

import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;


public class NetworkService extends IntentService {
    private static final String ACTION_CONNECTED_USER_INFO = "fr.sims.coachingproject.action.CONNECTED_USER_INFO";
    private static final String ACTION_COACHING_RELATIONS = "fr.sims.coachingproject.action.COACHING_RELATIONS";


    public NetworkService() {
        super("NetworkService");
    }


    public static void startActionConnectedUserInfo(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_CONNECTED_USER_INFO);
        context.startService(intent);
    }
    /*public static void startActionCoachingRelations(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_COACHING_RELATIONS);
        context.startService(intent);
    }*/

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_CONNECTED_USER_INFO:
                    handleActionConnectedUserInfo();
                    break;
                case ACTION_COACHING_RELATIONS:
                    handleActionCoachingRelation();
                    break;
            }
        }
    }

    protected void handleActionConnectedUserInfo() {
        long id = SharedPrefUtil.getConnectedUserId(this);
        String res = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.USER_PROFILE + id, getToken());
        if(!res.isEmpty()) {
            UserProfile up = UserProfile.parseItem(res);

            ActiveAndroid.beginTransaction();
            try {
                UserProfile.deleteUserProfileById(id);
                up.save();
                ActiveAndroid.setTransactionSuccessful();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                ActiveAndroid.endTransaction();
            }
        }
    }

    protected void handleActionCoachingRelation() {
        String res = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.COACHING_RELATION, getToken());
        if(!res.isEmpty()) {
            UserProfile.parseItem(res).save();
        }
    }

    private String getToken() {
        return SharedPrefUtil.getConnectedToken(this);
    }

}
