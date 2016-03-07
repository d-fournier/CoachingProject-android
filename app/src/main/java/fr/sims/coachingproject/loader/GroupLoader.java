package fr.sims.coachingproject.loader;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.util.Const;

/**
 * Created by Benjamin on 01/03/2016.
 */
public class GroupLoader  extends GenericLocalLoader<List<Group>> {

    private int mId;

    public GroupLoader(Context context,int id) {
        super(context);
        mId = id;
    }

    @Override
    protected String getBroadcastEvent() {
        return Const.BroadcastEvent.EVENT_GROUPS_UPDATED;
    }

    @Override
    public List<Group> loadInBackground() {
        if (mId != -1) {
            final Group g = Group.getGroupById(mId);
            return new ArrayList<Group>() {
                {
                    add(g);
                }
            };
        } else {
            return Group.getAllGroups();
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
