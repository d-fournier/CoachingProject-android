package fr.sims.coachingproject.ui.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.MessageLoader;
import fr.sims.coachingproject.model.Message;
import fr.sims.coachingproject.receiver.GenericBroadcastReceiver;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.ui.adapter.MessageAdapter;
import fr.sims.coachingproject.ui.view.ContextMenuRecyclerView;
import fr.sims.coachingproject.util.Const;

/**
 * Created by Segolene on 18/02/2016.
 */

public class MessageFragment extends GenericFragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<Message>>, GenericBroadcastReceiver.BroadcastReceiverListener {


    public static final String MESSAGES_TITLE = "Messages";
    public static final String PINNED_TITLE = "Favoris";
    private final String RELATION_ID = "relationId";
    private final String GROUP_ID = "groupId";

    private SwipeRefreshLayout mRefreshLayout;

    private TextView mNoMessageText;
    private RecyclerView mMessagesRV;

    private MessageAdapter mMessageAdapter;
    private GenericBroadcastReceiver mBroadcastReceiver;

    private long mRelationId;
    private long mGroupId;
    private boolean mIsPinned;

    /***
     * @param relationId     Id of relation
     * @param pinnedMessages if true, shows pinned messages
     * @return the fragment
     */
    public static MessageFragment newRelationInstance(long relationId, boolean pinnedMessages) {
        MessageFragment fragment = new MessageFragment();
        fragment.mRelationId = relationId;
        fragment.mGroupId = -1;
        fragment.mIsPinned = pinnedMessages;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /***
     * @param groupId        Id of group
     * @param pinnedMessages if true, shows pinned messages
     * @return
     */
    public static MessageFragment newGroupInstance(long groupId, boolean pinnedMessages) {
        MessageFragment fragment = new MessageFragment();
        fragment.mGroupId = groupId;
        fragment.mRelationId = -1;
        fragment.mIsPinned = pinnedMessages;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        registerForContextMenu(mMessagesRV);
        getLoaderManager().initLoader(Const.Loaders.MESSAGE_LOADER_ID, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mRelationId = savedInstanceState.getLong(RELATION_ID);
            mGroupId = savedInstanceState.getLong(GROUP_ID);
        }
        mBroadcastReceiver = new GenericBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver, new IntentFilter(Const.BroadcastEvent.EVENT_END_SERVICE_ACTION));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_relation_chat;
    }


    protected void bindView(View view) {
        mNoMessageText = (TextView) view.findViewById(R.id.emptyList);

        mMessagesRV = (RecyclerView) view.findViewById(R.id.message_list);
        mMessagesRV.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessageAdapter = new MessageAdapter(getContext(), mIsPinned);
        mMessagesRV.setAdapter(mMessageAdapter);

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_relation_chat);
        mRefreshLayout.setOnRefreshListener(this);

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });

        if (mRelationId != -1) {
            NetworkService.startActionRelationMessages(getContext(), mRelationId);
        } else if (mGroupId != -1) {
            NetworkService.startActionGroupMessages(getContext(), mGroupId);
        }

    }

    @Override
    public void onRefresh() {
        if (mRelationId != -1) {
            NetworkService.startActionRelationMessages(getContext(), mRelationId);
        } else if (mGroupId != -1) {
            NetworkService.startActionGroupMessages(getContext(), mGroupId);
        }
    }

    @Override
    public Loader<List<Message>> onCreateLoader(int id, Bundle args) {
        if (mNoMessageText != null) {
            mNoMessageText.setVisibility(View.GONE);
        }
        return new MessageLoader(getContext(), mRelationId, mGroupId);
    }

    @Override
    public void onLoadFinished(Loader<List<Message>> loader, List<Message> data) {
        List<Message> filteredData = new ArrayList<>();
        if (mIsPinned) {
            for (Message mess : data) {
                if (mess.mIsPinned) {
                    filteredData.add(mess);
                }
            }
        } else {
            filteredData.addAll(data);
        }

        mMessageAdapter.setData(filteredData);
        if (mMessageAdapter.getItemCount() == 0) {
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
        if ((intent.getStringExtra(Const.BroadcastEvent.EXTRA_ACTION_NAME).equals(NetworkService.ACTION_RELATION_MESSAGES) ||
                intent.getStringExtra(Const.BroadcastEvent.EXTRA_ACTION_NAME).equals(NetworkService.ACTION_GROUP_MESSAGES)) && mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(RELATION_ID, mRelationId);
        outState.putLong(GROUP_ID, mGroupId);
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu,
                                    final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Message");
        if (mIsPinned) {
            menu.add(0, v.getId(), 0, R.string.unpin_message);
        } else {
            menu.add(0, v.getId(), 0, R.string.pin_message);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean requestPinnedValue = item.getTitle().equals(getString(R.string.pin_message));

        if (mIsPinned == requestPinnedValue) {
            // onContextItemSelected is called for every fragment loaded until it returns true
            // therefore we check if the fragment is the one we want
            return false;
        }


        ContextMenuRecyclerView.RecyclerContextMenuInfo menuInfo = (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
        Message message = mMessageAdapter.getItem(menuInfo.position);


        NetworkService.startActionTogglePinMessages(getContext(), message.mIdDb, requestPinnedValue);

        String display = requestPinnedValue ? getResources().getString(R.string.message_pinned) : getResources().getString(R.string.message_unpinned);
        Snackbar.make(mRefreshLayout, display, Snackbar.LENGTH_LONG).show();
        return true;
    }

}

