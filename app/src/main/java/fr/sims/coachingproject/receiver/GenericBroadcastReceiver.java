package fr.sims.coachingproject.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.Loader;

/**
 * Created by Donovan on 16/02/2016.
 */
public class GenericBroadcastReceiver extends android.content.BroadcastReceiver {
    private BroadcastReceiverListener mListener;

    public GenericBroadcastReceiver(BroadcastReceiverListener listener)
    {
        this.mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(mListener != null) {
            mListener.onBroadcastReceive(intent);
        }
    }

    public interface BroadcastReceiverListener {
        void onBroadcastReceive(Intent intent);
    }
}
