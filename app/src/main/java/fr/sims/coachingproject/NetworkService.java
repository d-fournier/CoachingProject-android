package fr.sims.coachingproject;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import com.activeandroid.ActiveAndroid;

import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.model.Message;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;


public class NetworkService extends IntentService {
    public static final String ACTION_CONNECTED_USER_INFO = "fr.sims.coachingproject.action.CONNECTED_USER_INFO";
    public static final String ACTION_COACHING_RELATIONS = "fr.sims.coachingproject.action.COACHING_RELATIONS";
    public static final String ACTION_COACHING_RELATION_ITEM = "fr.sims.coachingproject.action.COACHING_RELATION_ITEM";
    public static final String ACTION_GROUPS = "fr.sims.coachingproject.action.GROUPS";

    private static final String EXTRA_ITEM_ID = "fr.sims.coachingproject.extra.ITEM_ID";

    public NetworkService() {
        super("NetworkService");
    }

    public static void startActionConnectedUserInfo(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_CONNECTED_USER_INFO);
        context.startService(intent);
    }
    public static void startActionCoachingRelations(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_COACHING_RELATIONS);
        context.startService(intent);
    }

    public static void startActionGroups(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_GROUPS);
        context.startService(intent);
    }

    public static void startActionMessages(Context context, long id) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_COACHING_RELATION_ITEM);
        intent.putExtra(EXTRA_ITEM_ID, id);
        context.startService(intent);
    }

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
                case ACTION_COACHING_RELATION_ITEM:
                    handleActionCoachingRelationItem(intent.getLongExtra(EXTRA_ITEM_ID, -1));
                    break;
                case ACTION_GROUPS:
                    handleActionGroups();
                    break;
            }

            Intent endIntent = new Intent(Const.BroadcastEvent.EVENT_END_SERVICE_ACTION);
            endIntent.putExtra(Const.BroadcastEvent.EXTRA_ACTION_NAME, action);
            LocalBroadcastManager.getInstance(this).sendBroadcast(endIntent);
        }
    }

    protected void handleActionConnectedUserInfo() {
        long id = SharedPrefUtil.getConnectedUserId(this);
        NetworkUtil.Response res = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.USER_PROFILE + id, getToken());
        if(!res.getBody().isEmpty()) {
            UserProfile up = UserProfile.parseItem(res.getBody());

            ActiveAndroid.beginTransaction();
            try {
                up.saveOrUpdate();
                ActiveAndroid.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ActiveAndroid.endTransaction();
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Const.BroadcastEvent.EVENT_USER_PROFILE_UPDATED));
        }
    }


    protected void handleActionCoachingRelation() {
        NetworkUtil.Response ress = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.COACHING_RELATION, getToken());
        if(!ress.getBody().isEmpty()) {
            CoachingRelation[] crList = CoachingRelation.parseList(ress.getBody());

            ActiveAndroid.beginTransaction();
            try {
                for(CoachingRelation cr : crList) {
                    cr.saveOrUpdate();
                }
                ActiveAndroid.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ActiveAndroid.endTransaction();
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Const.BroadcastEvent.EVENT_COACHING_RELATIONS_UPDATED));
        }
    }

    protected void handleActionGroups() {
        NetworkUtil.Response res = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS, getToken());
        if(!res.getBody().isEmpty()) {
            Group[] gList = Group.parseList(res.getBody());

            ActiveAndroid.beginTransaction();
            try {
                for(Group g : gList) {
                    g.saveOrUpdate();
                }
                ActiveAndroid.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ActiveAndroid.endTransaction();
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Const.BroadcastEvent.EVENT_GROUPS_UPDATED));
        }
    }

    protected void handleActionCoachingRelationItem(long relationId) {
        NetworkUtil.Response res = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.COACHING_RELATION + relationId + "/"+Const.WebServer.MESSAGES, getToken());
        if(!res.getBody().isEmpty()) {
            Message[] messages = Message.parseList(res.getBody());

            ActiveAndroid.beginTransaction();

            try {
                for(Message ms : messages){
                    ms.saveOrUpdate();
                }
                ActiveAndroid.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ActiveAndroid.endTransaction();
            }

            Intent intent = new Intent(Const.BroadcastEvent.EVENT_COACHING_RELATIONS_ITEM_UPDATED);
            intent.putExtra(Const.BroadcastEvent.EXTRA_ITEM_ID, relationId);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private String getToken() {
        return SharedPrefUtil.getConnectedToken(this);
    }

}
