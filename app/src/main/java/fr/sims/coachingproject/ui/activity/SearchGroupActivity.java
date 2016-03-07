package fr.sims.coachingproject.ui.activity;


import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.GroupSearchLoader;
import fr.sims.coachingproject.loader.SportLoader;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.model.SportLevel;
import fr.sims.coachingproject.ui.adapter.SearchGroupListAdapter;
import fr.sims.coachingproject.ui.adapter.PlacesAutoCompleteAdapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import fr.sims.coachingproject.util.Const;


/**
 * Created by Anthony Barbosa on 16/02/2016.
 */

public class SearchGroupActivity extends AppCompatActivity implements SearchGroupListAdapter.OnItemClickListener, OnItemClickListener {

    EditText mSearchInput;

    RecyclerView mRecycleView;
    Button mSearchButton;
    Spinner mSportsSpinner;
    List<Group> mGroupList;
    List<Sport> mSportList;
    SearchGroupListAdapter mSearchGroupListAdapter;
    Bundle mSearchArgs;
    ProgressBar mLoadingBar;
    TextView mEmptyCoachListText;
    GroupsLoaderCallbacks mGroupLoader;
    SportLoaderCallbacks mSportLoader;
    ArrayAdapter<Sport> mSportsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_search);

        final AutoCompleteTextView mAutoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        mAutoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item_places));
        mAutoCompView.setOnItemClickListener(this);

        mLoadingBar = (ProgressBar) findViewById(R.id.loading_progress_bar);

        mSearchArgs = new Bundle();

        mGroupList = new ArrayList<>();
        mSportList = new ArrayList<>();

        mRecycleView = (RecyclerView) findViewById(R.id.Search_List);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mSearchGroupListAdapter = new SearchGroupListAdapter();
        mSearchGroupListAdapter.setOnItemClickListener(this);
        mRecycleView.setAdapter(mSearchGroupListAdapter);

        mSportsSpinner = (Spinner) findViewById(R.id.spinner_sports);
        mSportsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mSportList);
        mSportsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSportsSpinner.setAdapter(mSportsAdapter);


        mSearchInput = (EditText) findViewById(R.id.inputSearch);
        mSearchButton = (Button) findViewById(R.id.search_button);
        mEmptyCoachListText = (TextView) findViewById(R.id.no_coach_text);


        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchArgs.putCharSequence("searchText", mSearchInput.getText().toString());
                mSearchArgs.putLong("idSport", ((Sport) mSportsSpinner.getSelectedItem()).getmIdDb());
                mSearchArgs.putCharSequence("searchPlace",  mAutoCompView.getText().toString());
                getLoaderManager().restartLoader(0, mSearchArgs, mGroupLoader);
            }
        });

        mSportLoader = new SportLoaderCallbacks();
        mGroupLoader = new GroupsLoaderCallbacks();
        getLoaderManager().initLoader(0, mSearchArgs, mGroupLoader);
        getLoaderManager().initLoader(1, mSearchArgs, mSportLoader);
    }

    @Override
    public void onItemClick(View view, int position) {
        // GroupActivity.startActivity(this, mGroupList.get(position).mIdDb);
    }

    @Override
    public void onItemLongClick(View view, int position) {
    }


    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        String str = (String) parent.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

    }

    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(Const.WebServer.PLACES_API_BASE + Const.WebServer.TYPE_AUTOCOMPLETE + Const.WebServer.OUT_JSON);
            sb.append("?key=" + Const.WebServer.GOOGLE_API_KEY);
            sb.append("&types=(cities)");
            sb.append("&components=country:fr");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return resultList;
        } catch (IOException e) {
            e.printStackTrace();
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    class GroupsLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Group>> {


        @Override
        public Loader<List<Group>> onCreateLoader(int id, Bundle args) {
            mLoadingBar.setVisibility(View.VISIBLE);
            mRecycleView.setVisibility(View.GONE);
            mEmptyCoachListText.setVisibility(View.GONE);
            return new GroupSearchLoader(getApplicationContext(), mSearchArgs.getString("searchText", ""), mSearchArgs.getLong("idSport", -1), mSearchArgs.getString("searchPlace",""));
        }

        @Override
        public void onLoadFinished(Loader<List<Group>> loader, List<Group> data) {
            mGroupList = data;
            mSearchGroupListAdapter.setData(mGroupList);
            mLoadingBar.setVisibility(View.GONE);

            if (mGroupList.isEmpty()) {
                mRecycleView.setVisibility(View.GONE);
                mEmptyCoachListText.setVisibility(View.VISIBLE);
            } else {
                mRecycleView.setVisibility(View.VISIBLE);
                mEmptyCoachListText.setVisibility(View.GONE);
            }

        }

        @Override
        public void onLoaderReset(Loader<List<Group>> loader) {

        }


    }

    class SportLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Sport>> {

        @Override
        public Loader<List<Sport>> onCreateLoader(int id, Bundle args) {
            return new SportLoader(getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<List<Sport>> loader, List<Sport> data) {
            mSportList = data;
            mSportsAdapter.clear();

            //Create fake sport for "All sports" with ID -1
            Sport allSports = new Sport();
            allSports.mName = getString(R.string.all_sports);
            allSports.mIdDb = -1;
            mSportsAdapter.add(allSports);

            //Add all sports got from server
            mSportsAdapter.addAll(mSportList);


        }

        @Override
        public void onLoaderReset(Loader<List<Sport>> loader) {

        }


    }
}
