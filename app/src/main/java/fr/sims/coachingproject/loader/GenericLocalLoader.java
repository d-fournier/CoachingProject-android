package fr.sims.coachingproject.loader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Donovan on 16/02/2016.
 */
public abstract class GenericLocalLoader<D> extends AsyncTaskLoader<D> {

    LocalBroadcastManager mLocalBroadcastManager;
    LoaderBroadcastReceiver mLoaderBroadcastReceiver;

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
            mLoaderBroadcastReceiver = new LoaderBroadcastReceiver(this);
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

    private class LoaderBroadcastReceiver extends BroadcastReceiver {
        private Loader loader;

        public LoaderBroadcastReceiver(Loader loader)
        {
            this.loader = loader;
        }

        @Override
        public void onReceive(Context context, Intent intent)
        {
            loader.onContentChanged();
        }
    }

}
