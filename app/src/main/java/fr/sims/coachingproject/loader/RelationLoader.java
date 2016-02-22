package fr.sims.coachingproject.loader;

import android.content.Context;

import fr.sims.coachingproject.model.CoachingRelation;
import fr.sims.coachingproject.util.Const;

/**
 * Created by Segolene on 19/02/2016.
 */
public class RelationLoader  extends GenericLocalLoader<CoachingRelation> {

    private Context mCtx;
    private long mId;

    public RelationLoader(Context context, long id) {
        super(context);

        mId = id;
        mCtx = getContext();
    }


    @Override
    protected String getBroadcastEvent() {
        return Const.BroadcastEvent.EVENT_COACHING_RELATION_UPDATED;
    }

    @Override
    public CoachingRelation loadInBackground() {
        return CoachingRelation.getCoachingRelationById(mId);
    }
}
