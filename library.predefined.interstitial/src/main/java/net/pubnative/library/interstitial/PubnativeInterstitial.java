package net.pubnative.library.interstitial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import net.pubnative.library.interstitial.activity.PubnativeInterstitialActivity;
import net.pubnative.library.request.PubnativeRequest;
import net.pubnative.library.request.model.PubnativeAdModel;

import java.util.List;
import java.util.UUID;

import static android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class PubnativeInterstitial {

    private static final String TAG = PubnativeInterstitial.class.getSimpleName();

    private Context mContext;
    private Listener mListener;

    private String mIdentifier = UUID.randomUUID().toString();

    public interface Listener {
        void onPubnativeInterstitialOpened();
        void onPubnativeInterstitialClosed();
        void onPubnativeInterstitialFailed(Exception exception);
    }

    /**
     * Open ads in fullscreen activity
     * @param context Context
     * @param adModel Object of PubnativeAdModel
     * @param listener A valid or null listener
     */
    public void show(Context context, PubnativeAdModel adModel, Listener listener) {

        Log.v(TAG, "show");
        mContext = context;
        mListener = listener;

        registerBroadcastReceiver();

        Intent adIntent = new Intent(mContext, PubnativeInterstitialActivity.class);
        adIntent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        adIntent.putExtra(PubnativeInterstitialActivity.EXTRA_AD, adModel);
        adIntent.putExtra(PubnativeInterstitialActivity.EXTRA_IDENTIFIER, mIdentifier);
        mContext.startActivity(adIntent);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.v(TAG, "mMessageReceiver - onReceive");
            String identifier = intent.getStringExtra(PubnativeInterstitialActivity.EXTRA_IDENTIFIER);

            if(!identifier.equals(mIdentifier)) {
                return; // I am not the correct one :)
            }
            String action = intent.getStringExtra("action");
            switch (action) {
                case PubnativeInterstitialActivity.ACTION_ACTIVITY_RESUMED:
                    invokeOnPubnativeInterstitialOpened();
                    break;
                case PubnativeInterstitialActivity.ACTION_FAILED_INVALID_DATA:
                    invokeOnPubnativeInterstitialFailed(new Exception("Invalid data"));
                    break;
                case PubnativeInterstitialActivity.ACTION_ACTIVITY_STOPPED:
                    invokeOnPubnativeInterstitialClosed();
                    break;
            }
        }
    };

    private void registerBroadcastReceiver() {
        LocalBroadcastManager.getInstance(mContext.getApplicationContext()).registerReceiver(mMessageReceiver, new IntentFilter(PubnativeInterstitialActivity.BROADCAST_INTENT_EVENT));
    }

    private void unRegisterBroadcastReceiver() {
        LocalBroadcastManager.getInstance(mContext.getApplicationContext()).unregisterReceiver(mMessageReceiver);
    }

    private void invokeOnPubnativeInterstitialOpened() {

        Log.v(TAG, "invokeOnPubnativeInterstitialOpened");
        if(mListener != null) {
            mListener.onPubnativeInterstitialOpened();
        }
    }

    private void invokeOnPubnativeInterstitialClosed() {

        Log.v(TAG, "invokeOnPubnativeInterstitialClosed");
        if(mListener != null) {
            mListener.onPubnativeInterstitialClosed();
        }
        unRegisterBroadcastReceiver();
    }

    private void invokeOnPubnativeInterstitialFailed(Exception exception) {

        Log.v(TAG, "invokeOnPubnativeInterstitialFailed");
        if(mListener != null) {
            mListener.onPubnativeInterstitialFailed(exception);
        }
        unRegisterBroadcastReceiver();
    }
}
