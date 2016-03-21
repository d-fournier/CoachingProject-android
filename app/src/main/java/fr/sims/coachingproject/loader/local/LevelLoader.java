package fr.sims.coachingproject.loader.local;

import android.content.Context;

import java.util.List;

import fr.sims.coachingproject.model.SportLevel;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.util.Const;

/**
 * Created by Benjamin on 16/02/2016.
 */
public class LevelLoader extends GenericLocalLoader<List<SportLevel>> {

    List<SportLevel> mLevelsList;
    public long mSport;

    public LevelLoader(Context context, long sport) {
        super(context);
        mLevelsList = null;
        mSport = sport;
    }

    @Override
    public List<SportLevel> loadInBackground() {
        if (mSport != -1) {
            return SportLevel.getAllSportLevelsBySportId(mSport);
        }else{
            List<SportLevel> listSportLevels = SportLevel.getAllSportLevels();
            if(listSportLevels == null || listSportLevels.isEmpty()){
                NetworkService.startActionSportsLevels(getContext());
                return null;
            }else{
                return listSportLevels;
            }
        }
    }


    @Override
    protected String getBroadcastEvent() {
        return Const.BroadcastEvent.EVENT_LEVELS_UPDATED;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

}
