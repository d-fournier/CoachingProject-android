package fr.sims.coachingproject.loader.local;

import android.content.Context;

import java.util.List;

import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.util.Const;

/**
 * Created by Benjamin on 16/02/2016.
 */
public class SportLoader extends GenericLocalLoader<List<Sport>> {

    List<Sport> mSportList;


    public SportLoader(Context context) {
        super(context);
        mSportList = null;
    }

    @Override
    protected String getBroadcastEvent() {
        return Const.BroadcastEvent.EVENT_SPORTS_UPDATED;
    }

    @Override
    public List<Sport> loadInBackground() {
        List<Sport> listSport = Sport.getAllSports();
        if(listSport == null || listSport.isEmpty()){
            NetworkService.startActionSportsLevels(getContext());
            return null;
        }else{
            return listSport;
        }
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
