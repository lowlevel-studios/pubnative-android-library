package net.pubnative.library.interstitial.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.pubnative.library.predefined.interstitial.R;
import net.pubnative.library.request.model.PubnativeAdModel;

public class PubnativeInterstitialActivity extends Activity implements PubnativeAdModel.Listener {

    private static final String TAG                         = PubnativeInterstitialActivity.class.getSimpleName();

    public static final String EXTRA_AD                     = "ad";
    public static final String EXTRA_IDENTIFIER             = "identifier";
    public static final String BROADCAST_INTENT_EVENT       = "interstitial_event";
    public static final String ACTION_ACTIVITY_RESUMED      = "action_activity_resumed";
    public static final String ACTION_ACTIVITY_STOPPED      = "action_activity_stopped";
    public static final String ACTION_FAILED_INVALID_DATA   = "action_failed_invalid_data";

    private TextView            titleView;
    private TextView            descriptionView;
    private ImageView           iconView;
    private ImageView           bannerView;
    private RatingBar           ratingView;
    private TextView            downloadView;

    private PubnativeAdModel    mPubnativeAdModel;
    private String              mIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.pubnative_interstitial);

        titleView = (TextView) this.findViewById(R.id.pn_interstitial_title);
        descriptionView = (TextView) this.findViewById(R.id.pn_interstitial_description);
        iconView = (ImageView) this.findViewById(R.id.pn_interstitial_icon);
        bannerView = (ImageView) this.findViewById(R.id.pn_interstitial_banner);
        ratingView = (RatingBar) this.findViewById(R.id.pn_interstitial_rating);
        downloadView = (TextView) this.findViewById(R.id.pn_interstitial_cta);

        renderAd();

        ratingView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {

        Log.v(TAG, "onResume");
        super.onResume();

        mPubnativeAdModel.startTracking(findViewById(R.id.pn_interstitial_container), downloadView, this);
        sendLocalMessage(ACTION_ACTIVITY_RESUMED);
    }

    @Override
    protected void onStop() {

        Log.v(TAG, "onStop");
        super.onStop();

        sendLocalMessage(ACTION_ACTIVITY_STOPPED);
    }

    private void renderAd() {

        Log.v(TAG, "renderAd");
        mPubnativeAdModel = (PubnativeAdModel) getIntent().getSerializableExtra("ad");

        if(mPubnativeAdModel == null) {
            sendLocalMessage(ACTION_FAILED_INVALID_DATA);
            finish();
            return;
        }

        if(mPubnativeAdModel.getTitle() != null) {
            titleView.setText(mPubnativeAdModel.getTitle());
        }
        if(mPubnativeAdModel.getDescription() != null) {
            descriptionView.setText(mPubnativeAdModel.getDescription());
        }
        if(mPubnativeAdModel.getCtaText() != null) {
            downloadView.setText(mPubnativeAdModel.getCtaText());
        }
        if(mPubnativeAdModel.getBannerUrl() != null) {
            Picasso.with(this).load(mPubnativeAdModel.getBannerUrl()).into(bannerView);
        }
        if(mPubnativeAdModel.getIconUrl() != null) {
            Picasso.with(this).load(mPubnativeAdModel.getIconUrl()).into(iconView);
        }
        /*if(mPubnativeAdModel.getRating() != null) {
            ratingView.setRating(Float.parseFloat(mPubnativeAdModel.getRating()));
        }*/

        mIdentifier = getIntent().getStringExtra(EXTRA_IDENTIFIER);
    }

    private void sendLocalMessage(String action) {

        Log.v(TAG, "sendLocalMessage");
        Intent intent = new Intent(BROADCAST_INTENT_EVENT);
        intent.putExtra("action", action);
        intent.putExtra(EXTRA_IDENTIFIER, mIdentifier);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onPubnativeAdModelImpression(PubnativeAdModel pubnativeAdModel, View view) {

        Log.v(TAG, "onPubnativeAdModelImpression");
    }

    @Override
    public void onPubnativeAdModelClick(PubnativeAdModel pubnativeAdModel, View view) {

        Log.v(TAG, "onPubnativeAdModelClick");
    }

    @Override
    public void onPubnativeAdModelOpenOffer(PubnativeAdModel pubnativeAdModel) {

        Log.v(TAG, "onPubnativeAdModelOpenOffer");
        finish();
    }
}
