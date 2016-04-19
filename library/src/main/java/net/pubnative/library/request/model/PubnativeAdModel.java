// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.library.request.model;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import net.pubnative.URLDriller;
import net.pubnative.library.tracking.PubnativeImpressionTracker;
import net.pubnative.library.tracking.PubnativeTrackingManager;
import net.pubnative.library.widget.PubnativeWebView;

import java.util.List;
import java.util.Map;

public class PubnativeAdModel implements PubnativeImpressionTracker.Listener,
                                         URLDriller.Listener {

    private static String        TAG          = PubnativeAdModel.class.getSimpleName();

    private PubnativeAdDataModel mPubnativeAdDataModel;

    public PubnativeAdModel(PubnativeAdDataModel adDataModel) {
        mPubnativeAdDataModel = adDataModel;
    }

    //==============================================================================================
    // Listener
    //==============================================================================================

    /**
     * Interface definition for callbacks to be invoked when impression confirmed/failed, ad clicked/clickfailed
     */
    public interface Listener {

        /**
         * Called when impression is confirmed
         *
         * @param pubnativeAdModel PubnativeAdModel impression that was confirmed
         * @param view             The view where impression confirmed
         */
        void onPubnativeAdModelImpression(PubnativeAdModel pubnativeAdModel, View view);

        /**
         * Called when click is confirmed
         *
         * @param pubnativeAdModel PubnativeAdModel that detected the click
         * @param view             The view that was clicked
         */
        void onPubnativeAdModelClick(PubnativeAdModel pubnativeAdModel, View view);

        /**
         * Called before the model opens the offer
         *
         * @param pubnativeAdModel PubnativeAdModel which's offer will be opened
         */
        void onPubnativeAdModelOpenOffer(PubnativeAdModel pubnativeAdModel);
    }

    protected transient Listener mListener;

    //==============================================================================================
    // Fields
    //==============================================================================================
    public String getTitle() {

        Log.v(TAG, "getTitle");
        return mPubnativeAdDataModel.getTitle();
    }

    public String getDescription() {

        Log.v(TAG, "getDescription");
        return mPubnativeAdDataModel.getDescription();
    }

    public String getCtaText() {

        Log.v(TAG, "getCtaText");
        return mPubnativeAdDataModel.getCta();
    }

    /**
     * Get icon url
     *
     * @return iconUrl
     *
     * @deprecated use {@link #getIcon()}
     */
    public String getIconUrl() {

        Log.v(TAG, "getIconUrl");
        return mPubnativeAdDataModel.getIcon().url;
    }

    /**
     * Get object of PubnativeImage as icon
     *
     * @return {@link PubnativeImage}
     */
    public PubnativeImage getIcon() {

        Log.v(TAG, "getIcon");
        return mPubnativeAdDataModel.getIcon();
    }

    /**
     * Get banner url
     *
     * @return bannerUrl
     *
     * @deprecated use {@link #getBanner()}
     */
    @Deprecated
    public String getBannerUrl() {

        Log.v(TAG, "getBannerUrl");
        return mPubnativeAdDataModel.getBanner().url;
    }

    /**
     * Get object of PubnativeImage as banner
     *
     * @return {@link PubnativeImage}
     */
    public PubnativeImage getBanner() {

        Log.v(TAG, "getBanner");
        return mPubnativeAdDataModel.getBanner();
    }

    public String getClickUrl() {

        Log.v(TAG, "getClickUrl");
        return mPubnativeAdDataModel.getClickUrl();
    }

    /**
     * Get revenue model
     *
     * @deprecated use {@link #getMetaField(String)}
     */
    @Deprecated
    public String getRevenueModel() {

        Log.v(TAG, "getRevenueModel");
        return null;
    }

    @Deprecated
    public List<PubnativeBeacon> getBeacons() {

        Log.v(TAG, "getBeacons");
        return null;
    }

    @Deprecated
    public String getType() {

        Log.v(TAG, "getType");
        return null;
    }

    @Deprecated
    public String getPortraitBannerUrl() {

        Log.v(TAG, "getPortraitBannerUrl");
        return null;
    }

    /**
     * Get meta field
     * @param metaType type of meta field
     * @return map
     */
    public Map getMetaField(String metaType) {

        Log.v(TAG, "getMetaFields");
        return mPubnativeAdDataModel.getMetaField(metaType);
    }

    public Float getRating() {

        Log.v(TAG, "getRating");
        Float result = null;
        String rating = mPubnativeAdDataModel.getRating();
        if(rating != null) {
            result = Float.valueOf(rating);
        }
        return result;
    }

    //==============================================================================================
    // Tracking
    //==============================================================================================
    private transient PubnativeImpressionTracker mPubnativeAdTracker     = null;
    private transient boolean                    mIsImpressionConfirmed  = false;
    private transient View                       mClickableView          = null;

    /**
     * Start tracking of ad view
     *
     * @param view     ad view
     * @param listener listener for callbacks
     */
    public void startTracking(View view, Listener listener) {

        Log.v(TAG, "startTracking: both ad view & clickable view are same");
        startTracking(view, view, listener);
    }

    /**
     * start tracking of your ad view
     *
     * @param view          ad view
     * @param clickableView clickable view
     * @param listener      listener for callbacks
     */
    public void startTracking(View view, View clickableView, Listener listener) {

        Log.v(TAG, "startTracking");
        mListener = listener;
        // Impression tracking
        boolean beaconsFound = false;
        if (mPubnativeAdDataModel != null) {
            List<PubnativeBeacon> beacons = mPubnativeAdDataModel.getBeacons(PubnativeBeacon.BeaconType.IMPRESSION);
            if(beacons != null && beacons.size() > 0) {
                beaconsFound = true;
            } else {
                List<String> trackingUrls = mPubnativeAdDataModel.getAssetTrackingUrls();
                if(trackingUrls != null && trackingUrls.size() > 0) {
                    beaconsFound = true;
                }
            }
        }
        if (!beaconsFound) {
            Log.e(TAG, "startTracking - Error: no valid beacon");
        } else if (mIsImpressionConfirmed) {
            Log.v(TAG, "startTracking - impression is already confirmed, dropping impression tracking");
        } else {
            if (mPubnativeAdTracker == null) {
                mPubnativeAdTracker = new PubnativeImpressionTracker();
            }
            mPubnativeAdTracker.startTracking(view, this);
        }
        // Click tracking
        if (mPubnativeAdDataModel == null || TextUtils.isEmpty(mPubnativeAdDataModel.getClickUrl())) {
            Log.e(TAG, "startTracking - Error: click url is empty, clicks won't be tracked");
        } else {
            mClickableView = clickableView;
            mClickableView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Log.v(TAG, "onClick");
                    invokeOnClick(view);
                    URLDriller driller = new URLDriller();
                    driller.setListener(PubnativeAdModel.this);
                    driller.drill(mPubnativeAdDataModel.getClickUrl());
                }
            });
        }
    }

    /**
     * stop tracking of ad view
     */
    public void stopTracking() {

        Log.v(TAG, "stopTracking");
        mPubnativeAdTracker.stopTracking();
        if (mClickableView != null) {
            mClickableView.setOnClickListener(null);
        }
    }

    protected void openURL(String urlString) {

        Log.v(TAG, "openURL: " + urlString);
        if (TextUtils.isEmpty(urlString)) {
            Log.e(TAG, "Error: ending URL cannot be opened - " + urlString);
        } else if (mClickableView == null) {
            Log.e(TAG, "Error: clickable view not set");
        } else {
            try {
                Uri uri = Uri.parse(urlString);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mClickableView.getContext().startActivity(intent);
                invokeOnOpenOffer();
            } catch (Exception ex) {
                Log.e(TAG, "openURL: Error - " + ex.getMessage());
            }
        }
    }

    //==============================================================================================
    // Listener helpers
    //==============================================================================================
    protected void invokeOnImpression(View view) {

        Log.v(TAG, "invokeOnImpression");
        mIsImpressionConfirmed = true;
        if (mListener != null) {
            mListener.onPubnativeAdModelImpression(PubnativeAdModel.this, view);
        }
    }

    protected void invokeOnClick(View view) {

        Log.v(TAG, "invokeOnClick");
        if (mListener != null) {
            mListener.onPubnativeAdModelClick(PubnativeAdModel.this, view);
        }
    }

    protected void invokeOnOpenOffer() {

        Log.v(TAG, "invokeOnOpenOffer");
        if (mListener != null) {
            mListener.onPubnativeAdModelOpenOffer(this);
        }
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // PubnativeImpressionTracker.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onImpressionDetected(View view) {

        Log.v(TAG, "onImpressionDetected");
        // Track beacons
        List<PubnativeBeacon> beacons = mPubnativeAdDataModel.getBeacons(PubnativeBeacon.BeaconType.IMPRESSION);
        if(beacons != null && beacons.size() > 0) {
            for(PubnativeBeacon beacon: beacons) {

                if(!TextUtils.isEmpty(beacon.url)) {
                    PubnativeTrackingManager.track(view.getContext(), beacon.url);
                }

                if(!TextUtils.isEmpty(beacon.js)) {
                    PubnativeWebView webView = new PubnativeWebView(view.getContext());
                    ((ViewGroup)view.getParent()).addView(webView);
                    webView.loadBeacon(beacon.js);
                }
            }
        }

        // Track accessed asset's tracking url
        List<String> assetTrackingUrls = mPubnativeAdDataModel.getAssetTrackingUrls();
        if(assetTrackingUrls != null && assetTrackingUrls.size() > 0) {
            for(String trackingUrl: assetTrackingUrls) {
                PubnativeTrackingManager.track(view.getContext(), trackingUrl);
            }
        }
        invokeOnImpression(view);
    }

    // URLDriller.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onURLDrillerStart(String url) {

        Log.v(TAG, "onURLDrillerStart: " + url);
    }

    @Override
    public void onURLDrillerRedirect(String url) {

        Log.v(TAG, "onURLDrillerRedirect: " + url);
    }

    @Override
    public void onURLDrillerFinish(String url) {

        Log.v(TAG, "onURLDrillerFinish: " + url);
        openURL(url);
    }

    @Override
    public void onURLDrillerFail(String url, Exception exception) {

        Log.v(TAG, "onURLDrillerFail: " + exception);
        openURL(url);
    }
}
