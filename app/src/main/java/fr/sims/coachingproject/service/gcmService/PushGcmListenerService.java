package fr.sims.coachingproject.service.gcmService;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.ArrayList;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.model.Message;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.activity.GroupActivity;
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
            case Const.Notification.Type.MESSAGE_NEW:
                handleMessageNew(data);
                break;
            case Const.Notification.Type.COACHING_RESPONSE:
            case Const.Notification.Type.COACHING_END:
            case Const.Notification.Type.COACHING_NEW:
                handleCoachingEvent(messageType, data);
                break;
            case Const.Notification.Type.GROUP_JOIN:
            case Const.Notification.Type.GROUP_INVITE:
            case Const.Notification.Type.GROUP_JOIN_ACCEPTED:
                handleGroupEvent(messageType, data);
                break;
        }
    }

    private void handleMessageNew(Bundle data) {
        String messageString = data.getString(Const.Notification.Data.CONTENT, "");
        Message message = Message.parseItem(messageString);
        if (message == null)
            return;
        message.saveOrUpdate();

        String tag = Const.Notification.Id.MESSAGE + "_";
        NotificationCompat.Builder notifBuilder;
        if (message.mRelation != null) {
            notifBuilder = getRelationNotification(message.mRelation);
            tag += Const.Notification.Tag.RELATION + String.valueOf(message.mRelation.mIdDb);
        } else {
            notifBuilder = getGroupNotification(message.mGroup);
            tag += Const.Notification.Tag.GROUP + String.valueOf(message.mGroup.mIdDb);
        }

        String newMsgContent = message.mSender.mDisplayName + ": " + message.mContent;
        setContent(notifBuilder, tag, newMsgContent, R.string.notif_content_message, R.plurals.notif_summary_message);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(tag, Const.Notification.Id.MESSAGE, notifBuilder.build());

        Intent broadcast = new Intent(Const.BroadcastEvent.EVENT_MESSAGES_UPDATED);
        broadcast.putExtra(Const.BroadcastEvent.EXTRA_ITEM_ID, message.mIdDb);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    private void handleGroupEvent(String messageType, Bundle data) {
        String groupString = data.getString(Const.Notification.Data.CONTENT, "");
        String joinName = data.getString(Const.Notification.Data.DISPLAY_NAME);
        Group group = Group.parseItem(groupString);
        if (group == null) {
            return;
        }

        int contentId = -1;
        switch (messageType) {
            case Const.Notification.Type.GROUP_JOIN:
                contentId = R.string.notif_group_join;
                group.mIsCurrentUserMember = true;
                group.mIsCurrentUserPending = false;
                break;
            case Const.Notification.Type.GROUP_INVITE:
                contentId = R.string.notif_group_invite;
                group.mIsCurrentUserMember = false;
                group.mIsCurrentUserPending = true;
                break;
            case Const.Notification.Type.GROUP_JOIN_ACCEPTED:
                contentId = R.string.notif_group_join_accepted;
                group.mIsCurrentUserMember = true;
                group.mIsCurrentUserPending = false;
                break;
        }
        group.saveOrUpdate();

        String tag = Const.Notification.Id.GROUP + "_" + Const.Notification.Tag.GROUP + String.valueOf(group.mIdDb);
        NotificationCompat.Builder notifBuilder = getGroupNotification(group);
        String newMsgContent = getString(contentId, group.mName, joinName);
        setContent(notifBuilder, tag, newMsgContent, R.string.notif_content_event, R.plurals.notif_summary_event);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(tag, Const.Notification.Id.GROUP, notifBuilder.build());

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

        int contentId = -1;
        switch (messageType) {
            case Const.Notification.Type.COACHING_RESPONSE:
                if (relation.mIsAccepted) {
                    contentId = R.string.notif_coaching_accept;
                } else {
                    contentId = R.string.notif_coaching_refuse;
                }
                break;
            case Const.Notification.Type.COACHING_END:
                contentId = R.string.notif_coaching_end;
                break;
            case Const.Notification.Type.COACHING_NEW:
                contentId = R.string.notif_coaching_new;
                break;
        }

        String tag = Const.Notification.Id.RELATION + "_" + Const.Notification.Tag.RELATION + String.valueOf(relation.mIdDb);
        NotificationCompat.Builder notifBuilder = getRelationNotification(relation);

        String newMsgContent = getString(contentId, partner.mDisplayName);
        setContent(notifBuilder, tag, newMsgContent, R.string.notif_content_event, R.plurals.notif_summary_event);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(tag, Const.Notification.Id.RELATION, notifBuilder.build());
    }

    private void setContent(NotificationCompat.Builder notifBuilder, String tag, String newEventMessage, int contentResourceId, int summaryResourceId) {
        ArrayList<String> historyList = SharedPrefUtil.getNotificationContent(this, tag);
        historyList.add(0, newEventMessage);
        int msgNumber = historyList.size();
        if (msgNumber > 1) {
            notifBuilder.setContentText(getString(contentResourceId, msgNumber));
            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
            int index = 0;
            for (String s : historyList) {
                style.addLine(s);
                if (index >= 2)
                    break;
                else
                    index++;
            }
            if (index + 1 < msgNumber)
                style.setSummaryText(getResources().getQuantityString(summaryResourceId, (msgNumber - 3), (msgNumber - 3)));
            notifBuilder.setStyle(style);
        } else {
            notifBuilder.setContentText(newEventMessage);
        }
        SharedPrefUtil.putNotificationContent(this, tag, historyList);
    }

    private NotificationCompat.Builder getGroupNotification(Group gr) {
        Intent intent = GroupActivity.getIntent(this, gr.mIdDb);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = getBasicNotification();
        builder
                .setContentIntent(pendingIntent)
                .setContentTitle("Group " + gr.mName);

        return builder;
    }

    private NotificationCompat.Builder getRelationNotification(CoachingRelation cr) {
        Intent intent = RelationActivity.getIntent(this, cr.mIdDb);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        UserProfile partner;
        if (cr.mCoach.mIdDb == SharedPrefUtil.getConnectedUserId(this)) {
            partner = cr.mTrainee;
        } else {
            partner = cr.mCoach;
        }

        NotificationCompat.Builder builder = getBasicNotification();
        builder
                .setContentIntent(pendingIntent)
                .setContentTitle("Relation with " + partner.mDisplayName);

        return builder;
    }

    private NotificationCompat.Builder getBasicNotification() {
        int color;
        if (Build.VERSION.SDK_INT > 23)
            color = getResources().getColor(R.color.colorPrimary, null);
        else
            color = getResources().getColor(R.color.colorPrimary);

        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.img_runners_white)
                .setColor(color)
                .setAutoCancel(true);
    }
}