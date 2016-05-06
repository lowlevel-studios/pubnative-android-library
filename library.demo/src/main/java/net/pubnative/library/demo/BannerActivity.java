package net.pubnative.library.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import net.pubnative.library.banner.PubnativeBanner;
import net.pubnative.library.demo.utils.Settings;

public class BannerActivity extends Activity implements PubnativeBanner.Listener {

    public static final String TAG = BannerActivity.class.getSimpleName();
    private RelativeLayout mLoaderContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        mLoaderContainer = (RelativeLayout) findViewById(R.id.activity_native_container_loader);

    }

    public void onRequestClick(View v) {
        PubnativeBanner banner = new PubnativeBanner(
                this,
                Settings.getAppToken(),
                PubnativeBanner.Size.BANNER_50,
                PubnativeBanner.Position.BOTTOM
        );
        banner.setListener(this);
        banner.load();
        banner.show();
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeBanner.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeBannerLoadFinish(PubnativeBanner banner) {
        Log.v(TAG, "onPubnativeBannerLoadFinish");
        banner.show();
    }

    @Override
    public void onPubnativeBannerLoadFail(PubnativeBanner banner, Exception exception) {
        Log.v(TAG, "onPubnativeBannerLoadFail", exception);
        mLoaderContainer.setVisibility(View.GONE);
    }

    @Override
    public void onPubnativeBannerShow(PubnativeBanner banner) {
        Log.v(TAG, "onPubnativeBannerShow");
        mLoaderContainer.setVisibility(View.GONE);
    }

    @Override
    public void onPubnativeBannerImpressionConfirmed(PubnativeBanner banner) {
        Log.v(TAG, "onPubnativeBannerImpressionConfirmed");
    }

    @Override
    public void onPubnativeBannerClick(PubnativeBanner banner) {
        Log.v(TAG, "onPubnativeBannerClick");
    }

    @Override
    public void onPubnativeBannerHide(PubnativeBanner banner) {
        Log.v(TAG, "onPubnativeBannerHide");
    }
}
