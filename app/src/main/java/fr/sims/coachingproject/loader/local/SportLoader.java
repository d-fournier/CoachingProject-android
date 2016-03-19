package fr.sims.coachingproject.loader.local;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

import fr.sims.coachingproject.model.Sport;

/**
 * Created by Benjamin on 16/02/2016.
 */
public class SportLoader extends AsyncTaskLoader<List<Sport>> {

    List<Sport> mSportList;


    public SportLoader(Context context) {
        super(context);
        mSportList = null;
    }

    @Override
    public List<Sport> loadInBackground() {
        return Sport.getAllSports();
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
