package fr.sims.coachingproject.ui.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.sims.coachingproject.NetworkService;
import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.MessageLoader;
import fr.sims.coachingproject.model.Message;
import fr.sims.coachingproject.receiver.GenericBroadcastReceiver;
import fr.sims.coachingproject.ui.adapter.MessageAdapter;
import fr.sims.coachingproject.util.Const;

/**
 * Created by Segolene on 18/02/2016.
 */
public class RelationChatFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<Message>>, GenericBroadcastReceiver.BroadcastReceiverListener {

    private MessageAdapter mMessageAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private GenericBroadcastReceiver mBroadcastReceiver;


    public static final String TABS_TITLE = "Messages";

    public static android.support.v4.app.Fragment newInstance() {
        RelationChatFragment fragment = new RelationChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);

        mBroadcastReceiver = new GenericBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver, new IntentFilter(Const.BroadcastEvent.EVENT_END_SERVICE_ACTION));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_relation_chat, container, false);
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
        NetworkService.startActionMessages(getContext());
    }

    @Override
    public void onRefresh() {
        NetworkService.startActionMessages(getContext());
    }

    @Override
    public Loader<List<Message>> onCreateLoader(int id, Bundle args) {
        return new MessageLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Message>> loader, List<Message> data) {
        mMessageAdapter.setData(data);

    }

    @Override
    public void onLoaderReset(Loader<List<Message>> loader) {
        mMessageAdapter.clearData();
    }

    @Override
    public void onBroadcastReceive(Intent intent) {
        if (intent.getStringExtra(Const.BroadcastEvent.EXTRA_ACTION_NAME).equals(NetworkService.ACTION_MESSAGES) && mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(false);
        }
    }
}
