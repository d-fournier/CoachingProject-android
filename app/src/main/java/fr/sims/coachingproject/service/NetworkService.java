package fr.sims.coachingproject.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import com.activeandroid.ActiveAndroid;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

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
    public static final String ACTION_RELATION_MESSAGES = "fr.sims.coachingproject.action.RELATION_MESSAGES";
    public static final String ACTION_GROUP_MESSAGES = "fr.sims.coachingproject.action.GROUP_MESSAGES";
    public static final String ACTION_TOGGLE_PIN_MESSAGES="fr.sims.coachingproject.action.TOGGLE_PIN_MESSAGES";
    public static final String ACTION_USER_GROUPS = "fr.sims.coachingproject.action.USER_GROUPS";
    public static final String ACTION_ACCEPT_USER_GROUPS = "fr.sims.coachingproject.action.ACCEPT_USER_GROUPS";
    public static final String ACTION_INVITATION_USER_GROUPS = "fr.sims.coachingproject.action.INVITATION_USER_GROUPS";

    private static final String EXTRA_ITEM_ID = "fr.sims.coachingproject.extra.ITEM_ID";
    private static final String EXTRA_PINNED_VALUE = "fr.sims.coachingproject.extra.PINNED_VALUE";
    private static final String EXTRA_MESSAGE_ID = "fr.sims.coachingproject.extra.MESSAGE_ID";
    private static final String EXTRA_GROUP_ID = "fr.sims.coachingproject.extra.GROUP_ID";
    private static final String EXTRA_USER_IDS = "fr.sims.coachingproject.extra.USER_ID";
    private static final String EXTRA_ACCEPTED = "fr.sims.coachingproject.extra.ACCEPTED";

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

    public static void startActionAcceptUserGroups(Context context, long[] userId, long groupId, boolean accepted) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_ACCEPT_USER_GROUPS);
        intent.putExtra(EXTRA_USER_IDS, userId);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        intent.putExtra(EXTRA_ACCEPTED, accepted);
        context.startService(intent);
    }

    public static void startActionInvitationUserGroups(Context context, long groupId, boolean accepted) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_INVITATION_USER_GROUPS);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        intent.putExtra(EXTRA_ACCEPTED, accepted);
        context.startService(intent);
    }


    public static void startActionUserGroups(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_USER_GROUPS);
        context.startService(intent);
    }

    public static void startActionRelationMessages(Context context, long id) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_RELATION_MESSAGES);
        intent.putExtra(EXTRA_ITEM_ID, id);
        context.startService(intent);
    }

    public static void startActionGroupMessages(Context context, long id) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_GROUP_MESSAGES);
        intent.putExtra(EXTRA_ITEM_ID, id);
        context.startService(intent);
    }

    public static void startActionTogglePinMessages(Context context, long messageId, boolean toPin){
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_TOGGLE_PIN_MESSAGES);
        intent.putExtra(EXTRA_MESSAGE_ID, messageId);
        intent.putExtra(EXTRA_PINNED_VALUE, toPin);
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
                case ACTION_RELATION_MESSAGES:
                    handleActionRelationMessages(intent.getLongExtra(EXTRA_ITEM_ID, -1));
                    break;
                case ACTION_TOGGLE_PIN_MESSAGES:
                    handleActionTogglePinMessage(intent.getLongExtra(EXTRA_MESSAGE_ID, -1), intent.getBooleanExtra(EXTRA_PINNED_VALUE, false));
                    break;
                case ACTION_USER_GROUPS:
                    handleActionUserGroups();
                    break;
                case ACTION_GROUP_MESSAGES:
                    handleActionGroupMessages(intent.getLongExtra(EXTRA_ITEM_ID, -1));
                    break;
                case ACTION_ACCEPT_USER_GROUPS:
                    handleActionAcceptUserGroups(intent.getLongArrayExtra(EXTRA_USER_IDS),intent.getLongExtra(EXTRA_GROUP_ID,-1), intent.getBooleanExtra(EXTRA_ACCEPTED,false));
                    break;
                case ACTION_INVITATION_USER_GROUPS:
                    handleActionInvitationUserGroups(intent.getLongExtra(EXTRA_GROUP_ID, -1), intent.getBooleanExtra(EXTRA_ACCEPTED, false));
            }

            Intent endIntent = new Intent(Const.BroadcastEvent.EVENT_END_SERVICE_ACTION);
            endIntent.putExtra(Const.BroadcastEvent.EXTRA_ACTION_NAME, action);
            LocalBroadcastManager.getInstance(this).sendBroadcast(endIntent);
        }
    }

    private void handleActionInvitationUserGroups(long groupId, boolean accepted) {
        JSONObject json=new JSONObject();
        try {
            json.put("accepted", accepted);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkUtil.Response res = NetworkUtil.post(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS + groupId +
                Const.WebServer.SEPARATOR + Const.WebServer.ACCEPT_INVITE + Const.WebServer.SEPARATOR, getToken(), json.toString());

        if(res.isSuccessful()){
            //TODO je sais pas quoi faire ici
        }else{

        }
    }


    private void handleActionAcceptUserGroups(long[] userIds,long groupId, boolean accepted) {
        JSONObject json=new JSONObject();
        JSONArray idArray= new JSONArray();
        for (long userId : userIds) {
            idArray.put(userId);
        }
        try {
            json.put("accepted", accepted);
            json.put("users",idArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkUtil.Response res = NetworkUtil.post(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS + groupId +
                Const.WebServer.SEPARATOR + Const.WebServer.ACCEPT_JOIN + Const.WebServer.SEPARATOR, getToken(), json.toString());

        if(res.isSuccessful()){
            //TODO je sais pas quoi faire ici
        }else{

        }

    }


    protected void handleActionConnectedUserInfo() {
        long id = SharedPrefUtil.getConnectedUserId(this);
        NetworkUtil.Response res = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.USER_PROFILE + id, getToken());
        if(res.isSuccessful()) {
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

    // TODO handle wrong request
    protected void handleActionCoachingRelation() {
        NetworkUtil.Response ress = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.COACHING_RELATION, getToken());
        if(ress.isSuccessful()) {
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

    protected void handleActionUserGroups() {
        NetworkUtil.Response res = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS
                + Const.WebServer.USER_GROUPS+ Const.WebServer.SEPARATOR, getToken());
        if(res.isSuccessful()) {
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

    protected void handleActionRelationMessages(long relationId) {
        NetworkUtil.Response res = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.COACHING_RELATION + relationId + "/" + Const.WebServer.MESSAGES, getToken());
        if(res.isSuccessful()) {
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

            Intent intent = new Intent(Const.BroadcastEvent.EVENT_MESSAGES_UPDATED);
            intent.putExtra(Const.BroadcastEvent.EXTRA_ITEM_ID, relationId);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    protected void handleActionGroupMessages(long groupId) {
        NetworkUtil.Response res = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS + groupId + Const.WebServer.SEPARATOR + Const.WebServer.MESSAGES, getToken());
        if(res.isSuccessful()) {
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

            Intent intent = new Intent(Const.BroadcastEvent.EVENT_MESSAGES_UPDATED);
            intent.putExtra(Const.BroadcastEvent.EXTRA_ITEM_ID, groupId);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    protected void handleActionTogglePinMessage(long messageId, boolean toPin) {
        JSONObject json=new JSONObject();
        try {
            json.put("is_pinned", Boolean.toString(toPin));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkUtil.Response res=NetworkUtil.patch(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.MESSAGES + messageId + "/", getToken(), json.toString());

        if(res.getReturnCode()==HttpsURLConnection.HTTP_OK){
            Message message=Message.getMessageById(messageId);
            message.mIsPinned=toPin;
            message.save();

            Intent intent = new Intent(Const.BroadcastEvent.EVENT_MESSAGES_UPDATED);
            if(message.mRelation==null){
                intent.putExtra(Const.BroadcastEvent.EXTRA_ITEM_ID, message.mGroup.mIdDb);
            }else{
                intent.putExtra(Const.BroadcastEvent.EXTRA_ITEM_ID, message.mRelation.mIdDb);
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private String getToken() {
        return SharedPrefUtil.getConnectedToken(this);
    }


}
