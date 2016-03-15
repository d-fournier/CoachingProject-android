package fr.sims.coachingproject.loader.local;

import android.content.Context;

import java.util.List;

import fr.sims.coachingproject.loader.local.GenericLocalLoader;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.util.Const;

/**
 * Created by Benjamin on 01/03/2016.
 */
public class GroupLoader extends GenericLocalLoader<List<Group>> {

    public GroupLoader(Context context) {
        super(context);
    }

    @Override
    protected String getBroadcastEvent() {
        return Const.BroadcastEvent.EVENT_GROUPS_UPDATED;
    }

    @Override
    public List<Group> loadInBackground() {
            return Group.getAllGroups();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }


}
