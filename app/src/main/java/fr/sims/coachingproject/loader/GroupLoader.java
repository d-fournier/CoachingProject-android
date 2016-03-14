package fr.sims.coachingproject.loader;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

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
