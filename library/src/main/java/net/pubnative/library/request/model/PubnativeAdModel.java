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

public class PubnativeAdModel implements PubnativeImpressionTracker.Listener,
                                         URLDriller.Listener {

    private static String       TAG          = PubnativeAdModel.class.getSimpleName();
    public PubnativeAdDataModel adDataModel;
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
        List<String> allBeacons = adDataModel.getAllBeacons();
        List<String> jsBeacons = adDataModel.getJsBeacons();
        if ((allBeacons == null || allBeacons.size() == 0) && (jsBeacons == null || jsBeacons.size() == 0)) {
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
        if (TextUtils.isEmpty(adDataModel.getClickUrl())) {
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
                    driller.drill(adDataModel.getClickUrl());
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
        for(String beacon: adDataModel.getAllBeacons()) {
            PubnativeTrackingManager.track(view.getContext(), beacon);
        }
        List<String> jsBeacons = adDataModel.getJsBeacons();
        if(jsBeacons != null && jsBeacons.size() > 0) {
            for(String jsBeacon: jsBeacons) {
                PubnativeWebView webView = new PubnativeWebView(view.getContext());
                ((ViewGroup)view.getParent()).addView(webView);
                webView.loadBeacon(jsBeacon);
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
