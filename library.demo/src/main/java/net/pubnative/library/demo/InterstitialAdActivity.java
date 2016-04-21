package net.pubnative.library.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.pubnative.library.demo.utils.Settings;
import net.pubnative.library.interstitial.PubnativeInterstitial;
import net.pubnative.library.request.PubnativeRequest;
import net.pubnative.library.request.model.PubnativeAdModel;

import java.util.List;

public class InterstitialAdActivity extends Activity implements PubnativeRequest.Listener {

    private static final String TAG = InterstitialAdActivity.class.getName();

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
        PubnativeRequest request = new PubnativeRequest();
        request.setParameter(PubnativeRequest.Parameters.APP_TOKEN, Settings.getAppToken());
        request.start(this, this);
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================

    // PubnativeRequest.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeRequestSuccess(PubnativeRequest request, List<PubnativeAdModel> ads) {
        Log.v(TAG, "onPubnativeRequestSuccess");
        if (ads != null && ads.size() > 0) {
            new PubnativeInterstitial().show(this, ads.get(0));
        } else {
            Toast.makeText(this, "ERROR: no - fill", Toast.LENGTH_SHORT);
        }
        mLoaderContainer.setVisibility(View.GONE);
    }

    @Override
    public void onPubnativeRequestFailed(PubnativeRequest request, Exception ex) {
        Log.v(TAG, "onPubnativeRequestFailed: " + ex);
        Toast.makeText(this, "ERROR: " + ex, Toast.LENGTH_SHORT);
        mLoaderContainer.setVisibility(View.GONE);
    }
}
