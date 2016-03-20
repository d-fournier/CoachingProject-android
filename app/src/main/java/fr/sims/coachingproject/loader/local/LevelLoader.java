package fr.sims.coachingproject.loader.local;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

import fr.sims.coachingproject.model.SportLevel;

/**
 * Created by Benjamin on 16/02/2016.
 */
public class LevelLoader extends AsyncTaskLoader<List<SportLevel>> {

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
            return SportLevel.getAllSportLevels();
        }
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

}
