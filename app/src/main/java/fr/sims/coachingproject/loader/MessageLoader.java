package fr.sims.coachingproject.loader;

import android.content.Context;

import java.util.List;

import fr.sims.coachingproject.model.Message;
import fr.sims.coachingproject.util.Const;

/**
 * Created by Segolene on 23/02/2016.
 */
public class MessageLoader extends GenericLocalLoader<List<Message>> {

    private long mRelationId;

    public MessageLoader(Context context, long relationId) {
        super(context);
        mRelationId=relationId;
    }

    @Override
    protected String getBroadcastEvent() {
        return Const.BroadcastEvent.EVENT_COACHING_RELATIONS_ITEM_UPDATED;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Message> loadInBackground() {
        List<Message> r = Message.getAllMessagesByRelationId(mRelationId);
        return r;
    }
}
