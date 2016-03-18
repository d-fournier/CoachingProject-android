package fr.sims.coachingproject.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;
import java.io.InputStream;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.MultipartUtility;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by Donovan on 18/03/2016.
 */
public class MessageSendFragment extends GenericFragment implements View.OnClickListener {
    private static final String EXTRA_RELATION_ID = "fr.sims.coachingproject.extra.RELATION_ID";
    private static final String EXTRA_GROUP_ID = "fr.sims.coachingproject.extra.GROUP_ID";

    private static final String TAG_GROUP_PREFIX = "_group_";
    private static final String TAG_RELATION_PREFIX = "_relation_";

    // Send message views
    private ImageButton mSendBtn;
    private ImageButton mAttachFileButton;
    private EditText mMessageET;
    private View mMainLayout;


    private Uri mUploadFileUri;
    private String mFileName;

    private long mGroupId;
    private long mRelationId;

    public static String getGroupTag(long groupId) {
        return MessageSendFragment.class.getSimpleName() + TAG_GROUP_PREFIX + groupId;
    }

    public static String getRelationTag(long relationId) {
        return MessageSendFragment.class.getSimpleName() + TAG_RELATION_PREFIX + relationId;
    }

    /***
     * @param relationId     Id of relation
     * @return the fragment
     */
    public static MessageSendFragment newRelationInstance(long relationId) {
        MessageSendFragment fragment = new MessageSendFragment();
        Bundle args = new Bundle();
        args.putLong(EXTRA_RELATION_ID, relationId);
        fragment.setArguments(args);
        return fragment;
    }

    /***
     * @param groupId        Id of group
     * @return
     */
    public static MessageSendFragment newGroupInstance(long groupId) {
        MessageSendFragment fragment = new MessageSendFragment();
        Bundle args = new Bundle();
        args.putLong(EXTRA_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message_send;
    }

    @Override
    protected void bindArguments(Bundle args) {
        super.bindArguments(args);
        mGroupId = args.getLong(EXTRA_GROUP_ID, -1);
        mRelationId = args.getLong(EXTRA_RELATION_ID, -1);
    }

    @Override
    protected void bindView(View view) {
        super.bindView(view);
        // Send Message View
        mMainLayout = view;
        mSendBtn = (ImageButton) view.findViewById(R.id.message_send);
        mSendBtn.setOnClickListener(this);
        mAttachFileButton = (ImageButton) view.findViewById(R.id.button_attach_file);
        mAttachFileButton.setOnClickListener(this);
        mMessageET = (EditText) view.findViewById(R.id.message_content);
        mUploadFileUri = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.message_send:
                sendMessage();
                break;
            case R.id.button_attach_file:
                selectFile();
                break;
        }
    }

    private void sendMessage() {
        String message = mMessageET.getText().toString();
        new SendMessageTask().execute(message);
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

    // TODO GetContext Null
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
            getContext().getContentResolver().takePersistableUriPermission(mUploadFileUri, takeFlags);
        }

        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.
        Cursor cursor = getContext().getContentResolver()
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

    private class SendMessageTask extends AsyncTask<String, Void, NetworkUtil.Response> {
        @Override
        protected NetworkUtil.Response doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String connectedToken = SharedPrefUtil.getConnectedToken(getContext());
            String url = Const.WebServer.DOMAIN_NAME + Const.WebServer.API + Const.WebServer.MESSAGES;

            try {
                MultipartUtility multipart = new MultipartUtility(url, "UTF-8", "Token " + connectedToken, "POST");
                multipart.addFormField("content", params[0]);
                if(mGroupId != -1)
                    multipart.addFormField("to_group",String.valueOf(mGroupId));
                else if (mRelationId != -1)
                    multipart.addFormField("to_relation",String.valueOf(mRelationId));
                if (mUploadFileUri != null) {
                    InputStream in = getContext().getContentResolver().openInputStream(mUploadFileUri);
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
            mAttachFileButton.setEnabled(true);
            if (response != null && response.isSuccessful()) {
                mMessageET.setText("");
                NetworkService.startActionGroupMessages(getContext(), mGroupId);
            } else {
                Snackbar.make(mMainLayout, "Error", Snackbar.LENGTH_LONG);
            }
        }

        @Override
        protected void onPreExecute() {
            mSendBtn.setEnabled(false);
            mAttachFileButton.setEnabled(false);
        }
    }

}
