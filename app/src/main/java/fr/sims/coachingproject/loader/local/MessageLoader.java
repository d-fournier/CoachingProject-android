package fr.sims.coachingproject.loader.local;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.loader.local.GenericLocalLoader;
import fr.sims.coachingproject.model.Message;
import fr.sims.coachingproject.util.Const;

/**
 * Created by Segolene on 23/02/2016.
 */
public class MessageLoader extends GenericLocalLoader<List<Message>> {

    private long mRelationId;
    private long mGroupId;

    public MessageLoader(Context context, long relationId, long groupId) {
        super(context);
        mRelationId = relationId;
        mGroupId = groupId;
    }

    @Override
    protected String getBroadcastEvent() {
        return Const.BroadcastEvent.EVENT_MESSAGES_UPDATED;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Message> loadInBackground() {
        if (mRelationId != -1) {
            return Message.getAllMessagesByRelationId(mRelationId);
        } else if (mGroupId != -1) {
            return Message.getAllMessagesByGroupId(mGroupId);
        } else {
            return new ArrayList<>();
        }
    }
}
