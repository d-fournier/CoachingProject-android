package fr.sims.coachingproject.loader.local;

import android.content.Context;

import java.util.List;

import fr.sims.coachingproject.loader.local.GenericLocalLoader;
import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.util.Const;

/**
 * Created by dfour on 11/02/2016.
 */
public class RelationListLoader extends GenericLocalLoader<List<CoachingRelation>> {

    public RelationListLoader(Context context) {
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
