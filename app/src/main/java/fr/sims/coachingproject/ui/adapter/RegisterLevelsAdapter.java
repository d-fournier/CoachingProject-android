package fr.sims.coachingproject.ui.adapter;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.model.SportLevel;

/**
 * Created by Segolene on 08/03/2016.
 */
public class RegisterLevelsAdapter extends ArrayAdapter {

    private Context mContext;

    private List<Sport> mAllSportsList = new ArrayList<>();
    private Map<Integer, SportLevel> mLevelsSelected = new ArrayMap<>();
    private Map<Integer, Sport> mSportSelected = new ArrayMap<>();
    private Map<Integer, List<SportLevel>> mLevelMap = new ArrayMap<>();

    private Map<Integer, View> mViewMap = new ArrayMap<>();

    private Sport mNoSelectionSport;
    private SportLevel mNoSelectionLevel;

    private OnDataChangedListener mListener;

    public RegisterLevelsAdapter(Context context, OnDataChangedListener listener) {
        super(context, R.layout.list_item_level);
        mListener = listener;
        mContext = context;

        final String NO_SPORT_SELECTED = "Select sport";
        mNoSelectionSport = new Sport();
        mNoSelectionSport.mIdDb = -1;
        mNoSelectionSport.mName = NO_SPORT_SELECTED;

        final String NO_LEVEL_SELECTED = "Select level";
        mNoSelectionLevel = new SportLevel();
        mNoSelectionLevel.mIdDb = -1;
        mNoSelectionLevel.mTitle = NO_LEVEL_SELECTED;
    }

    @Override
    public int getCount() {
        int counter = 0; // Number of real levels selected
        for (SportLevel level : mLevelsSelected.values()) {
            if (level.mIdDb != -1) {
                counter++;
            }
        }
        return counter + 1;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_level, null);
        final Spinner sportSpinner = (Spinner) convertView.findViewById(R.id.register_spinner_sport);
        final Spinner levelSpinner = (Spinner) convertView.findViewById(R.id.register_spinner_level);

        // Sports
        List<Sport> sports = new ArrayList<>();
        sports.add(mNoSelectionSport);
        sports.addAll(mAllSportsList);
        ArrayAdapter<Sport> sportAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, sports);
        sportSpinner.setAdapter(sportAdapter);
        if (mSportSelected.containsKey(position)) {
            sportSpinner.setSelection(sportAdapter.getPosition(mSportSelected.get(position)));
        }

        // Levels
        List<SportLevel> levels = new ArrayList<>();
        if (mLevelMap.containsKey(position)) {
            levels.addAll(mLevelMap.get(position));
        }else{
            levels.add(mNoSelectionLevel);
            mLevelMap.put(position, levels);
        }
        ArrayAdapter<SportLevel> levelAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, levels);
        levelSpinner.setAdapter(levelAdapter);
        if (mLevelsSelected.containsKey(position)) {
            levelSpinner.setSelection(levelAdapter.getPosition(mLevelsSelected.get(position)));
        }

        sportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                Sport sport = (Sport) spinner.getItemAtPosition(position);

                View repeatedView = (View) spinner.getParent();
                AdapterView adapterView = (AdapterView) repeatedView.getParent();
                int globalPos = adapterView.getPositionForView(repeatedView);

                Sport previousSport = mSportSelected.put(globalPos, sport);

                if (previousSport == null || previousSport.mIdDb != sport.mIdDb) {
                    // Sport changed or initialized
                    if (sport.mIdDb != -1) {
                        mListener.reloadLevels(sport.mIdDb);
                    } else if (sport.mIdDb == -1) {
                        // No sport selected
                        List<SportLevel> noLevels = new ArrayList<>();
                        noLevels.add(mNoSelectionLevel);
                        mLevelMap.put(globalPos, noLevels);
                        mLevelsSelected.put(globalPos, mNoSelectionLevel);
                         notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                SportLevel level = (SportLevel) spinner.getItemAtPosition(position);

                View repeatedView = (View) spinner.getParent();
                AdapterView adapterView = (AdapterView) repeatedView.getParent();
                int globalPos = adapterView.getPositionForView(spinner);

                SportLevel previousLevel = mLevelsSelected.put(globalPos, level);
                if (previousLevel == null || previousLevel.mIdDb != level.mIdDb) {
                     notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mViewMap.put(position, convertView);

        return convertView;
    }


    public void setSports(List<Sport> sportList) {
        mAllSportsList.clear();
        mAllSportsList.addAll(sportList);
        notifyDataSetChanged();
    }

    public void setLevels(long sportId, List<SportLevel> levels) {
        for (int pos : mSportSelected.keySet()) {
            if (mSportSelected.get(pos).mIdDb == sportId) {
                List<SportLevel> levelsToSave=new ArrayList<>();
                levelsToSave.add(mNoSelectionLevel);
                levelsToSave.addAll(levels);
                mLevelMap.put(pos, levelsToSave);
                mLevelsSelected.put(pos, mNoSelectionLevel);

                 notifyDataSetChanged();
                break;
            }
        }

    }


    public interface OnDataChangedListener {
        void reloadLevels(long sportId);
    }


}
