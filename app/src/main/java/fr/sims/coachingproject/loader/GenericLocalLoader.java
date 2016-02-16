package fr.sims.coachingproject.loader;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;

import fr.sims.coachingproject.receiver.GenericBroadcastReceiver;

/**
 * Created by Donovan on 16/02/2016.
 */
public abstract class GenericLocalLoader<D> extends AsyncTaskLoader<D> implements GenericBroadcastReceiver.BroadcastReceiverListener {

    LocalBroadcastManager mLocalBroadcastManager;
    GenericBroadcastReceiver mLoaderBroadcastReceiver;

    public GenericLocalLoader(Context context) {
        super(context);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
    }

    protected abstract String getBroadcastEvent();

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        String event = getBroadcastEvent();
        if(event != null) {
            mLoaderBroadcastReceiver = new GenericBroadcastReceiver(this);
            mLocalBroadcastManager.registerReceiver(mLoaderBroadcastReceiver, new IntentFilter(event));
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        if(mLoaderBroadcastReceiver != null) {
            mLocalBroadcastManager.unregisterReceiver(mLoaderBroadcastReceiver);
        }
    }

    @Override
    public void onBroadcastReceive(Intent intent) {
        this.onContentChanged();
    }
}
