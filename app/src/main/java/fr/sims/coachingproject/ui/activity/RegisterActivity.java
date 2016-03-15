package fr.sims.coachingproject.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.LevelLoader;
import fr.sims.coachingproject.loader.SportLoader;
import fr.sims.coachingproject.model.Sport;
import fr.sims.coachingproject.model.SportLevel;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.model.fakejson.LoginRequest;
import fr.sims.coachingproject.model.fakejson.LoginResponse;
import fr.sims.coachingproject.service.gcmService.RegistrationGCMIntentService;
import fr.sims.coachingproject.ui.adapter.CityAutoCompleteAdapter;
import fr.sims.coachingproject.ui.adapter.RegisterLevelsAdapter;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.MultipartUtility;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;


/**
 * Created by Segolene on 08/03/2016.
 */
public class RegisterActivity extends AppCompatActivity implements RegisterLevelsAdapter.OnDataChangedListener {

    private final int SPORT_LOADER_ID = 0;
    private final String ARG_SPORT_ID = "sportId";

    private DatePickerFragment mDateFragment;
    private LinearLayout mLevelView;
    private RegisterLevelsAdapter mLevelAdapter;

    private SportLoaderCallbacks mSportLoader;
    private LevelsLoaderCallbacks mLevelLoader;

    private List<Sport> mSportList = new ArrayList<>();
    private String mImageUrl;

    private UserRegisterTask mRegisterTask = null;

    public static void startActivity(Context ctx) {
        Intent intent = new Intent(ctx, RegisterActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mLevelView = (LinearLayout) findViewById(R.id.register_levels_list);
        mLevelAdapter = new RegisterLevelsAdapter(this, this);

        AutoCompleteTextView cityView = (AutoCompleteTextView) findViewById(R.id.register_city);
        cityView.setAdapter(new CityAutoCompleteAdapter(this, android.R.layout.simple_list_item_1));

        mSportLoader = new SportLoaderCallbacks();
        getLoaderManager().initLoader(SPORT_LOADER_ID, null, mSportLoader);
        mLevelLoader = new LevelsLoaderCallbacks();

        mDateFragment = new DatePickerFragment();

        mImageUrl = "";

        displayError(false);

        reloadView();

    }


    public void register(View v) {
        EditText usernameView = (EditText) findViewById(R.id.register_username);
        EditText displaynameView = (EditText) findViewById(R.id.register_display_name);
        EditText passwordView = (EditText) findViewById(R.id.register_password);
        EditText repeatPasswordView = (EditText) findViewById(R.id.register_password_repeat);
        EditText emailView = (EditText) findViewById(R.id.register_email);
        AutoCompleteTextView cityView = (AutoCompleteTextView) findViewById(R.id.register_city);
        CheckBox isCoachView = (CheckBox) findViewById(R.id.register_is_coach);
        EditText descriptionView = (EditText) findViewById(R.id.register_description);

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
        String displayName = displaynameView.getText().toString();
        String password = passwordView.getText().toString();
        String repeatPassword = repeatPasswordView.getText().toString();
        String email = emailView.getText().toString();
        String city = cityView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username.
        if (TextUtils.isEmpty(username) && username.matches("/[a-zA-Z0-9@.+-_]+/")) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }

        // Check for a valid display name.
        if (TextUtils.isEmpty(displayName)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = displaynameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (!TextUtils.equals(password, repeatPassword)) {
            repeatPasswordView.setError(getString(R.string.passwords_dont_match));
            focusView = repeatPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        }

        // Check for a valid city.
        if (TextUtils.isEmpty(city)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = cityView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            String date = "";
            if (mDateFragment.mDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                date = sdf.format(mDateFragment.mDate);
            }

            mRegisterTask = new UserRegisterTask(username, password, email, displayName,
                    date, city, descriptionView.getText().toString(), isCoachView.isChecked(), mLevelAdapter.getLevelsSelectedIds());
            mRegisterTask.execute((Void) null);
        }
    }

    public void displayError(Boolean errorVisible) {
        LinearLayout content = (LinearLayout) findViewById(R.id.register_content);
        LinearLayout error = (LinearLayout) findViewById(R.id.register_error);

        if (errorVisible) {
            content.setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
        } else {
            content.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
        }

    }

    public void retryToConnect(View view) {
        getLoaderManager().restartLoader(SPORT_LOADER_ID, null, mSportLoader);
        reloadView();
    }

    public void showDatePickerDialog(View v) {
        mDateFragment.show(getSupportFragmentManager(), "datePicker");

    }

    public void selectImage(View view) {
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
        Uri originalUri = null;
        if (requestCode == Const.WebServer.PICK_IMAGE_REQUEST) {
            originalUri = data.getData();
        } else if (requestCode == Const.WebServer.PICK_IMAGE_AFTER_KITKAT_REQUEST) {
            originalUri = data.getData();
            final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            // Check for the freshest data.
            getContentResolver().takePersistableUriPermission(originalUri, takeFlags);
        }
        mImageUrl = originalUri.toString();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), originalUri);
            ImageView imageView = (ImageView) findViewById(R.id.register_profile_image);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadLevels(long sportId) {
        // Loader id specific to sport and different from SPORT_LOADER_ID
        int loaderId = (int) sportId + SPORT_LOADER_ID + 1;

        Bundle args = new Bundle();
        args.putLong(ARG_SPORT_ID, sportId);

        if (getLoaderManager().getLoader(loaderId) != null) {
            getLoaderManager().restartLoader(loaderId, args, mLevelLoader);
        } else {
            getLoaderManager().initLoader(loaderId, args, mLevelLoader);
        }
    }

    @Override
    public void reloadView() {
        mLevelView.removeAllViews();
        for (int i = 0; i < mLevelAdapter.getCount(); i++) {
            View mLinearView = mLevelAdapter.getView(i, null, null);
            mLevelView.addView(mLinearView, i);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private Date mDate = null;

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
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            mDate = cal.getTime();
            Button dateButton = (Button) getActivity().findViewById(R.id.register_date);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            dateButton.setText(sdf.format(mDate));
        }
    }


    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private JSONObject userInfos;

        UserRegisterTask(String username, String password, String email, String displayName, String birthdate, String city, String description, boolean isCoach, List<Long> sportLevelsIds) {
            userInfos = new JSONObject();
            try {
                userInfos.put("username", username);
                userInfos.put("password", password);
                userInfos.put("email", email);
                userInfos.put("displayName", displayName);
                userInfos.put("isCoach", isCoach);
                userInfos.put("city", city);

                if (!birthdate.isEmpty()) userInfos.put("birthdate", birthdate);
                if (!description.isEmpty()) userInfos.put("description", description);

                JSONArray levelsArray = new JSONArray();
                for (Long levelId : sportLevelsIds) {
                    levelsArray.put(levelId);
                }
                if (levelsArray.length() > 0) userInfos.put("levels", levelsArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Register
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

            // Log in
            NetworkUtil.Response login_token = NetworkUtil.post(Const.WebServer.DOMAIN_NAME + Const.WebServer.AUTH + Const.WebServer.LOGIN, null, (new LoginRequest(username, password)).toJson());
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

            // Upload profile picture
            if (!mImageUrl.isEmpty()) {
                int indexLastSlash = mImageUrl.lastIndexOf("/");
                String filename = indexLastSlash == -1 ? mImageUrl + ".png" : mImageUrl.substring(indexLastSlash) + ".png";
                Uri imageUri = Uri.parse(mImageUrl);
                if (uploadImage(imageUri, filename, up.mIdDb)) {
                    up.mPicture = imageUri.getPath();
                    up.save();
                } else {
                    return false;
                }
            }
            return true;
        }

        protected Boolean uploadImage(Uri uploadFileUri, String filename, long userId) {

            String url = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.USER_PROFILE + userId + "/";
            try {
                MultipartUtility multipart = new MultipartUtility(url, "UTF-8", "Token " + SharedPrefUtil.getConnectedToken(getApplicationContext()));
                InputStream in = getContentResolver().openInputStream(uploadFileUri);
                multipart.addFilePart("picture", in, filename);
                multipart.finish();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;

            if (success) {
                RegistrationGCMIntentService.startActionRegistrationGCM(getApplicationContext());
                SharedPrefUtil.putIsFirstLaunch(getApplicationContext(), false);
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
            if (data != null) {
                mSportList = data;
                displayError(false);
            } else {
                mSportList.clear();
                displayError(true);
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
            LevelLoader levelLoader = (LevelLoader) loader;
            mLevelAdapter.setLevels(levelLoader.mSport, data);
            displayError(data == null);
        }

        @Override
        public void onLoaderReset(Loader<List<SportLevel>> loader) {

        }
    }
}
