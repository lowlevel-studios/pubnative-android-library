package net.pubnative.library.banner;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.pubnative.library.request.PubnativeAsset;
import net.pubnative.library.request.PubnativeRequest;
import net.pubnative.library.request.model.PubnativeAdModel;

import java.util.List;

public class PubnativeBanner implements PubnativeRequest.Listener,
                                        PubnativeAdModel.Listener {

    public static final String TAG = PubnativeBanner.class.getSimpleName();
    protected Context               mContext;
    protected String                mAppToken;
    protected Size                  mBannerSize;
    protected Position              mBannerPosition;
    protected Boolean               mIsLoading = false;
    protected Boolean               mIsShown = false;
    protected Listener              mListener;
    protected PubnativeAdModel      mAdModel;
    protected Handler               mHandler;
    // Banner view
    protected ViewGroup             mContainer;
    protected TextView              mTitle;
    protected TextView              mDescription;
    protected ImageView             mIcon;
    protected RelativeLayout        mBannerView;
    protected Button                mInstall;

    public enum Size {
        BANNER_50,
        BANNER_90
    }

    public enum Position {
        TOP,
        BOTTOM
    }

    /**
     * Interface for callbacks related to the banner view behaviour
     */
    public interface Listener {

        /**
         * Called whenever the banner finished loading an ad
         *
         * @param banner banner that finished the load
         */
        void onPubnativeBannerLoadFinish(PubnativeBanner banner);

        /**
         * Called whenever the banner failed loading an ad
         *
         * @param banner banner that failed the load
         * @param exception    exception with the description of the load error
         */
        void onPubnativeBannerLoadFail(PubnativeBanner banner, Exception exception);

        /**
         * Called when the Banner was just shown on the screen
         *
         * @param banner banner that was shown in the screen
         */
        void onPubnativeBannerShow(PubnativeBanner banner);

        /**
         * Called when the banner impression was confrimed
         *
         * @param banner banner which impression was confirmed
         */
        void onPubnativeBannerImpressionConfirmed(PubnativeBanner banner);

        /**
         * Called whenever the banner was clicked by the user
         *
         * @param banner banner that was clicked
         */
        void onPubnativeBannerClick(PubnativeBanner banner);

        /**
         * Called whenever the banner was removed from the screen
         *
         * @param banner banner that was hidden
         */
        void onPubnativeBannerHide(PubnativeBanner banner);
    }

    public PubnativeBanner() {

    }

    /**
     * Sets a callback listener for this interstitial object
     *
     * @param listener valid PubnativeInterstitial.Listener object
     */
    public void setListener(Listener listener) {

        Log.v(TAG, "setListener");
        mListener = listener;
    }

    /**
     * Starts loading an ad for this interstitial
     *
     * @param context context of {@link Activity}, where is banner will show
     * @param appToken application token from settings
     * @param bannerSize size of banner
     * @param bannerPosition banner position on the screen
     */
    public void load(Context context, String appToken, Size bannerSize, Position bannerPosition) {

        buildBanner(context, appToken, bannerSize, bannerPosition);

        Log.v(TAG, "load");
        if (TextUtils.isEmpty(mAppToken)) {
            invokeLoadFail(new Exception("PubnativeBanner - load error: app token is null or empty"));
        } else if (mContext == null) {
            invokeLoadFail(new Exception("PubnativeBanner - load error: context is null or empty"));
        } else if (mIsLoading) {
            Log.w(TAG, "load - The ad is loaded or being loaded, dropping this call");
        } else if (mIsShown) {
            Log.w(TAG, "load - The ad is shown, dropping this call");
        } else if (isReady()) {
            invokeLoadFinish();
        } else {
            mIsLoading = true;
            mHandler = new Handler(Looper.getMainLooper());
            PubnativeRequest request = new PubnativeRequest();
            request.setParameter(PubnativeRequest.Parameters.APP_TOKEN, mAppToken);
            String[] assets = new String[] {
                    PubnativeAsset.TITLE,
                    PubnativeAsset.DESCRIPTION,
                    PubnativeAsset.ICON,
                    PubnativeAsset.CALL_TO_ACTION
            };
            request.setParameterArray(PubnativeRequest.Parameters.ASSET_FIELDS, assets);
            request.start(mContext, this);
        }
    }

    /**
     * Method that checks if the banner is ready to be shown in the screen
     *
     * @return true if the banner can be shown false if not
     */
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
            Log.e(TAG, "show - Error: the banner is not yet loaded");
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

    //==============================================================================================
    // Helpers
    //==============================================================================================

    /**
     * Banner constructor method
     *
     * @param context context of {@link Activity}, where is banner will show
     * @param appToken application token from settings
     * @param bannerSize size of banner
     * @param bannerPosition banner position on the screen
     */
    protected void buildBanner(Context context, String appToken, Size bannerSize, Position bannerPosition) {

        mContext = context;
        mAppToken = appToken;
        mBannerSize = bannerSize;
        mBannerPosition = bannerPosition;

        RelativeLayout banner;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (context instanceof Activity) {
            mContainer = (ViewGroup) ((ViewGroup) ((Activity) mContext).findViewById(android.R.id.content)).getChildAt(0);
        } else {
            Log.e(TAG, "Wrong type of Context. Must be Activity context");
            mContainer = new RelativeLayout(context);
        }
        switch (bannerSize) {
            case BANNER_90:
                banner = (RelativeLayout) layoutInflater.inflate(R.layout.pubnative_banner_tablet, null);
                break;
            case BANNER_50:
            default:
                banner = (RelativeLayout) layoutInflater.inflate(R.layout.pubnative_banner_phone, null);
                break;
        }

        mBannerView = (RelativeLayout) banner.findViewById(R.id.pubnative_banner_view);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBannerView.getLayoutParams();
        switch (bannerPosition) {
            case TOP:
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case BOTTOM:
            default:
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
        }
        mTitle = (TextView) banner.findViewById(R.id.pubnative_banner_title);
        mDescription = (TextView) banner.findViewById(R.id.pubnative_banner_description);
        mIcon = (ImageView) banner.findViewById(R.id.pubnative_banner_image);
        mInstall = (Button) banner.findViewById(R.id.pubnative_banner_button);

        mBannerView.setLayoutParams(params);
        mContainer.addView(banner);
    }

    protected void hide() {
        Log.v(TAG, "hide");
        if (mIsShown) {
            RelativeLayout rootBannerView = (RelativeLayout) mContainer.findViewById(R.id.pubnative_banner_root_view);
            mContainer.removeView(rootBannerView);
            mAdModel.stopTracking();
            mIsShown = false;
            invokeHide();
        }
    }

    protected void render() {
        Log.v(TAG, "render");
        mTitle.setText(mAdModel.getTitle());
        mDescription.setText(mAdModel.getDescription());
        mInstall.setText(mAdModel.getCtaText());
        Picasso.with(mContext).load(mAdModel.getIconUrl()).into(mIcon);
        mAdModel.startTracking(mContainer, mBannerView, this);
        invokeShow();
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeLoadFinish() {

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                Log.v(TAG, "invokeLoadFinish");
                mIsLoading = false;
                if (mListener != null) {
                    mListener.onPubnativeBannerLoadFinish(PubnativeBanner.this);
                }
            }
        });
    }

    protected void invokeLoadFail(final Exception exception) {

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                Log.v(TAG, "invokeLoadFail", exception);
                mIsLoading = false;
                if (mListener != null) {
                    mListener.onPubnativeBannerLoadFail(PubnativeBanner.this, exception);
                }
            }
        });
    }

    protected void invokeShow() {

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                mIsShown = true;
                Log.v(TAG, "invokeShow");
                if (mListener != null) {
                    mListener.onPubnativeBannerShow(PubnativeBanner.this);
                }
            }
        });
    }

    protected void invokeImpressionConfirmed() {

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                Log.v(TAG, "invokeImpressionConfirmed");
                if (mListener != null) {
                    mListener.onPubnativeBannerImpressionConfirmed(PubnativeBanner.this);
                }
            }
        });
    }

    protected void invokeClick() {

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                Log.v(TAG, "invokeClick");
                if (mListener != null) {
                    mListener.onPubnativeBannerClick(PubnativeBanner.this);
                }
            }
        });
    }

    protected void invokeHide() {

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                Log.v(TAG, "invokeHide");
                mIsShown = false;
                if (mListener != null) {
                    mListener.onPubnativeBannerHide(PubnativeBanner.this);
                }
            }
        });
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
            invokeLoadFail(new Exception("PubnativeBanner - load error: no-fill"));
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

    //----------------------------------------------------------------------------------------------
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
