package net.pubnative.library.banner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

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

    public enum Size {
        BANNER_50,
        BANNER_90
    }

    public enum Position {
        TOP,
        BOTTOM
    }

    public PubnativeBanner(Context context, String appToken, Size bannerSize, Position bannerPosition) {
        RelativeLayout banner = null;

        mContext = context;
        mAppToken = appToken;
        mBannerSize = bannerSize;
        mBannerPosition = bannerPosition;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) ((Activity) mContext).findViewById(android.R.id.content)).getChildAt(0);
        switch (bannerSize) {
            case BANNER_90:
                banner = (RelativeLayout) layoutInflater.inflate(R.layout.pubnative_banner_tablet, null);
                break;
            case BANNER_50:
            default:
                banner = (RelativeLayout) layoutInflater.inflate(R.layout.pubnative_banner_phone, null);
                break;
        }
        RelativeLayout bannerView = (RelativeLayout) banner.findViewById(R.id.pubnative_banner_view);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)bannerView.getLayoutParams();
        switch (bannerPosition) {
            case TOP:
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case BOTTOM:
            default:
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
        }
        bannerView.setLayoutParams(params);

        viewGroup.addView(banner);
    }

    public void load() {
        //TODO: load method implementation
    }

    public void show() {
        //TODO: show method implementation
        render();
    }

    public void destroy() {
        //TODO: destroy method implementation
    }

    protected void hide() {
        //TODO: hide method implementation
    }

    protected void render() {
        //TODO: render method implementation

        //invokeShow();
    }

    protected int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeRequest.Listener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onPubnativeRequestSuccess(PubnativeRequest request, List<PubnativeAdModel> ads) {

    }

    @Override
    public void onPubnativeRequestFailed(PubnativeRequest request, Exception ex) {

    }

    //----------------------------------------------------------------------------------------------
    // PubnativeAdModel.Listener
    //----------------------------------------------------------------------------------------------

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
