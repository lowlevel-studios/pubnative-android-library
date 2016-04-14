package net.pubnative.library.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import net.pubnative.library.demo.utils.Settings;
import net.pubnative.library.interstitial.PubnativeInterstitial;

public class InterstitialAdActivity extends Activity implements PubnativeInterstitial.Listener {

    private static final String TAG = NativeAdActivity.class.getName();

    private RelativeLayout   mLoaderContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);
        mLoaderContainer = (RelativeLayout) findViewById(R.id.activity_native_container_loader);
    }

    public void onRequestClick(View v) {

        Log.v(TAG, "onRequestClick");
        mLoaderContainer.setVisibility(View.VISIBLE);
        PubnativeInterstitial pubnativeInterstitial = new PubnativeInterstitial();
        pubnativeInterstitial.show(this, Settings.getAppToken(), this);
    }

    @Override
    public void onPubnativeInterstitialStarted() {
        Log.v(TAG, "onPubnativeInterstitialStarted");
    }

    @Override
    public void onPubnativeInterstitialOpened() {
        mLoaderContainer.setVisibility(View.GONE);
        Log.v(TAG, "onPubnativeInterstitialOpened");
    }

    @Override
    public void onPubnativeInterstitialClosed() {
        mLoaderContainer.setVisibility(View.GONE);
        Log.v(TAG, "onPubnativeInterstitialClosed");
    }

    @Override
    public void onPubnativeInterstitialFailed(Exception exception) {
        mLoaderContainer.setVisibility(View.GONE);
        Log.e(TAG, "onPubnativeInterstitialFailed " + exception);
    }
}
