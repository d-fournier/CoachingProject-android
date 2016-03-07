package fr.sims.coachingproject.service.gcmService;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.model.Message;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.activity.RelationActivity;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.SharedPrefUtil;

// TODO v7 ???

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

        switch (messageType) {
            case Const.Notification.Type.COACHING_RESPONSE:
            case Const.Notification.Type.COACHING_END:
            case Const.Notification.Type.COACHING_NEW:
                handleCoachingEvent(messageType, data);
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

        message.saveOrUpdate();

        String tag = "" + message.mRelation.mIdDb;
        // TODO Retrieve unread messages from the relation to update notif

        Intent intent = RelationActivity.getIntent(this, message.mRelation.mIdDb);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notifBuilder = getBasicNotification(pendingIntent);
        notifBuilder
                .setContentTitle("You received new messages from " + message.mSender.mDisplayName)
                .setContentText(message.mContent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(tag, Const.Notification.Type.MESSAGE_NEW_ID, notifBuilder.build());

    }

    private void handleCoachingEvent(String messageType, Bundle data) {
        String coachString = data.getString(Const.Notification.Data.CONTENT, "");
        CoachingRelation relation = CoachingRelation.parseItem(coachString);

        if (relation == null)
            return;

        relation.saveOrUpdate();

        // Retrieve partner
        UserProfile partner;
        boolean isCurrentUserCoach = (relation.mCoach.mIdDb == SharedPrefUtil.getConnectedUserId(this));
        if (isCurrentUserCoach) {
            partner = relation.mTrainee;
        } else {
            partner = relation.mCoach;
        }

        int notifId = 0;
        int titleId = -1;
        String tag = "" + relation.mIdDb;
        switch (messageType) {
            case Const.Notification.Type.COACHING_RESPONSE:
                notifId = Const.Notification.Type.COACHING_RESPONSE_ID;
                if(relation.mIsAccepted){
                    titleId = R.string.notif_coaching_accept;
                } else {
                    titleId = R.string.notif_coaching_refuse;
                }
                break;
            case Const.Notification.Type.COACHING_END:
                notifId = Const.Notification.Type.COACHING_END_ID;
                titleId = R.string.notif_coaching_end;
                break;
            case Const.Notification.Type.COACHING_NEW:
                notifId = Const.Notification.Type.COACHING_NEW_ID;
                titleId = R.string.notif_coaching_new;
                break;
        }
        String title = getString(titleId, partner.mDisplayName);

        Intent intent = RelationActivity.getIntent(this, relation.mIdDb);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notifBuilder = getBasicNotification(pendingIntent);
        notifBuilder
                .setContentTitle(title);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(tag, notifId, notifBuilder.build());
    }


    private NotificationCompat.Builder getBasicNotification(PendingIntent pendingIntent){
        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_send_24dp)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
    }
}