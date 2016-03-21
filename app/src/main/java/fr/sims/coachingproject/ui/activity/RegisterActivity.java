package fr.sims.coachingproject.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.local.LevelLoader;
import fr.sims.coachingproject.loader.local.SportLoader;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.model.SportLevel;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.model.fakejson.LoginRequest;
import fr.sims.coachingproject.model.fakejson.LoginResponse;
import fr.sims.coachingproject.service.gcmService.RegistrationGCMIntentService;
import fr.sims.coachingproject.ui.adapter.CityAutoCompleteAdapter;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.MultipartUtility;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by Benjamin on 18/03/2016.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String SPORT_ID = "sportId";
    private static final String VIEW_POSITION = "viewPosition";

    private static int MAX_LEVELS_NUMBER;

    private List<LinearLayout> mLevelViewsChildren;

    private int mAddedLevelsNumber;

    private LinearLayout mLevelViewsParent;

    private List<List<SportLevel>> mLevelsLists;
    private List<ArrayAdapter<SportLevel>> mLevelsAdapters;

    private List<Long> mLevelsSelected;
    private boolean mSameSportSelectedTwice;

    private SportLoaderCallbacks mSportLoader;
    private List<Sport> mSportsList;

    private LevelsLoaderCallbacks mLevelLoader;
    private List<SportLevel> mSportLevelList;

    private Button mAddLevelButton;
    private Button mRemoveLevelButton;
    private Button mRegisterButton;
    private ImageView mUploadedImage;
    private Button mDateButton;

    private Uri mUploadFileUri;
    private String mFileName;


    public static void startActivity(Context ctx) {
        Intent intent = new Intent(ctx, RegisterActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        AutoCompleteTextView cityView = (AutoCompleteTextView) findViewById(R.id.register_city);
        cityView.setAdapter(new CityAutoCompleteAdapter(this, android.R.layout.simple_list_item_1));

        mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(this);
        mUploadedImage = (ImageView) findViewById(R.id.register_profile_image);
        mUploadedImage.setOnClickListener(this);
        mDateButton = (Button) findViewById(R.id.register_date_button);
        mDateButton.setOnClickListener(this);

        mAddLevelButton = (Button) findViewById(R.id.register_add_level_button);
        mAddLevelButton.setOnClickListener(this);
        mRemoveLevelButton = (Button) findViewById(R.id.register_remove_level_button);
        mRemoveLevelButton.setOnClickListener(this);

        mLevelViewsParent = (LinearLayout) findViewById(R.id.register_levels_list);

        mLevelsLists = new ArrayList<>();
        mLevelsAdapters = new ArrayList<>();
        mLevelsSelected = new ArrayList<>();
        mSameSportSelectedTwice = false;

        mSportsList = new ArrayList<>();
        mSportLevelList = new ArrayList<>();

        mSportLoader = new SportLoaderCallbacks();
        mLevelLoader = new LevelsLoaderCallbacks();

        mLevelViewsChildren = new ArrayList<>();

        mAddedLevelsNumber = 0;

        mUploadFileUri = null;

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
                    //We remove the last element of all our lists
                    mLevelViewsParent.removeViewAt(mAddedLevelsNumber - 1);//This is the parent layout
                    mLevelViewsChildren.remove(mAddedLevelsNumber - 1);//This is the list of layouts containing the two spinners
                    mLevelsLists.remove(mAddedLevelsNumber - 1);//This is the list of levels list in each spinner of levels
                    mLevelsAdapters.remove(mAddedLevelsNumber - 1);//This is the list of all the adapters for each spinner of levels
                    mLevelsSelected.remove(mAddedLevelsNumber - 1);//This is the list of the all the selected levels
                    mAddedLevelsNumber--;
                    mAddLevelButton.setEnabled(true);
                    checkSelectedSports();
                    getLoaderManager().destroyLoader(Const.Loaders.LEVEL_LOADER_ID);
                }
                break;
            case R.id.register_button:
                register();
                break;
            case R.id.register_date_button:
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, monthOfYear, dayOfMonth);
                        Date date = cal.getTime();
                        mDateButton.setText((new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)).format(date));
                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE)).show();
                break;
            case R.id.register_profile_image:
                selectImage();
                break;
        }
    }

    public LinearLayout buildLevelView() {
        LinearLayout levelView = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item_level, null);

        //On récupère les spinners
        Spinner sportsSpinner = (Spinner) levelView.findViewById(R.id.register_spinner_sport);
        Spinner levelsSpinner = (Spinner) levelView.findViewById(R.id.register_spinner_level);

        //On set l'adapter sur le spinner des sports
        ArrayAdapter<Sport> sportAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_element, mSportsList);
        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportsSpinner.setAdapter(sportAdapter);
        sportsSpinner.setSelection(mAddedLevelsNumber);

        //On set l'adapter sur le spinner des levels
        mLevelsLists.add(new ArrayList<SportLevel>());
        mLevelsSelected.add((long) -1);
        ArrayAdapter<SportLevel> levelAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_element, mLevelsLists.get(mAddedLevelsNumber));
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
                        Snackbar.make(mLevelViewsParent, R.string.same_level_selected, Snackbar.LENGTH_LONG).show();
                        mSameSportSelectedTwice = true;
                        return;
                    }
                }
                mSameSportSelectedTwice = false;
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
                mLevelsSelected.set(viewPosition, ((SportLevel) parent.getSelectedItem()).mIdDb);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mLevelViewsChildren.add(levelView);

        mAddedLevelsNumber++;

        return levelView;
    }

    public void checkSelectedSports() {
        List<Sport> sportSelectedList = new ArrayList<>();
        for (LinearLayout layout : mLevelViewsChildren) {
            Spinner firstSportSpinner = (Spinner) layout.findViewById(R.id.register_spinner_sport);
            sportSelectedList.add((Sport) firstSportSpinner.getSelectedItem());
        }
        for(Sport firstSport : sportSelectedList){
            for(Sport secondSport : sportSelectedList){
                if(firstSport == secondSport && sportSelectedList.indexOf(firstSport) != sportSelectedList.indexOf(secondSport)){
                    mSameSportSelectedTwice = true;
                    return;
                }
            }
        }
        mSameSportSelectedTwice = false;


    }


    //----------------------------------------------------------METHOD FROM PREVIOUS REGISTRATION--------------------------------------------------------------
    public void selectImage() {
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), Const.WebServer.PICK_IMAGE_REQUEST);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, Const.WebServer.PICK_IMAGE_AFTER_KITKAT_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (null == data) return;
        if (requestCode == Const.WebServer.PICK_IMAGE_REQUEST) {
            mUploadFileUri = data.getData();
        } else if (requestCode == Const.WebServer.PICK_IMAGE_AFTER_KITKAT_REQUEST) {
            mUploadFileUri = data.getData();
            final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            // Check for the freshest data.
            getContentResolver().takePersistableUriPermission(mUploadFileUri, takeFlags);
        }
        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.
        Cursor cursor = this.getContentResolver()
                .query(mUploadFileUri, null, null, null, null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {
                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                mFileName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        } finally {
            cursor.close();
        }

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mUploadFileUri);
            ImageView imageView = (ImageView) findViewById(R.id.register_profile_image);
            imageView.setImageBitmap(bitmap);
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register() {

        if (mSameSportSelectedTwice) {
            Snackbar.make(mLevelViewsParent, R.string.same_level_selected, Snackbar.LENGTH_LONG).show();
            return;
        }
        String username, displayName, password, repeatPassword, email, city, description, date;

        if ((username = ((EditText) findViewById(R.id.register_username)).getText().toString()).isEmpty()) {
            Snackbar.make(mLevelViewsParent, R.string.username_empty, Snackbar.LENGTH_LONG).show();
            return;
        }
        if ((displayName = ((EditText) findViewById(R.id.register_display_name)).getText().toString()).isEmpty()) {
            Snackbar.make(mLevelViewsParent, R.string.display_name_empty, Snackbar.LENGTH_LONG).show();
            return;
        }
        if ((password = ((EditText) findViewById(R.id.register_password)).getText().toString()).isEmpty()) {
            Snackbar.make(mLevelViewsParent, R.string.password_empty, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!(repeatPassword = ((EditText) findViewById(R.id.register_password)).getText().toString()).equals(password)) {
            Snackbar.make(mLevelViewsParent, R.string.passwords_dont_match, Snackbar.LENGTH_LONG).show();
            return;
        }
        if ((email = ((EditText) findViewById(R.id.register_email)).getText().toString()).isEmpty()) {
            Snackbar.make(mLevelViewsParent, R.string.email_empty, Snackbar.LENGTH_LONG).show();
            return;
        }
        if ((city = ((EditText) findViewById(R.id.register_city)).getText().toString()).isEmpty()) {
            Snackbar.make(mLevelViewsParent, R.string.city_empty, Snackbar.LENGTH_LONG).show();
            return;
        }

        description = ((EditText) findViewById(R.id.register_description)).getText().toString();
        date = (String) mDateButton.getText();

        (new UserRegisterTask(username, password, email, displayName, date, city, description, ((CheckBox) findViewById(R.id.register_is_coach)).isChecked(), mLevelsSelected)).execute();
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        String mUsername;
        String mPassword;
        private JSONObject mUserInfos;

        public UserRegisterTask(String username, String password, String email, String displayName, String date, String city, String description, boolean isCoach, List<Long> mLevelsSelected) {
            mUsername = username;
            mPassword = password;
            mUserInfos = new JSONObject();
            try {
                mUserInfos.put("username", username);
                mUserInfos.put("password", password);
                mUserInfos.put("email", email);
                mUserInfos.put("displayName", displayName);
                mUserInfos.put("isCoach", isCoach);
                mUserInfos.put("city", city);

                if (!date.isEmpty()) mUserInfos.put("birthdate", date);
                if (!description.isEmpty()) mUserInfos.put("description", description);

                JSONArray levelsArray = new JSONArray();
                for (Long levelId : mLevelsSelected) {
                    levelsArray.put(levelId);
                }
                if (levelsArray.length() > 0) mUserInfos.put("levels", levelsArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Register
            NetworkUtil.Response response_register = NetworkUtil.post(Const.WebServer.DOMAIN_NAME + Const.WebServer.AUTH + Const.WebServer.REGISTER, null, mUserInfos.toString());
            if (!response_register.isSuccessful())
                return false;

            // Log in
            NetworkUtil.Response response_login = NetworkUtil.post(Const.WebServer.DOMAIN_NAME + Const.WebServer.AUTH + Const.WebServer.LOGIN, null, (new LoginRequest(mUsername, mPassword)).toJson());
            if (!response_login.isSuccessful())
                return false;

            String token = (LoginResponse.fromJson(response_login.getBody())).token;

            //Get user
            NetworkUtil.Response response_user = NetworkUtil.get(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.USER_PROFILE + Const.WebServer.ME,
                    token);
            if (!response_user.isSuccessful())
                return false;

            UserProfile up = UserProfile.parseItem(response_user.getBody());
            up.saveOrUpdate();

            SharedPrefUtil.putConnectedToken(getApplicationContext(), token);
            SharedPrefUtil.putConnectedUserId(getApplicationContext(), up.mIdDb);

            if(mUploadFileUri!=null) {
                //Upload image
                String url = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.USER_PROFILE + up.mIdDb + Const.WebServer.SEPARATOR;
                try {
                    MultipartUtility multipart = new MultipartUtility(url, "UTF-8", "Token " + SharedPrefUtil.getConnectedToken(getApplicationContext()), "PATCH");
                    InputStream in = getContentResolver().openInputStream(mUploadFileUri);
                    multipart.addFilePart("picture", in, mFileName);
                    NetworkUtil.Response response_image = multipart.finish();

                    return response_image.isSuccessful();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                RegistrationGCMIntentService.startActionRegistrationGCM(getApplicationContext());
                SharedPrefUtil.putIsFirstLaunch(getApplicationContext(), false);
                MainActivity.startActivity(getApplicationContext());
                finish();
            } else {
                Snackbar.make(mLevelViewsParent, R.string.register_failed, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------


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
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_connectivity), Toast.LENGTH_LONG).show();
                finish();
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

            } finally {
                mAddLevelButton.setEnabled(true);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<SportLevel>> loader) {

        }
    }
}
