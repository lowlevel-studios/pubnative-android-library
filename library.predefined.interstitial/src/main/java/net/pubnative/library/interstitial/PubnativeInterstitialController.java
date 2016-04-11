package net.pubnative.library.interstitial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import net.pubnative.library.interstitial.activity.PubnativeInterstitial;
import net.pubnative.library.request.PubnativeRequest;
import net.pubnative.library.request.model.PubnativeAdModel;

import java.util.List;
import java.util.UUID;

import static android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class PubnativeInterstitialController implements PubnativeRequest.Listener {

    private static String TAG = PubnativeInterstitialController.class.getSimpleName();

    private Context mContext;
    private Listener mListener;

    private String mIdentifier = UUID.randomUUID().toString();

    public interface Listener {
        void onPubnativeInterstitialStarted();
        void onPubnativeInterstitialOpened();
        void onPubnativeInterstitialClosed();
        void onPubnativeInterstitialFailed(Exception exception);
    }

    public void show(Context context, String appToken, Listener listener) {

        Log.v(TAG, "show");
        mContext = context;
        mListener = listener;

        invokeOnPubnativeInterstitialStarted();

        registerBroadcastReceiver();

        PubnativeRequest request = new PubnativeRequest();
        request.setParameter(PubnativeRequest.Parameters.APP_TOKEN, appToken);
        request.start(context, PubnativeRequest.Endpoint.NATIVE, this);
    }

    @Override
    public void onPubnativeRequestSuccess(PubnativeRequest request, List<? extends PubnativeAdModel> ads) {

        Log.v(TAG, "onPubnativeRequestSuccess");
        Intent adIntent = new Intent(mContext, PubnativeInterstitial.class);
        adIntent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        PubnativeAdModel pubnativeAdModel = ads.get(0);
        adIntent.putExtra(PubnativeInterstitial.EXTRA_AD, pubnativeAdModel);
        adIntent.putExtra(PubnativeInterstitial.EXTRA_IDENTIFIER, mIdentifier);
        mContext.startActivity(adIntent);
    }

    @Override
    public void onPubnativeRequestFailed(PubnativeRequest request, Exception ex) {

        Log.v(TAG, "onPubnativeRequestFailed");
        invokeOnPubnativeInterstitialFailed(ex);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.v(TAG, "mMessageReceiver - onReceive");
            String identifier = intent.getStringExtra(PubnativeInterstitial.EXTRA_IDENTIFIER);

            if(!identifier.equals(mIdentifier)) {
                return; // I am not the correct one :)
            }
            String action = intent.getStringExtra("action");
            Log.v(TAG, "mMessageReceiver - onReceive action = " + action);
            switch (action) {
                case PubnativeInterstitial.ACTION_ACTIVITY_RESUMED:
                    invokeOnPubnativeInterstitialOpened();
                    break;
                case PubnativeInterstitial.ACTION_FAILED_INVALID_DATA:
                    invokeOnPubnativeInterstitialFailed(new Exception("Invalid data"));
                    break;
                case PubnativeInterstitial.ACTION_ACTIVITY_STOPPED:
                    invokeOnPubnativeInterstitialClosed();
                    break;
            }
        }
    };

    private void registerBroadcastReceiver() {
        LocalBroadcastManager.getInstance(mContext.getApplicationContext()).registerReceiver(mMessageReceiver, new IntentFilter(PubnativeInterstitial.BROADCAST_INTENT_EVENT));
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

    private void invokeOnPubnativeInterstitialStarted() {

        Log.v(TAG, "invokeOnPubnativeInterstitialStarted");
        if(mListener != null) {
            mListener.onPubnativeInterstitialStarted();
        }
    }

    private void invokeOnPubnativeInterstitialFailed(Exception exception) {

        Log.v(TAG, "invokeOnPubnativeInterstitialFailed");
        if(mListener != null) {
            mListener.onPubnativeInterstitialFailed(exception);
        }
        unRegisterBroadcastReceiver();
    }
}
