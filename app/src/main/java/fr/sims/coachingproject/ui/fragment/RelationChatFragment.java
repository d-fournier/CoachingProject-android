package fr.sims.coachingproject.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.NetworkService;
import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.MessageLoader;
import fr.sims.coachingproject.model.Message;
import fr.sims.coachingproject.receiver.GenericBroadcastReceiver;
import fr.sims.coachingproject.ui.adapter.MessageAdapter;
import fr.sims.coachingproject.util.Const;
import fr.sims.coachingproject.util.NetworkUtil;
import fr.sims.coachingproject.util.SharedPrefUtil;

/**
 * Created by Segolene on 18/02/2016.
 */
public class RelationChatFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<Message>>, GenericBroadcastReceiver.BroadcastReceiverListener, View.OnClickListener {

    private MessageAdapter mMessageAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private GenericBroadcastReceiver mBroadcastReceiver;
    private EditText mMessageET;
    private Button btn;
    private long mRelationId;
    private boolean mPinned;
    private LinearLayout layoutView;

    private TextView mNoMessageText;

    private final String RELATION_ID="relationId";
    public static final String MESSAGES_TITLE = "Messages";
    public static final String PINNED_TITLE = "Favoris";

    public static android.support.v4.app.Fragment newInstance(long relationId, boolean pinnedMessages) {
        RelationChatFragment fragment = new RelationChatFragment();
        fragment.mRelationId=relationId;
        fragment.mPinned=pinnedMessages;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null){
            mRelationId=savedInstanceState.getLong(RELATION_ID);
        }

        getLoaderManager().initLoader(0, null, this);

        mBroadcastReceiver = new GenericBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver, new IntentFilter(Const.BroadcastEvent.EVENT_END_SERVICE_ACTION));


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_relation_chat, container, false);
        mNoMessageText=(TextView)view.findViewById(R.id.emptyList);
        bindView(view);
        return view;
    }


    protected void bindView(View view) {
        mMessageAdapter=new MessageAdapter(getContext());
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

        btn = (Button) view.findViewById(R.id.send_button);
        btn.setOnClickListener(this);
        mMessageET = (EditText) view.findViewById(R.id.message_editText);
    }

    @Override
    public void onRefresh() {
        NetworkService.startActionMessages(getContext(), mRelationId);
    }

    @Override
    public Loader<List<Message>> onCreateLoader(int id, Bundle args) {
        if(mNoMessageText!=null){
            mNoMessageText.setVisibility(View.GONE);
        }
        return new MessageLoader(getContext(), mRelationId);
    }

    @Override
    public void onLoadFinished(Loader<List<Message>> loader, List<Message> data) {
        List<Message> filteredData= new ArrayList<>();
        for(Message mess : data){
            if (mess.mIsPinned==mPinned){
                filteredData.add(mess);
            }
        }
        mMessageAdapter.setData(filteredData);
        if (mMessageAdapter.isEmpty()){
            mNoMessageText.setVisibility(View.VISIBLE);
        }else{
            mNoMessageText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Message>> loader) {
        mMessageAdapter.clearData();
    }

    @Override
    public void onBroadcastReceive(Intent intent) {
        if (intent.getStringExtra(Const.BroadcastEvent.EXTRA_ACTION_NAME).equals(NetworkService.ACTION_COACHING_RELATION_ITEM) && mRefreshLayout != null) {
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
        in.hideSoftInputFromWindow( mMessageET.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        String message = mMessageET.getText().toString();

        String body = "";

        try {
            JSONObject parent = new JSONObject();
            parent.put("content",message);
            parent.put("to_relation", ""+mRelationId);
            parent.put("is_pinned", false);

            body = parent.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendRequestTask().execute(body);
    }


    private class SendRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            if(params.length > 0) {
                String body = params[0];
                String connectedToken = SharedPrefUtil.getConnectedToken(getContext());
                String response = NetworkUtil.post("https://coachingproject.herokuapp.com/api/messages/", connectedToken, body);

            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mMessageET.setText("");
            btn.setEnabled(true);
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(true);
                }
            });
            NetworkService.startActionMessages(getContext(), mRelationId);
        }

        @Override
        protected void onPreExecute()
        {
            btn.setEnabled(false);
        }

    }
}
