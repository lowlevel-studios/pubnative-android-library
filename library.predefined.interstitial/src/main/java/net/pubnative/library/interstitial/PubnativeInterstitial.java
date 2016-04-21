package net.pubnative.library.interstitial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.pubnative.library.predefined.interstitial.R;
import net.pubnative.library.request.model.PubnativeAdModel;

import static android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class PubnativeInterstitial extends Activity implements PubnativeAdModel.Listener {

    private static final String           TAG                = PubnativeInterstitial.class.getSimpleName();
    public  static final String           EXTRA_AD           = "ad";

    private              TextView         titleView;
    private              TextView         descriptionView;
    private              ImageView        iconView;
    private              ImageView        bannerView;
    private              RatingBar        ratingView;
    private              TextView         downloadView;
    private              PubnativeAdModel mPubnativeAdModel;

    /**
     * Open a fullscreen activity with the data of passed ad
     *
     * @param context Valid context
     * @param adModel Ad
     */
    public static void show(Context context, PubnativeAdModel adModel) {

        Log.v(TAG, "show");

        if(adModel == null) {
            Log.e(TAG, "Invalid data, can't open activity");
        } else {
            Intent adIntent = new Intent(context, PubnativeInterstitial.class);
            adIntent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | FLAG_ACTIVITY_SINGLE_TOP);
            adIntent.putExtra(PubnativeInterstitial.EXTRA_AD, adModel);
            context.startActivity(adIntent);
        }
    }

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
    }

    @Override
    protected void onResume() {

        Log.v(TAG, "onResume");
        super.onResume();

        mPubnativeAdModel.startTracking(findViewById(R.id.pn_interstitial_container), downloadView, this);
    }

    private void renderAd() {

        Log.v(TAG, "renderAd");
        mPubnativeAdModel = (PubnativeAdModel) getIntent().getSerializableExtra(EXTRA_AD);

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
        if(mPubnativeAdModel.getRating() != 0) {
            ratingView.setRating(mPubnativeAdModel.getRating());
        } else {
            Log.i(TAG, "Rating is not available");
            ratingView.setVisibility(View.GONE);
        }
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
