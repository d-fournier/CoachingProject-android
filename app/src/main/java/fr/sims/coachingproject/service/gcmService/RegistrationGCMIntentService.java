package fr.sims.coachingproject.service.gcmService;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by Donovan on 04/03/2016.
 */
public class RegistrationGCMIntentService extends IntentService {
    private static final String TAG = RegistrationGCMIntentService.class.getSimpleName();

    public static final String ACTION_REGISTRATION_GCM = "fr.sims.coachingproject.action.REGISTRATION_GCM";
    public static final String ACTION_UPDATE_GCM_TOKEN = "fr.sims.coachingproject.action.ACTION_UPDATE_GCM_TOKEN";


    public RegistrationGCMIntentService() {
        super("RegistrationGCMIntentService");
    }

    public static void startActionRegistrationGCM(Context context) {
        Intent intent = new Intent(context, RegistrationGCMIntentService.class);
        intent.setAction(ACTION_REGISTRATION_GCM);
        context.startService(intent);
    }

    public static void startActionUpdateGCMToken(Context context) {
        Intent intent = new Intent(context, RegistrationGCMIntentService.class);
        intent.setAction(ACTION_UPDATE_GCM_TOKEN);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_REGISTRATION_GCM:
                    handleActionRegistrationGCM();
                    break;
                case ACTION_UPDATE_GCM_TOKEN:
                    handleActionUpdateGCMToken();
                    break;
            }
        }
    }

    private void handleActionRegistrationGCM() {
        if(!SharedPrefUtil.isGCMTokenSentToServer(this))
            sendToken();
    }

    private void handleActionUpdateGCMToken() {
        SharedPrefUtil.putIsGCMTokenSentToServer(this, false);
        sendToken();
    }

    private void sendToken(){
        String userToken = SharedPrefUtil.getConnectedToken(this);
        if(userToken.isEmpty())
            return;
        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            String deviceId = instanceID.getId();
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            boolean isSuccess = sendRegistrationToServer(token, deviceId);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            SharedPrefUtil.putIsGCMTokenSentToServer(this, isSuccess);
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            SharedPrefUtil.putIsGCMTokenSentToServer(this, false);
        }
        // TODO Add Broadcast
        // Notify UI that registration has completed, so the progress indicator can be hidden.
//        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param registrationToken The new token.
     * @param deviceId The GCM device Id.
     */
    private boolean sendRegistrationToServer(String registrationToken, String deviceId) {
        JSONObject obj = new JSONObject();
        String body;
        try {
            obj.put("device_id", deviceId);
            obj.put("registration_token", registrationToken);
            obj.put("name", "android");
            body = obj.toString();
        } catch (JSONException e) {
            return false;
        }

        NetworkUtil.Response res = NetworkUtil.post(Const.WebServer.DOMAIN_NAME + Const.WebServer.API  + Const.WebServer.DEVICES, SharedPrefUtil.getConnectedToken(this), body);
        return res.isSuccessful();
    }

    /*
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
     */
}
