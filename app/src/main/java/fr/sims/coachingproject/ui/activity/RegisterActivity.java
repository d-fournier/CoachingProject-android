package fr.sims.coachingproject.ui.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.network.LevelLoader;
import fr.sims.coachingproject.loader.network.SportLoader;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.model.SportLevel;
import fr.sims.coachingproject.util.Const;

/**
 * Created by Benjamin on 18/03/2016.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String SPORT_ID = "sportId";
    private static final String VIEW_POSITION = "viewPosition";
    private static final String ADD_LAYOUT = "addLayout";

    private static int MAX_LEVELS_NUMBER;

    private List<LinearLayout> mLevelViewsChildren;

    private int mAddedLevelsNumber;

    private LinearLayout mLevelViewsParent;

    private List<List<SportLevel>> mLevelsLists;
    private List<ArrayAdapter<SportLevel>> mLevelsAdapters;

    private List<Long> mLevelsSelected;

    private SportLoaderCallbacks mSportLoader;
    private List<Sport> mSportsList;

    private LevelsLoaderCallbacks mLevelLoader;
    private List<SportLevel> mSportLevelList;

    private ImageButton mAddLevelButton;
    private ImageButton mRemoveLevelButton;


    public static void startActivity(Context ctx) {
        Intent intent = new Intent(ctx, RegisterActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAddLevelButton = (ImageButton) findViewById(R.id.register_add_level_button);
        mAddLevelButton.setOnClickListener(this);
        mRemoveLevelButton = (ImageButton) findViewById(R.id.register_remove_level_button);
        mRemoveLevelButton.setOnClickListener(this);

        mLevelViewsParent = (LinearLayout) findViewById(R.id.register_levels_list);

        mLevelsLists = new ArrayList<>();
        mLevelsAdapters = new ArrayList<>();
        mLevelsSelected = new ArrayList<>();

        mSportsList = new ArrayList<>();
        mSportLevelList = new ArrayList<>();

        mSportLoader = new SportLoaderCallbacks();
        mLevelLoader = new LevelsLoaderCallbacks();

        mLevelViewsChildren = new ArrayList<>();

        mAddedLevelsNumber = 0;

        getLoaderManager().initLoader(Const.Loaders.SPORT_LOADER_ID, null, mSportLoader);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_add_level_button:
                if (mAddedLevelsNumber < MAX_LEVELS_NUMBER) {
                    mAddLevelButton.setEnabled(false);
                    mLevelViewsParent.addView(buildLevelView());
                }
                break;
            case R.id.register_remove_level_button:
                if (mAddedLevelsNumber > 0) {
                    mLevelViewsParent.removeViewAt(mAddedLevelsNumber - 1);
                    mLevelViewsChildren.remove(mAddedLevelsNumber - 1);
                    mLevelsLists.remove(mAddedLevelsNumber - 1);
                    mLevelsAdapters.remove(mAddedLevelsNumber - 1);
                    mAddedLevelsNumber--;
                    mAddLevelButton.setEnabled(true);
                    getLoaderManager().destroyLoader(Const.Loaders.LEVEL_LOADER_ID);
                }
                break;
        }
    }

    public LinearLayout buildLevelView() {
        LinearLayout levelView = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item_level, null);

        //On récupère les spinners
        Spinner sportsSpinner = (Spinner) levelView.findViewById(R.id.register_spinner_sport);
        Spinner levelsSpinner = (Spinner) levelView.findViewById(R.id.register_spinner_level);

        //On set l'adapter sur le spinner des sports
        ArrayAdapter<Sport> sportAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, mSportsList);
        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportsSpinner.setAdapter(sportAdapter);
        sportsSpinner.setSelection(mAddedLevelsNumber);

        //On set l'adapter sur le spinner des levels
        mLevelsLists.add(new ArrayList<SportLevel>());
        mLevelsSelected.add((long) -1);
        ArrayAdapter<SportLevel> levelAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, mLevelsLists.get(mAddedLevelsNumber));
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelsSpinner.setAdapter(levelAdapter);

        //On garde des références vers les adapters de chaque spinner
        mLevelsAdapters.add(levelAdapter);

        //On crée un Bundle qu'on passe au loader de levels avec l'ID du sport, et la position de la vue
        Bundle args = new Bundle();
        args.putLong(SPORT_ID, mSportsList.get(0).mIdDb);
        args.putInt(VIEW_POSITION, mAddedLevelsNumber);
        getLoaderManager().restartLoader(Const.Loaders.LEVEL_LOADER_ID, args, mLevelLoader);

        sportsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Ici on met à jour la liste des niveaux de la bonne vue
                Bundle args = new Bundle();
                args.putLong(SPORT_ID, ((Sport) parent.getSelectedItem()).mIdDb);
                LinearLayout levelView = (LinearLayout) parent.getParent();
                int viewPosition = mLevelViewsChildren.indexOf(levelView);
                args.putInt(VIEW_POSITION, viewPosition);
                getLoaderManager().restartLoader(Const.Loaders.LEVEL_LOADER_ID, args, mLevelLoader);

                //Ici on check si on selectionne 2 fois le même sport
                Sport currentSport = (Sport) parent.getSelectedItem();
                for (LinearLayout l : mLevelViewsChildren) {
                    Spinner sportSpinner = (Spinner) l.findViewById(R.id.register_spinner_sport);
                    Sport selectedSport = (Sport) sportSpinner.getSelectedItem();
                    if (selectedSport == currentSport && mLevelViewsChildren.indexOf(l) != viewPosition) {
                        Snackbar.make(mLevelViewsParent, "You cannot have two levels in the same sport", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        levelsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout levelView = (LinearLayout) parent.getParent();
                int viewPosition = mLevelViewsChildren.indexOf(levelView);
                mLevelsSelected.set(viewPosition,((SportLevel) parent.getSelectedItem()).mIdDb);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mLevelViewsChildren.add(levelView);

        mAddedLevelsNumber++;

        return levelView;
    }

    class SportLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Sport>> {

        @Override
        public Loader<List<Sport>> onCreateLoader(int id, Bundle args) {
            return new SportLoader(getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<List<Sport>> loader, List<Sport> data) {
            if (data != null) {
                mSportsList.clear();
                mSportsList.addAll(data);
                MAX_LEVELS_NUMBER = mSportsList.size();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Sport>> loader) {

        }
    }

    class LevelsLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<SportLevel>> {

        private int mViewPosition;

        @Override
        public Loader<List<SportLevel>> onCreateLoader(int id, Bundle args) {
            mViewPosition = args.getInt(VIEW_POSITION, -1);

            return new LevelLoader(getApplicationContext(), args.getLong(SPORT_ID, -1));
        }

        @Override
        public void onLoadFinished(Loader<List<SportLevel>> loader, List<SportLevel> data) {
            try {
                if (data != null) {
                    mLevelsLists.get(mViewPosition).clear();
                    mLevelsLists.get(mViewPosition).addAll(data);
                    mLevelsAdapters.get(mViewPosition).notifyDataSetChanged();
                }
            } catch (IndexOutOfBoundsException e) {

            }finally{
                mAddLevelButton.setEnabled(true);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<SportLevel>> loader) {

        }
    }
}
