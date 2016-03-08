package fr.sims.coachingproject.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.MessageLoader;
import fr.sims.coachingproject.model.Message;
import fr.sims.coachingproject.receiver.GenericBroadcastReceiver;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.ui.adapter.MessageAdapter;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by Segolene on 18/02/2016.
 */
public class RelationChatFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<Message>>, GenericBroadcastReceiver.BroadcastReceiverListener, View.OnClickListener {

    public static final String MESSAGES_TITLE = "Messages";
    public static final String PINNED_TITLE = "Favoris";
    private final String RELATION_ID = "relationId";
    private MessageAdapter mMessageAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private GenericBroadcastReceiver mBroadcastReceiver;
    private EditText mMessageET;
    private Button mSendBtn;
    private long mRelationId;
    private boolean mPinned;
    private TextView mNoMessageText;

    public static android.support.v4.app.Fragment newInstance(long relationId, boolean pinnedMessages) {
        RelationChatFragment fragment = new RelationChatFragment();
        fragment.mRelationId = relationId;
        fragment.mPinned = pinnedMessages;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        registerForContextMenu(getListView());
        getLoaderManager().initLoader(Const.Loaders.MESSAGE_LOADER_ID, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mRelationId = savedInstanceState.getLong(RELATION_ID);
        }
        mBroadcastReceiver = new GenericBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver, new IntentFilter(Const.BroadcastEvent.EVENT_END_SERVICE_ACTION));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_relation_chat, container, false);
        mNoMessageText = (TextView) view.findViewById(R.id.emptyList);
        bindView(view);
        return view;
    }


    protected void bindView(View view) {
        mMessageAdapter = new MessageAdapter(getContext());
        setListAdapter(mMessageAdapter);

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_relation_chat);
        mRefreshLayout.setOnRefreshListener(this);

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
        NetworkService.startActionMessages(getContext(), mRelationId);

        mSendBtn = (Button) view.findViewById(R.id.send_button);
        mSendBtn.setOnClickListener(this);
        mMessageET = (EditText) view.findViewById(R.id.message_editText);
    }

    @Override
    public void onRefresh() {
        NetworkService.startActionMessages(getContext(), mRelationId);
    }

    @Override
    public Loader<List<Message>> onCreateLoader(int id, Bundle args) {
        if (mNoMessageText != null) {
            mNoMessageText.setVisibility(View.GONE);
        }
        return new MessageLoader(getContext(), mRelationId);
    }

    @Override
    public void onLoadFinished(Loader<List<Message>> loader, List<Message> data) {
        List<Message> filteredData = new ArrayList<>();
        if (mPinned) {
            for (Message mess : data) {
                if (mess.mIsPinned) {
                    filteredData.add(mess);
                }
            }
        } else {
            filteredData.addAll(data);
        }

        mMessageAdapter.setData(filteredData);
        if (mMessageAdapter.isEmpty()) {
            mNoMessageText.setVisibility(View.VISIBLE);
        } else {
            mNoMessageText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Message>> loader) {
        mMessageAdapter.clearData();
    }

    @Override
    public void onBroadcastReceive(Intent intent) {
        if (intent.getStringExtra(Const.BroadcastEvent.EXTRA_ACTION_NAME).equals(NetworkService.ACTION_RELATION_MESSAGES) && mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(RELATION_ID, mRelationId);
    }

    @Override
    public void onClick(View v) {


        InputMethodManager in = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(mMessageET.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        String message = mMessageET.getText().toString();

        String body = "";

        try {
            JSONObject parent = new JSONObject();
            parent.put("content", message);
            parent.put("to_relation", "" + mRelationId);
            parent.put("is_pinned", false);

            body = parent.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendRequestTask().execute(body);
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu,
                                    final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Message");
        if (mPinned) {
            menu.add(R.string.unpin_message);
        } else {
            menu.add(R.string.pin_message);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean requestPinnedValue = item.getTitle().equals(getString(R.string.pin_message));

        if (mPinned == requestPinnedValue) {
            // onContextItemSelected is called for every fragment loaded until it returns true
            // therefore we check if the fragment is the one we want
            return false;
        }

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Message message = mMessageAdapter.getItem(menuInfo.position);

        NetworkService.startActionTogglePinMessages(getContext(), message.mIdDb, requestPinnedValue);

        String display = requestPinnedValue ? "Message pinned" : "Message unpinned";
        Snackbar.make(mRefreshLayout, display, Snackbar.LENGTH_LONG).show();
        return true;
    }

    private class SendRequestTask extends AsyncTask<String, Void, NetworkUtil.Response> {
        @Override
        protected NetworkUtil.Response doInBackground(String... params) {
            if (params.length > 0) {
                String body = params[0];
                String connectedToken = SharedPrefUtil.getConnectedToken(getContext());
                NetworkUtil.Response response = NetworkUtil.post("https://coachingproject.herokuapp.com/api/messages/", connectedToken, body);
                return response;
            } else
                return null;
        }

        @Override
        protected void onPostExecute(NetworkUtil.Response response) {
            if (response != null) {
                mSendBtn.setEnabled(true);
                if (response.isSuccessful()) {
                    mMessageET.setText("");
                    mRefreshLayout.setRefreshing(true);
                    NetworkService.startActionMessages(getContext(), mRelationId);
                } else {
                    Snackbar.make(getListView(), R.string.no_connectivity, Snackbar.LENGTH_SHORT);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            mSendBtn.setEnabled(false);
        }

    }
}

