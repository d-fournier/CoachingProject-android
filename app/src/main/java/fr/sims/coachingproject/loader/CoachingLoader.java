package fr.sims.coachingproject.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.util.Const;

/**
 * Created by dfour on 11/02/2016.
 */
public class CoachingLoader extends GenericLocalLoader<List<CoachingRelation>> {

    public CoachingLoader(Context context) {
        super(context);
    }

    @Override
    protected String getBroadcastEvent() {
        return Const.BroadcastEvent.EVENT_COACHING_RELATIONS_UPDATED;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        forceLoad();
    }

    @Override
    public List<CoachingRelation> loadInBackground() {
        // TODO Handle Only custom coaching relation
        List<CoachingRelation> r = CoachingRelation.getAllCoachingRelation();
        return r;
    }
}
