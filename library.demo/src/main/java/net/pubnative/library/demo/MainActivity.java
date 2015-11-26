package net.pubnative.library.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.pubnative.library.Pubnative;
import net.pubnative.library.PubnativeContract;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.predefined.PubnativeActivityListener;
import net.pubnative.library.request.AdRequest;
import net.pubnative.library.request.AdRequestListener;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity implements PubnativeActivityListener, AdRequestListener {

    // OLD_API: 62cb87940b3a70cf67a200c3b443b47c20fd00b0a1aece21ec435d8e0521eb60
    // NEW_API: 6651a94cad554c30c47427cbaf0b613a967abcca317df325f363ef154a027092
    // SPOTX_VIDEO: 78fcce3f082f263348db271b0f15ecb83d259491b383f31bc0a8ba84897311b8
    private final String APP_TOKEN = "78fcce3f082f263348db271b0f15ecb83d259491b383f31bc0a8ba84897311b8";

    ImageView nativeIcon   = null;
    ImageView nativeBanner = null;
    TextView  nativeTitle  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        nativeIcon = (ImageView) this.findViewById(R.id.native_image);
        nativeBanner = (ImageView) this.findViewById(R.id.native_banner);
        nativeTitle = (TextView) this.findViewById(R.id.native_title);

        nativeIcon.setVisibility(View.GONE);
        nativeBanner.setVisibility(View.GONE);
        nativeTitle.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {

        super.onPause();
        Pubnative.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
        Pubnative.onResume();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        Pubnative.onDestroy();
    }

    public void onVideoClick(View view) {

        Pubnative.show(this, Pubnative.FullScreen.VIDEO, APP_TOKEN, this);
    }

    public void onInterstitialClick(View view) {

        Pubnative.show(this, Pubnative.FullScreen.INTERSTITIAL, APP_TOKEN, this);
    }

    public void onGameListClick(View view) {

        Pubnative.show(this, Pubnative.FullScreen.GAME_LIST, APP_TOKEN, this);
    }

    public void onNativeClick(View view) {

        nativeIcon.setVisibility(View.GONE);
        nativeBanner.setVisibility(View.GONE);
        nativeTitle.setVisibility(View.GONE);

        nativeIcon.setImageBitmap(null);
        nativeBanner.setImageBitmap(null);
        nativeTitle.setText("");

        AdRequest request = new AdRequest(this);
        request.setParameter(PubnativeContract.Request.APP_TOKEN, APP_TOKEN);
        request.start(AdRequest.Endpoint.NATIVE, this);
    }

    public void onSettingsClick(View view) {

        Toast.makeText(this, "Under development", Toast.LENGTH_SHORT).show();
    }

    //PubnativeActivityListener
    @Override
    public void onPubnativeActivityStarted(String identifier) {

        Log.v("pubnative-library-demo", "onPubnativeActivityStarted: " + identifier);
    }

    @Override
    public void onPubnativeActivityFailed(String identifier, Exception exception) {

        Log.v("pubnative-library-demo", "onPubnativeActivityFailed: " + identifier + " - Exception: " + exception);
    }

    @Override
    public void onPubnativeActivityOpened(String identifier) {

        Log.v("pubnative-library-demo", "onPubnativeActivityOpened: " + identifier);
    }

    @Override
    public void onPubnativeActivityClosed(String identifier) {

        Log.v("pubnative-library-demo", "onPubnativeActivityClosed: " + identifier);
    }

    @Override
    public void onAdRequestStarted(AdRequest request) {

        Log.v("pubnative-library-demo", "onAdRequestStarted:");
    }

    @Override
    public void onAdRequestFinished(AdRequest request, ArrayList<? extends NativeAdModel> ads) {

        Log.v("pubnative-library-demo", "onAdRequestFinished: loaded " + ads.size() + " ads");

        if (ads.size() > 0) {

            NativeAdModel model = ads.get(0);

            nativeTitle.setText(model.title);

            Picasso.with(this).load(model.iconUrl).into(nativeIcon);
            Picasso.with(this).load(model.bannerUrl).into(nativeBanner);

            nativeTitle.setVisibility(View.VISIBLE);
            nativeIcon.setVisibility(View.VISIBLE);
            nativeBanner.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAdRequestFailed(AdRequest request, Exception ex) {

        Log.v("pubnative-library-demo", "onAdRequestFailed: " + ex);
    }
}
