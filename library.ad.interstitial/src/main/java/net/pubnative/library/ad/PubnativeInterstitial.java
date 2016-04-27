package net.pubnative.library.ad;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.pubnative.library.request.PubnativeAsset;
import net.pubnative.library.request.PubnativeRequest;
import net.pubnative.library.request.model.PubnativeAdModel;

import java.util.List;

public class PubnativeInterstitial implements PubnativeRequest.Listener,
                                              PubnativeAdModel.Listener {

    private static final String TAG = PubnativeInterstitial.class.getSimpleName();
    protected Context                        mContext;
    protected PubnativeAdModel               mAdModel;
    protected PubnativeInterstitial.Listener mListener;
    protected String                         mAppToken;
    protected boolean                        mIsLoading;
    protected boolean                        mIsShown;
    protected WindowManager                  mWindowManager;
    // Interstitial view
    protected RelativeLayout                 mContainer;

    protected TextView                       mTitle;
    protected TextView                       mDescription;
    protected ImageView                      mIcon;
    protected ImageView                      mBanner;
    protected RatingBar                      mRating;
    protected TextView                       mCTA;

    public interface Listener {

        void onPubnativeInterstitialLoadFinish(PubnativeInterstitial interstitial);

        void onPubnativeInterstitialLoadFail(PubnativeInterstitial interstitial, Exception exception);

        void onPubnativeInterstitialShow(PubnativeInterstitial interstitial);

        void onPubnativeInterstitialImpressionConfirmed(PubnativeInterstitial interstitial);

        void onPubnativeInterstitialClick(PubnativeInterstitial interstitial);

        void onPubnativeInterstitialHide(PubnativeInterstitial interstitial);
    }

    public PubnativeInterstitial(Context context, String appToken) {

        mContext = context;
        mAppToken = appToken;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout interstitial = (RelativeLayout) layoutInflater.inflate(R.layout.pubnative_interstitial, null);
        mTitle = (TextView) interstitial.findViewById(R.id.pn_interstitial_title);
        mDescription = (TextView) interstitial.findViewById(R.id.pn_interstitial_description);
        mIcon = (ImageView) interstitial.findViewById(R.id.pn_interstitial_icon);
        mBanner = (ImageView) interstitial.findViewById(R.id.pn_interstitial_banner);
        mRating = (RatingBar) interstitial.findViewById(R.id.pn_interstitial_rating);
        mCTA = (TextView) interstitial.findViewById(R.id.pn_interstitial_cta);
        mContainer = new RelativeLayout(mContext) {

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {

                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    hide();
                    return true;
                }
                return super.dispatchKeyEvent(event);
            }

            @Override
            protected void onWindowVisibilityChanged(int visibility) {
                if(visibility != View.VISIBLE) {
                    Log.v(TAG, "VIEW INVISIBLE");
                    hide();
                }
            }
        };
        mContainer.addView(interstitial);
    }

    /**
     * Sets a callback listener for this interstitial object
     * @param listener valid PubnativeInterstitial.Listener object
     */
    public void setListener(Listener listener) {

        Log.v(TAG, "setListener");
        mListener = listener;
    }

    /**
     * Starts loading an ad for this interstitial
     */
    public void load() {

        Log.v(TAG, "load");
        if (TextUtils.isEmpty(mAppToken)) {
            invokeLoadFail(new Exception("PubnativeInterstitial - load error: app token is null or empty"));
        } else if (mContext == null) {
            invokeLoadFail(new Exception("PubnativeInterstitial - load error: context is null or empty"));
        } else if (mIsLoading) {
            Log.w(TAG, "load - The ad is loaded or being loaded, dropping this call");
        } else if (isReady()) {
            invokeLoadFinish();
        } else {
            mIsShown = false;
            mIsLoading = true;
            PubnativeRequest request = new PubnativeRequest();
            request.setParameter(PubnativeRequest.Parameters.APP_TOKEN, mAppToken);
            String[] assets = new String[] {
                    PubnativeAsset.TITLE,
                    PubnativeAsset.DESCRIPTION,
                    PubnativeAsset.ICON,
                    PubnativeAsset.BANNER,
                    PubnativeAsset.CALL_TO_ACTION,
                    PubnativeAsset.RATING
            };
            request.setParameterArray(PubnativeRequest.Parameters.ASSET_FIELDS, assets);
            request.start(mContext, this);
        }
    }

    public boolean isReady() {

        Log.v(TAG, "setListener");
        return mAdModel != null;
    }

    /**
     * Shows, if not shown and ready the cached interstitial over the current activity
     */
    public void show() {

        Log.v(TAG, "show");
        if (mIsShown) {
            Log.w(TAG, "show - the ad is already shown, ");
        } else if (isReady()) {
            render();
        } else {
            Log.e(TAG, "show - Error: the interstitial is not yet loaded");
        }
    }

    /**
     * Destroy the current interstitial resetting all the cached data and removing any possible interstitial in the screen
     */
    public void destroy() {

        Log.v(TAG, "destroy");
        hide();
        mAdModel = null;
        mIsShown = false;
        mIsLoading = false;
    }

    protected void hide() {

        Log.v(TAG, "hide");
        if (mIsShown) {
            mWindowManager.removeView(mContainer);
            mAdModel.stopTracking();
            mIsShown = false;
            invokeHide();
        }
    }

    protected void render() {

        Log.v(TAG, "render");

        mTitle.setText(mAdModel.getTitle());
        mDescription.setText(mAdModel.getDescription());
        mCTA.setText(mAdModel.getCtaText());
        Picasso.with(mContext).load(mAdModel.getBannerUrl()).into(mBanner);
        Picasso.with(mContext).load(mAdModel.getIconUrl()).into(mIcon);
        if (mAdModel.getRating() != 0) {
            mRating.setRating(mAdModel.getRating());
        } else {
            mRating.setVisibility(View.GONE);
        }
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowManager.addView(mContainer, params);
        mAdModel.startTracking(mContainer, mCTA, this);
        invokeShow();
    }
    //==============================================================================================
    // Callback helpers
    //==============================================================================================

    protected void invokeLoadFinish() {

        Log.v(TAG, "invokeLoadFinish");
        mIsLoading = false;
        if (mListener != null) {
            mListener.onPubnativeInterstitialLoadFinish(this);
        }
    }

    protected void invokeLoadFail(Exception exception) {

        Log.v(TAG, "invokeLoadFail", exception);
        mIsLoading = false;
        if (mListener != null) {
            mListener.onPubnativeInterstitialLoadFail(this, exception);
        }
    }

    protected void invokeShow() {

        mIsShown = true;
        Log.v(TAG, "invokeShow");
        if (mListener != null) {
            mListener.onPubnativeInterstitialShow(this);
        }
    }

    protected void invokeImpressionConfirmed() {

        Log.v(TAG, "invokeImpressionConfirmed");
        if (mListener != null) {
            mListener.onPubnativeInterstitialImpressionConfirmed(this);
        }
    }

    protected void invokeClick() {

        Log.v(TAG, "invokeClick");
        if (mListener != null) {
            mListener.onPubnativeInterstitialClick(this);
        }
    }

    protected void invokeHide() {

        Log.v(TAG, "invokeHide");
        mIsShown = false;
        if (mListener != null) {
            mListener.onPubnativeInterstitialHide(this);
        }
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeRequest.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeRequestSuccess(PubnativeRequest request, List<PubnativeAdModel> ads) {

        Log.v(TAG, "onPubnativeRequestSuccess");
        if (ads == null || ads.size() == 0) {
            invokeLoadFail(new Exception("PubnativeInterstitial - load error: no-fill"));
        } else {
            mAdModel = ads.get(0);
            invokeLoadFinish();
        }
    }

    @Override
    public void onPubnativeRequestFailed(PubnativeRequest request, Exception ex) {

        Log.v(TAG, "onPubnativeRequestFailed");
        invokeLoadFail(ex);
    }

    // PubnativeAdModel.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeAdModelImpression(PubnativeAdModel pubnativeAdModel, View view) {

        Log.v(TAG, "onPubnativeAdModelImpression");
        invokeImpressionConfirmed();
    }

    @Override
    public void onPubnativeAdModelClick(PubnativeAdModel pubnativeAdModel, View view) {

        Log.v(TAG, "onPubnativeAdModelClick");
        invokeClick();
    }

    @Override
    public void onPubnativeAdModelOpenOffer(PubnativeAdModel pubnativeAdModel) {

        Log.v(TAG, "onPubnativeAdModelOpenOffer");
    }
}
