package net.pubnative.library.interstitial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import net.pubnative.library.interstitial.activity.PubnativeInterstitial;
import net.pubnative.library.request.PubnativeRequest;
import net.pubnative.library.request.model.PubnativeAdModel;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class PubnativeInterstitialController implements PubnativeRequest.Listener {

    private Context mContext;
    private Listener mListener;

    public interface Listener {
        void onPubnativeInterstitialStarted();
        void onPubnativeInterstitialOpened();
        void onPubnativeInterstitialClosed();
        void onPubnativeInterstitialFailed(Exception exception);
    }

    public void show(Context context, String appToken, Listener listener) {
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
        Intent adIntent = new Intent(mContext, PubnativeInterstitial.class);
        adIntent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        PubnativeAdModel pubnativeAdModel = ads.get(0);
        adIntent.putExtra(PubnativeInterstitial.EXTRA_AD, pubnativeAdModel);
        mContext.startActivity(adIntent);
    }

    @Override
    public void onPubnativeRequestFailed(PubnativeRequest request, Exception ex) {
        invokeOnPubnativeInterstitialFailed(ex);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if(message.equals(PubnativeInterstitial.ACTION_ACTIVITY_RESUMED)) {
                invokeOnPubnativeInterstitialOpened();
            } else if(message.equals(PubnativeInterstitial.ACTION_FAILED_INVALID_DATA)) {
                invokeOnPubnativeInterstitialFailed(new Exception("Invalid ad found"));
            }
        }
    };

    private void registerBroadcastReceiver() {
        // Unregister all previously registered receivers with same context
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);

        // Register new one
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver, new IntentFilter(PubnativeInterstitial.BROADCAST_INTENT_EVENT));
    }

    private void invokeOnPubnativeInterstitialOpened() {
        if(mListener != null) {
            mListener.onPubnativeInterstitialOpened();
        }
    }

    private void invokeOnPubnativeInterstitialClosed() {
        if(mListener != null) {
            mListener.onPubnativeInterstitialClosed();
        }
    }

    private void invokeOnPubnativeInterstitialStarted() {
        if(mListener != null) {
            mListener.onPubnativeInterstitialStarted();
        }
    }

    private void invokeOnPubnativeInterstitialFailed(Exception exception) {
        if(mListener != null) {
            mListener.onPubnativeInterstitialFailed(exception);
        }
    }
}
