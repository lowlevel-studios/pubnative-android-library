package pubnative.net.ad.feed.banner;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.pubnative.library.request.PubnativeAsset;
import net.pubnative.library.request.PubnativeRequest;
import net.pubnative.library.request.model.PubnativeAdModel;

import java.util.List;

public class PubnativeFeedBanner implements PubnativeRequest.Listener {

    private static final String  TAG                  = PubnativeFeedBanner.class.getSimpleName();

    protected Context                       mContext;
    protected String                        mAppToken;
    protected PubnativeFeedBanner.Listener  mListener;
    protected boolean                       mIsLoading = false;
    protected Handler                       mHandler;

    // Banner view
    protected ViewGroup                     mContainer;
    protected TextView                      mTitle;
    protected TextView                      mDescription;
    protected ImageView                     mIcon;
    protected RelativeLayout                mBannerView;
    protected Button                        mInstall;

    /**
     * Interface for callbacks related to the in-feed banner
     */
    public interface Listener {

        /**
         * Called whenever the in-feed banner finished loading ad ad
         *
         * @param feedBanner feedBanner that finished the load
         */
        void onPubnativeFeedBannerLoadFinish(PubnativeFeedBanner feedBanner);

        /**
         * Called whenever the in-feed banner failed loading an ad
         *
         * @param feedBanner feedBanner that failed the load
         * @param exception  exception with the description of the load error
         */
        void onPubnativeFeedBannerLoadFailed(PubnativeFeedBanner feedBanner, Exception exception);

        /**
         * Called when the in-feed banner is shown on the screen
         *
         * @param feedBanner feedBanner that is shown on the screen
         */
        void onPubnativeInterstitialShow(PubnativeFeedBanner feedBanner);

        /**
         * Called when the in-feed banner impression was confirmed
         *
         * @param feedBanner feedBanner which impression was confirmed
         */
        void onPubnativeInterstitialImpressionConfirmed(PubnativeFeedBanner feedBanner);

        /**
         * Called whenever the in-feed banner was clicked by the user
         *
         * @param feedBanner feedBanner that was clicked
         */
        void onPubnativeInterstitialClick(PubnativeFeedBanner feedBanner);
    }

    public PubnativeFeedBanner(Context context, String appToken, Listener listener) {

        mContext = context;
        mAppToken = appToken;
        mListener = listener;
        mHandler = new Handler(Looper.getMainLooper());
        if (TextUtils.isEmpty(mAppToken)) {
            invokeLoadFail(new Exception("PubnativeBanner - load error: app token is null or empty"));
        } else if (mContext == null) {
            invokeLoadFail(new Exception("PubnativeBanner - load error: context is null or empty"));
        } else if (!(context instanceof Activity)) {
            invokeLoadFail(new Exception("PubnativeBanner - load error: wrong context type"));
        }
    }

    public void load() {

        Log.v(TAG, "load");
        if (mContext == null) {
            invokeLoadFail(new Exception("PubnativeBanner - load error: context is null or empty"));
        } else if (mIsLoading) {
            Log.w(TAG, "load - The ad is loaded or being loaded, dropping this call");
        } else if (isReady()) {
            invokeLoadFinish();
        } else {
            mIsLoading = true;
            mHandler = new Handler(Looper.getMainLooper());
            initialize();
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

    public boolean isReady() {
        boolean result = false;
        return result;
    }

    public void show(ViewGroup view) {

    }

    @Override
    public void onPubnativeRequestSuccess(PubnativeRequest request, List<PubnativeAdModel> ads) {

    }

    @Override
    public void onPubnativeRequestFailed(PubnativeRequest request, Exception ex) {

    }

    private void initialize() {

    }

    private void invokeLoadFail(Exception exception) {

    }

    private void invokeLoadFinish() {

    }
}
