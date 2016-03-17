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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.local.MessageLoader;
import fr.sims.coachingproject.model.Message;
import fr.sims.coachingproject.receiver.GenericBroadcastReceiver;
import fr.sims.coachingproject.service.NetworkService;
import fr.sims.coachingproject.ui.adapter.MessageListAdapter;
import fr.sims.coachingproject.ui.view.ContextMenuRecyclerView;
import fr.sims.coachingproject.util.Const;

/**
 * Created by Segolene on 18/02/2016.
 */

public class MessageFragment extends GenericFragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<Message>>, GenericBroadcastReceiver.BroadcastReceiverListener {

    private static final String EXTRA_RELATION_ID = "fr.sims.coachingproject.extra.RELATION_ID";
    private static final String EXTRA_GROUP_ID = "fr.sims.coachingproject.extra.GROUP_ID";
    private static final String EXTRA_IS_PINNED = "fr.sims.coachingproject.extra.IS_PINNED";

    public static final String MESSAGES_TITLE = "Messages";
    public static final String PINNED_TITLE = "Favoris";
    private final String RELATION_ID = "relationId";
    private final String GROUP_ID = "groupId";

    private SwipeRefreshLayout mRefreshLayout;

    private View mMessageListEmpty;
    private RecyclerView mMessagesRV;

    private MessageListAdapter mMessageAdapter;
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
        Bundle args = new Bundle();
        args.putLong(EXTRA_RELATION_ID, relationId);
        args.putBoolean(EXTRA_IS_PINNED, pinnedMessages);
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
        Bundle args = new Bundle();
        args.putLong(EXTRA_GROUP_ID, groupId);
        args.putBoolean(EXTRA_IS_PINNED, pinnedMessages);
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
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message;
    }

    @Override
    protected void bindArguments(Bundle args) {
        super.bindArguments(args);
        mRelationId = args.getLong(EXTRA_RELATION_ID, -1);
        mGroupId = args.getLong(EXTRA_GROUP_ID, -1);;
        mIsPinned = args.getBoolean(EXTRA_IS_PINNED, false);
    }

    protected void bindView(View view) {
        mMessageListEmpty = view.findViewById(R.id.message_list_empty);

        if(mIsPinned) {
            ((ImageView) view.findViewById(R.id.message_list_empty_picture)).setImageResource(R.drawable.ic_star_100dp);
            ((TextView) view.findViewById(R.id.message_list_empty_title)).setText(R.string.message_pinned_list_empty_title);
            View v = view.findViewById(R.id.message_list_empty_desc);
            v.setVisibility(View.VISIBLE);
            ((TextView) v).setText(R.string.message_pinned_list_empty_desc);
        } else {
            ((ImageView) view.findViewById(R.id.message_list_empty_picture)).setImageResource(R.drawable.ic_email_100dp);
            ((TextView) view.findViewById(R.id.message_list_empty_title)).setText(R.string.message_list_empty_title);
            view.findViewById(R.id.message_list_empty_desc).setVisibility(View.GONE);
        }

        mMessagesRV = (RecyclerView) view.findViewById(R.id.message_list);
        mMessagesRV.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessageAdapter = new MessageListAdapter(getContext(), mIsPinned);
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
        if (mMessageListEmpty != null) {
            mMessageListEmpty.setVisibility(View.GONE);
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
            mMessagesRV.setVisibility(View.GONE);
            mMessageListEmpty.setVisibility(View.VISIBLE);
        } else {
            mMessagesRV.setVisibility(View.VISIBLE);
            mMessageListEmpty.setVisibility(View.GONE);
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

