package net.pubnative.library.interstitial.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.pubnative.library.predefined.interstitial.R;
import net.pubnative.library.request.model.PubnativeAdModel;

public class PubnativeInterstitial extends Activity implements PubnativeAdModel.Listener {

    public static final String EXTRA_AD                     = "ad";
    public static final String BROADCAST_INTENT_EVENT       = "interstitial_event";
    public static final String ACTION_ACTIVITY_RESUMED      = "action_activity_resumed";
    public static final String ACTION_FAILED_INVALID_DATA   = "action_failed_invalid_data";

    private TextView            titleView;
    private TextView            descriptionView;
    private ImageView           iconView;
    private ImageView           bannerView;
    private RatingBar           ratingView;
    private TextView            downloadView;

    private PubnativeAdModel    mPubnativeAdModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pubnative_interstitial);

        titleView = (TextView) this.findViewById(R.id.pn_interstitial_title);
        descriptionView = (TextView) this.findViewById(R.id.pn_interstitial_description);
        iconView = (ImageView) this.findViewById(R.id.pn_interstitial_icon);
        bannerView = (ImageView) this.findViewById(R.id.pn_interstitial_banner);
        ratingView = (RatingBar) this.findViewById(R.id.pn_interstitial_rating);
        downloadView = (TextView) this.findViewById(R.id.pn_interstitial_cta);

        renderAd();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPubnativeAdModel.startTracking(findViewById(R.id.pn_interstitial_container), downloadView, this);
        sendLocalMessage(ACTION_ACTIVITY_RESUMED);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void renderAd() {

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
        if(mPubnativeAdModel.getCta() != null) {
            downloadView.setText(mPubnativeAdModel.getCta());
        }
        if(mPubnativeAdModel.getBanner() != null) {
            Picasso.with(this).load(mPubnativeAdModel.getBanner().getUrl()).into(bannerView);
        }
        if(mPubnativeAdModel.getIcon() != null) {
            Picasso.with(this).load(mPubnativeAdModel.getIcon().getUrl()).into(iconView);
        }
        if(mPubnativeAdModel.getRating() != null) {
            ratingView.setRating(Float.parseFloat(mPubnativeAdModel.getRating()));
        }
    }

    private void sendLocalMessage(String action) {
        Intent intent = new Intent(BROADCAST_INTENT_EVENT);
        intent.putExtra("action", action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onPubnativeAdModelImpression(PubnativeAdModel pubnativeAdModel, View view) {

    }

    @Override
    public void onPubnativeAdModelClick(PubnativeAdModel pubnativeAdModel, View view) {

    }

    @Override
    public void onPubnativeAdModelOpenOffer(PubnativeAdModel pubnativeAdModel) {

    }
}
