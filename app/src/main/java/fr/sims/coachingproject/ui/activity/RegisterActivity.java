package fr.sims.coachingproject.ui.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.LevelLoader;
import fr.sims.coachingproject.loader.SportLoader;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.model.SportLevel;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.model.fakejson.LoginRequest;
import fr.sims.coachingproject.model.fakejson.LoginResponse;
import fr.sims.coachingproject.service.gcmService.RegistrationGCMIntentService;
import fr.sims.coachingproject.ui.adapter.RegisterLevelsAdapter;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;


/**
 * Created by Segolene on 08/03/2016.
 */
public class RegisterActivity extends AppCompatActivity implements RegisterLevelsAdapter.OnDataChangedListener {

    private final int SPORT_LOADER_ID=0;
    private final String ARG_SPORT_ID="sportId";
    private final String ARG_VIEW_HOLDER="viewHolder";

    private DatePickerFragment mDateFragment;
    private ListView mLevelView;
    private RegisterLevelsAdapter mLevelAdapter;

    private SportLoaderCallbacks mSportLoader;
    private LevelsLoaderCallbacks mLevelLoader;

    private List<Sport> mSportList=new ArrayList<>();

    private UserRegisterTask mRegisterTask = null;

    public static void startActivity(Context ctx){
        Intent intent = new Intent(ctx,RegisterActivity.class);
        ctx.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mLevelView = (ListView) findViewById(R.id.register_levels_list);
        mLevelAdapter = new RegisterLevelsAdapter(this, this);
        mLevelView.setAdapter(mLevelAdapter);

        mSportLoader = new SportLoaderCallbacks();
        getLoaderManager().initLoader(SPORT_LOADER_ID, null, mSportLoader);
        mLevelLoader = new LevelsLoaderCallbacks();

    }


    public void register(View v){
        if(mDateFragment.mDate!=null){
            EditText usernameView=(EditText)findViewById(R.id.register_username);
            EditText displaynameView=(EditText)findViewById(R.id.register_display_name);
            EditText passwordView=(EditText)findViewById(R.id.register_password);
            EditText repeatPasswordView=(EditText)findViewById(R.id.register_password_repeat);
            EditText emailView=(EditText)findViewById(R.id.register_email);
            EditText cityView=(EditText)findViewById(R.id.register_city);
            CheckBox isCoachView=(CheckBox)findViewById(R.id.register_is_coach);
            EditText descriptionView=(EditText)findViewById(R.id.register_description);

            if (mRegisterTask != null) {
                return;
            }

            // Reset errors.
            usernameView.setError(null);
            passwordView.setError(null);
            repeatPasswordView.setError(null);
            emailView.setError(null);

            // Store values at the time of the login attempt.
            String username = usernameView.getText().toString();
            String password = passwordView.getText().toString();
            String repeatPassword=repeatPasswordView.getText().toString();
            String email=emailView.getText().toString();

            boolean cancel = false;
            View focusView = null;

            // Check for a valid password, if the user entered one.
            if (TextUtils.isEmpty(password)) {
                passwordView.setError(getString(R.string.error_invalid_password));
                focusView = passwordView;
                cancel = true;
            }

            if(!TextUtils.equals(password, repeatPassword )){
                repeatPasswordView.setError(getString(R.string.passwords_dont_match));
                focusView=repeatPasswordView;
                cancel=true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(username)) {
                usernameView.setError(getString(R.string.error_field_required));
                focusView = usernameView;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                emailView.setError(getString(R.string.error_field_required));
                focusView = emailView;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                int[] levels={1,2};
                mRegisterTask = new UserRegisterTask(username, password, email, displaynameView.getText().toString(),
                        mDateFragment.mDate, cityView.getText().toString(), descriptionView.getText().toString(), isCoachView.isSelected(), levels);
                mRegisterTask.execute((Void) null);
            }
        }

    }

    public void showDatePickerDialog(View v) {
        mDateFragment = new DatePickerFragment();

        mDateFragment.show(getSupportFragmentManager(), "datePicker");

    }


    @Override
    public void reloadLevels(long sportId) {
        // Loader id specific to sport and different from SPORT_LOADER_ID
        int loaderId = (int)sportId + SPORT_LOADER_ID + 1;

        Bundle args = new Bundle();
        args.putLong(ARG_SPORT_ID, sportId);

        if(getLoaderManager().getLoader(loaderId)!=null){
            getLoaderManager().restartLoader(loaderId, args,mLevelLoader);
        }else{
            getLoaderManager().initLoader(loaderId,args,mLevelLoader);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private Date mDate=null;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            Calendar cal=Calendar.getInstance();
            cal.set(year, month, day);
            mDate=cal.getTime();
            Button dateButton = (Button)getActivity().findViewById(R.id.register_date);
            SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
            dateButton.setText(sdf.format(mDate));
        }
    }


    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private JSONObject userInfos;

        UserRegisterTask(String username, String password, String email, String displayName, Date birthdate, String city,String description, boolean isCoach, int[] sportLevels) {
            userInfos=new JSONObject();
            try {
                userInfos.put("username", username);
                userInfos.put("password", password);
                userInfos.put("email", email);
                userInfos.put("displayName", displayName);
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                userInfos.put("birthdate", sdf.format(birthdate));
                userInfos.put("city", city);
                userInfos.put("description", description);
                userInfos.put("isCoach", isCoach);
                JSONArray levelsArray=new JSONArray();
                for(int i=0; i<sportLevels.length;i++){
                    levelsArray.put(sportLevels[i]);
                }
                userInfos.put("levels", levelsArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            NetworkUtil.Response response_token = NetworkUtil.post(Const.WebServer.DOMAIN_NAME + Const.WebServer.AUTH + Const.WebServer.REGISTER, null, userInfos.toString());
            if (!response_token.isSuccessful())
                return false;

            String username;
            String password;
            try {
                username = userInfos.get("username").toString();
                password = userInfos.get("password").toString();
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

            NetworkUtil.Response login_token = NetworkUtil.post(Const.WebServer.DOMAIN_NAME + Const.WebServer.AUTH + Const.WebServer.LOGIN, null, (new LoginRequest(username,password)).toJson());
            if (!login_token.isSuccessful())
                return false;

            LoginResponse lResponse = LoginResponse.fromJson(login_token.getBody());

            NetworkUtil.Response response_user = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.USER_PROFILE + Const.WebServer.ME,
                    lResponse.token);
            if (!response_user.isSuccessful())
                return false;

            UserProfile up = UserProfile.parseItem(response_user.getBody());
            up.saveOrUpdate();

            SharedPrefUtil.putConnectedToken(getApplicationContext(), lResponse.token);
            SharedPrefUtil.putConnectedUserId(getApplicationContext(), up.mIdDb);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;

            if (success) {
                RegistrationGCMIntentService.startActionRegistrationGCM(getApplicationContext());
                MainActivity.startActivity(getApplicationContext());
            } else {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.register_layout), R.string.register_failed, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
        }
    }

    class SportLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Sport>> {

        @Override
        public Loader<List<Sport>> onCreateLoader(int id, Bundle args) {
            return new SportLoader(getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<List<Sport>> loader, List<Sport> data) {
            if(data!=null){
                mSportList = data;
            }else{
                mSportList.clear();
            }
            mLevelAdapter.setSports(mSportList);
        }

        @Override
        public void onLoaderReset(Loader<List<Sport>> loader) {

        }
    }

    class LevelsLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<SportLevel>> {

        @Override
        public Loader<List<SportLevel>> onCreateLoader(int id, Bundle args) {
            return new LevelLoader(getApplicationContext(), args.getLong(ARG_SPORT_ID, -1));
        }

        @Override
        public void onLoadFinished(Loader<List<SportLevel>> loader, List<SportLevel> data) {
            LevelLoader levelLoader=(LevelLoader)loader;
            mLevelAdapter.setLevels(levelLoader.mSport, data);
        }

        @Override
        public void onLoaderReset(Loader<List<SportLevel>> loader) {

        }
    }
}
