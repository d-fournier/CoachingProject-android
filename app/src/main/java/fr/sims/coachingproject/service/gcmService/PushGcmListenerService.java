package fr.sims.coachingproject.service.gcmService;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

// TODO v7 ???
import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.Message;
import fr.sims.coachingproject.ui.activity.RelationActivity;
import fr.sims.coachingproject.util.Const;

/**
 * Created by Donovan on 04/03/2016.
 */
public class PushGcmListenerService extends GcmListenerService {

    private static final String TAG = PushGcmListenerService.class.getSimpleName();

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String messageType = data.getString(Const.Notification.Data.TYPE, "");

//        if (from.startsWith("/topics/")) {
//            // message received from some topic.
//        } else {
//            // normal downstream message.
//        }

        switch (messageType) {
            case Const.Notification.Type.COACHING_RESPONSE:
                handleCoachingResponse(data);
                break;
            case Const.Notification.Type.COACHING_END:
                handleCoachingEnd(data);
                break;
            case Const.Notification.Type.COACHING_NEW:
                handleCoachingNew(data);
                break;
            case Const.Notification.Type.MESSAGE_NEW:
                handleMessageNew(data);
                break;

        }
    }

    private void handleMessageNew(Bundle data) {
        String messageString = data.getString(Const.Notification.Data.CONTENT, "");
        Message message = Message.parseItem(messageString);

        if (message == null)
            return;

        // Retrieve Previous Notification

        Intent intent = RelationActivity.getIntent(this, message.mRelation.mIdDb);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_send_24dp)
                .setContentTitle("You received new messages from " + message.mSender.mDisplayName)
                .setContentText(message.mContent)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(""+message.mRelation.mIdDb, Const.Notification.Type.MESSAGE_NEW_ID, notifBuilder.build());

    }

    private void handleCoachingNew(Bundle data) {

    }

    private void handleCoachingEnd(Bundle data) {

    }

    private void handleCoachingResponse(Bundle data) {

    }

//    /**
//     * Create and show a simple notification containing the received GCM message.
//     *
//     * @param data GCM message received.
//     */
//    private void sendNotification(Bundle data) {
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//    }
}