package fr.sims.coachingproject.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.Arrays;
import java.util.List;

import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;

/**
 * Created by Benjamin on 01/03/2016.
 */
public class GroupLoader  extends AsyncTaskLoader<List<Group>> {

    public GroupLoader(Context context) {
        super(context);
    }

    @Override
    public List<Group> loadInBackground() {
        String request = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS;
        String response = NetworkUtil.get(request, null);
        if(response.isEmpty()){
            return null;
        }
        return Arrays.asList(Group.parseList(response));
    }
}
