package fr.sims.coachingproject.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.network.SingleGroupLoader;
import fr.sims.coachingproject.model.Group;
import fr.sims.coachingproject.model.Message;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.ui.adapter.pager.GroupPagerAdapter;
import fr.sims.coachingproject.ui.fragment.MessageFragment;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.MultipartUtility;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by Zhenjie CEN on 2016/3/6.
 */
public class GroupActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private static final String EXTRA_GROUP_ID = "fr.sims.coachingproject.extra.GROUP_ID";

    // Send message views
    private ImageButton mSendBtn;
    private ImageButton mAttachFileButton;
    private EditText mMessageET;
    private Toolbar mMessageToolbar;
    private Uri mUploadFileUri;
    private String mFileName;

    private TextView mGroupName;
    private TextView mGroupDescription;
    private TextView mGroupCreationDate;
    private TextView mGroupSport;
    private TextView mGroupCity;
    private long mGroupIdDb;
    private Group mGroup;

    private FloatingActionButton mButtonJoin;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private GroupPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;

    public static void startActivity(Context ctx, long id) {
        Intent startIntent = new Intent(ctx, GroupActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startIntent.putExtra(EXTRA_GROUP_ID, id);
        ctx.startActivity(startIntent);
    }

    public static Intent getIntent(Context ctx, long id) {
        Intent intent = new Intent(ctx, GroupActivity.class);
        intent.putExtra(EXTRA_GROUP_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        // Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        mGroupIdDb = intent.getLongExtra(EXTRA_GROUP_ID, -1);

        mGroupName = (TextView) findViewById(R.id.group_name);
        mGroupDescription = (TextView) findViewById(R.id.group_description);
        mGroupCreationDate = (TextView) findViewById(R.id.group_creation_date);
        mGroupSport = (TextView) findViewById(R.id.group_sport);
        mGroupCity = (TextView) findViewById(R.id.group_city);

        SingleGroupLoaderCallbacks mGroupLoader = new SingleGroupLoaderCallbacks();

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.groupMembersPager);
        mPagerAdapter = new GroupPagerAdapter(getSupportFragmentManager(), mGroupIdDb);
        mPager.setAdapter(mPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.view_group_tabs);
        mTabLayout.setupWithViewPager(mPager);
        mPager.addOnPageChangeListener(this);

        mButtonJoin = (FloatingActionButton) findViewById(R.id.group_send_join_invite);
        mButtonJoin.setOnClickListener(this);

        // Send Message View
        mSendBtn = (ImageButton) findViewById(R.id.message_send);
        mSendBtn.setOnClickListener(this);
        mAttachFileButton = (ImageButton) findViewById(R.id.button_attach_file);
        mAttachFileButton.setOnClickListener(this);
        mMessageET = (EditText) findViewById(R.id.message_content);
        mMessageToolbar = (Toolbar) findViewById(R.id.message_send_group_toolbar);
        mUploadFileUri = null;

        mTabLayout.setVisibility(View.VISIBLE);
        mPager.setVisibility(View.VISIBLE);

        getSupportLoaderManager().initLoader(Const.Loaders.GROUP_LOADER_ID, null, mGroupLoader);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mPagerAdapter.getItem(position) instanceof MessageFragment) {
            setMessageFragmentElementsVisibility();
        } else {
            setMembersFragmentElementsVisibility();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void hideAllElements() {
        mButtonJoin.setVisibility(View.GONE);
        mButtonJoin.setEnabled(false);
        mMessageToolbar.setVisibility(View.GONE);
    }

    public void setMembersFragmentElementsVisibility() {
        if (mGroup != null) {
            if (mGroup.mIsCurrentUserPending) {
                hideAllElements();
            } else {
                mButtonJoin.setEnabled(true);
                mButtonJoin.show();
                mMessageToolbar.setVisibility(View.GONE);
            }
        } else {
            hideAllElements();
        }
    }

    public void setMessageFragmentElementsVisibility() {
        if (mGroup != null) {
            if (mGroup.mIsCurrentUserMember) {
                mButtonJoin.setVisibility(View.GONE);
                mButtonJoin.setEnabled(false);
                mMessageToolbar.setVisibility(View.VISIBLE);
            } else if (mGroup.mIsCurrentUserPending) {
                hideAllElements();
            } else {
                mButtonJoin.setEnabled(true);
                mButtonJoin.show();
                mMessageToolbar.setVisibility(View.GONE);
            }
        } else {
            mButtonJoin.setEnabled(false);
            mButtonJoin.hide();
            mMessageToolbar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.message_send:
                sendMessage();
                break;
            case R.id.button_attach_file:
                selectFile();
                break;
            case R.id.group_send_join_invite:
                if (mGroup.mIsCurrentUserMember) {//We invite people
                    SearchActivity.startActivity(getApplicationContext(), true, mGroupIdDb, false);
                } else if (!mGroup.mIsCurrentUserPending) {//We are not member and not pending, so we join
                    new SendJoinTask().execute();
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean res = super.onCreateOptionsMenu(menu);
        if (mGroup != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_group, menu);
            menu.findItem(R.id.leave_group).setVisible(mGroup.mIsCurrentUserMember);
            return true;
        }
        return res;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.leave_group:
                new LeaveGroupTask().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendMessage() {
        String message = mMessageET.getText().toString();
        new SendMessageTask().execute(message, String.valueOf(mGroupIdDb));
    }

    private void selectFile() {
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), Const.WebServer.PICK_IMAGE_REQUEST);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, Const.WebServer.PICK_IMAGE_AFTER_KITKAT_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (data == null) return;
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    class SingleGroupLoaderCallbacks implements LoaderManager.LoaderCallbacks<Group> {

        @Override
        public Loader<Group> onCreateLoader(int id, Bundle args) {
            return new SingleGroupLoader(getApplicationContext(), mGroupIdDb);
        }

        @Override
        public void onLoadFinished(Loader<Group> loader, Group data) {
            if (data != null) {
                mGroupName.setText(data.mName);
                mGroupDescription.setText(data.mDescription);
                mGroupCreationDate.setText(getString(R.string.created_on, data.mCreationDate));
                mGroupSport.setText(data.mSport.mName);
                mGroupCity.setText(data.mCity);
                ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar)).setTitle(data.mName);
                mGroup = data;
                setMessageFragmentElementsVisibility();
            } else {
                //TODO Error
            }

        }

        @Override
        public void onLoaderReset(Loader<Group> loader) {

        }

    }

    private class SendJoinTask extends AsyncTask<Void, Void, NetworkUtil.Response> {

        @Override
        protected NetworkUtil.Response doInBackground(Void... params) {
            return NetworkUtil.post(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS + mGroupIdDb + Const.WebServer.SEPARATOR
                            + Const.WebServer.JOIN + Const.WebServer.SEPARATOR,
                    SharedPrefUtil.getConnectedToken(getApplicationContext()), "");
        }

        @Override
        protected void onPostExecute(NetworkUtil.Response response) {
            if (response.isSuccessful()) {
                Snackbar.make(mGroupName, R.string.demand_sent, Snackbar.LENGTH_LONG).show();
                mButtonJoin.setVisibility(View.GONE);
            } else {
                switch (response.getReturnCode()) {
                    case 400:
                        Snackbar.make(mGroupName, R.string.already_in_group, Snackbar.LENGTH_LONG).show();
                        break;
                    case 401:
                        Snackbar.make(mGroupName, R.string.not_connected, Snackbar.LENGTH_LONG).show();
                        break;
                    case 403:
                        Snackbar.make(mGroupName, R.string.group_private, Snackbar.LENGTH_LONG).show();
                        break;
                    default:
                        Snackbar.make(mGroupName, R.string.unknown_error, Snackbar.LENGTH_LONG).show();

                }

            }

        }
    }

    private class LeaveGroupTask extends AsyncTask<Void, Void, NetworkUtil.Response> {

        @Override
        protected NetworkUtil.Response doInBackground(Void... params) {
            NetworkUtil.Response res = NetworkUtil.post(Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.GROUPS + mGroupIdDb + Const.WebServer.SEPARATOR
                            + Const.WebServer.LEAVE + Const.WebServer.SEPARATOR,
                    SharedPrefUtil.getConnectedToken(getApplicationContext()), "");
            if (res.isSuccessful()) {
                Message.deleteAllMessagesByGroup(mGroup);
                mGroup.delete();
            }
            return res;
        }

        @Override
        protected void onPostExecute(NetworkUtil.Response response) {
            if (response.isSuccessful()) {
                Toast.makeText(getApplicationContext(), R.string.leave_group_success, Toast.LENGTH_LONG).show();
                GroupActivity.this.finish();
            } else {
                switch (response.getReturnCode()) {
                    case 400:
                        Snackbar.make(mGroupName, R.string.not_in_group, Snackbar.LENGTH_LONG).show();
                        break;
                    case 401:
                        Snackbar.make(mGroupName, R.string.not_connected, Snackbar.LENGTH_LONG).show();
                        break;
                    case 403:
                        Snackbar.make(mGroupName, R.string.leave_admin, Snackbar.LENGTH_LONG).show();
                        break;
                    default:
                        Snackbar.make(mGroupName, R.string.unknown_error, Snackbar.LENGTH_LONG).show();

                }
            }

        }
    }

    private class SendMessageTask extends AsyncTask<String, Void, NetworkUtil.Response> {
        @Override
        protected NetworkUtil.Response doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String connectedToken = SharedPrefUtil.getConnectedToken(getApplicationContext());
            String url = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.MESSAGES;

            try {
                MultipartUtility multipart = new MultipartUtility(url, "UTF-8", "Token " + connectedToken, "POST");
                multipart.addFormField("content", params[0]);
                multipart.addFormField("to_group",params[1]);
                if (mUploadFileUri != null) {
                    InputStream in = getContentResolver().openInputStream(mUploadFileUri);
                    multipart.addFilePart("associated_file", in, mFileName);
                }
                return multipart.finish();
            } catch (IOException e) {
                return null;
            }

        }


        @Override
        protected void onPostExecute(NetworkUtil.Response response) {
            mSendBtn.setEnabled(true);
            mMessageET.setText("");
            if (response != null && response.isSuccessful()) {
                NetworkService.startActionGroupMessages(getApplicationContext(), mGroupIdDb);
            } else {
                Snackbar.make(mPager, "Error", Snackbar.LENGTH_LONG);
            }
        }

        @Override
        protected void onPreExecute() {
            mSendBtn.setEnabled(false);
        }
    }

}







