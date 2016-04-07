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

package net.pubnative.library.request;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import net.pubnative.AdvertisingIdClient;
import net.pubnative.library.network.PubnativeHttpRequest;
import net.pubnative.library.request.model.PubnativeAPIV3AdModel;
import net.pubnative.library.request.model.PubnativeAPIV3ResponseModel;
import net.pubnative.library.utils.Crypto;
import net.pubnative.library.utils.SystemUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PubnativeRequest implements PubnativeHttpRequest.Listener, AdvertisingIdClient.Listener {

    private static         String               TAG                = PubnativeRequest.class.getSimpleName();
    private static final   String               NATIVE_TYPE_URL    = "native";
    protected static final String               BASE_URL           = "http://api.pubnative.net/api/v3";
    protected              Context              mContext           = null;
    protected              Endpoint             mEndpoint          = null;
    protected              Map<String, String>  mRequestParameters = new HashMap<String, String>();
    protected              Listener             mListener          = null;
    protected              PubnativeHttpRequest mRequest           = null;
    protected              boolean              mIsRunning         = false;
    protected              List<String>         mAssetList         = null;
    protected              List<String>         mMetaList          = null;

    /**
     * These are the various types of adds pubnative support
     */
    public enum Endpoint {
        NATIVE,
    }
    //==============================================================================================
    // REQUEST PARAMETERS
    //==============================================================================================

    /**
     * These are the various types of parameters
     */
    public interface Parameters {

        String APP_TOKEN                  = "apptoken";
        String ANDROID_ADVERTISER_ID      = "gid";
        String ANDROID_ADVERTISER_ID_SHA1 = "gidsha1";
        String ANDROID_ADVERTISER_ID_MD5  = "gidmd5";
        String OS                         = "os";
        String OS_VERSION                 = "osver";
        String DEVICE_MODEL               = "devicemodel";
        String NO_USER_ID                 = "dnt";
        String LOCALE                     = "locale";
        String AD_COUNT                   = "adcount";
        String ZONE_ID                    = "zoneid";
        String LAT                        = "lat";
        String LONG                       = "long";
        String GENDER                     = "gender";
        String AGE                        = "age";
        String KEYWORDS                   = "keywords";
        String APP_VERSION                = "appver";
        String TEST                       = "test";
        String VIDEO                      = "video";
        String META_FIELDS                = "mf";
        String ASSET_FIELDS               = "af";
    }

    //==============================================================================================
    // LISTENER
    //==============================================================================================

    /**
     * Listener interface used to start Pubnative request with success and failure callbacks.
     */
    public interface Listener {

        /**
         * Invoked when PubnativeRequest request is success
         *
         * @param request Request object used for making the request
         * @param ads     List of ads received
         */
        void onPubnativeRequestSuccess(PubnativeRequest request, List<PubnativeAPIV3AdModel> ads);

        /**
         * Invoked when PubnativeRequest request fails
         *
         * @param request Request object used for making the request
         * @param ex      Exception that caused the failure
         */
        void onPubnativeRequestFailed(PubnativeRequest request, Exception ex);
    }
    //==============================================================================================
    // Public
    //==============================================================================================

    /**
     * Sets parameters required to make the pub native request
     *
     * @param key   key name of parameter
     * @param value actual value of parameter
     */
    public void setParameter(String key, String value) {

        Log.v(TAG, "setParameter: " + key + " : " + value);
        if (TextUtils.isEmpty(key)) {
            Log.e(TAG, "Invalid key passed for parameter");
            return;
        }
        if (TextUtils.isEmpty(value)) {
            mRequestParameters.remove(key);
        } else {
            mRequestParameters.put(key, value);
        }
    }

    /**
     * Sets desired assets type in request
     *
     * @param assetFields list of asset types
     */
    public void setAssetFields(List<String> assetFields) {
        mAssetList = assetFields;
    }

    /**
     * Add asset type in request
     *
     * @param assetField asset type
     */
    public void addAssetField(String assetField) {
        if(mAssetList == null) {
            mAssetList = new ArrayList<String>();
        }

        mAssetList.add(assetField);
    }

    /**
     * Sets desired meta types in request
     *
     * @param metaFields list of asset types
     */
    public void setMetaFields(List<String> metaFields) {
        mAssetList = metaFields;
    }

    /**
     * Add meta type in request
     *
     * @param metaType meta type
     */
    public void addMetaField(String metaType) {
        if(mAssetList == null) {
            mAssetList = new ArrayList<String>();
        }

        mAssetList.add(metaType);
    }

    /**
     * Starts pub native request, This function make the ad request to the pubnative server. It makes asynchronous network request in the background.
     *
     * @param context valid Context object
     * @param endpoint endpoint of ad (ex: NATIVE)
     * @param listener valid nativeRequestListener to track ad request callbacks.
     */
    public void start(Context context, Endpoint endpoint, Listener listener) {

        Log.v(TAG, "start");
        if (listener == null) {
            Log.e(TAG, "start - Request started without listener, dropping call");
        } else {
            mListener = listener;
            if (context == null) {
                invokeOnFail(new IllegalArgumentException("PubnativeRequest - Error: context is null"));
            } else if(mIsRunning) {
                Log.w(TAG, "PubnativeRequest - this request is already running, dropping the call");
            } else {
                mIsRunning = true;
                mContext = context;
                mEndpoint = endpoint;
                setDefaultParameters();
                if (!mRequestParameters.containsKey(Parameters.ANDROID_ADVERTISER_ID)) {
                    AdvertisingIdClient.getAdvertisingId(mContext, this);
                } else {
                    sendNetworkRequest();
                }
            }
        }
    }

    //==============================================================================================
    // Private
    //==============================================================================================
    protected void setDefaultParameters() {

        Log.v(TAG, "setDefaultParameters");
        if (!mRequestParameters.containsKey(Parameters.OS)) {
            mRequestParameters.put(Parameters.OS, "android");
        }
        if (!mRequestParameters.containsKey(Parameters.DEVICE_MODEL)) {
            mRequestParameters.put(Parameters.DEVICE_MODEL, SystemUtils.isTablet(mContext) ? "tablet" : "phone");
        }
        if (!mRequestParameters.containsKey(Parameters.OS_VERSION)) {
            mRequestParameters.put(Parameters.OS_VERSION, Build.VERSION.RELEASE);
        }
        if (!mRequestParameters.containsKey(Parameters.LOCALE)) {
            mRequestParameters.put(Parameters.LOCALE, Locale.getDefault().getLanguage());
        }
        // If none of lat and long is sent by the client then only we add default values. We can't alter client's parameters.
        if (!mRequestParameters.containsKey(Parameters.LAT)
                && !mRequestParameters.containsKey(Parameters.LONG)
                && SystemUtils.isLocationPermissionGranted(mContext)) {
            Location location = SystemUtils.getLastLocation(mContext);
            if(location != null) {
                mRequestParameters.put(Parameters.LAT, String.valueOf(location.getLatitude()));
                mRequestParameters.put(Parameters.LONG, String.valueOf(location.getLongitude()));
            }
        }
    }

    protected String getRequestURL() {

        Log.v(TAG, "getRequestURL");
        String url = null;
        if (mEndpoint != null) {
            switch (mEndpoint) {
                case NATIVE:
                    // Creating base URI
                    Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon();
                    uriBuilder.appendPath(NATIVE_TYPE_URL);
                    // Appending parameters
                    for (String key : mRequestParameters.keySet()) {
                        String value = mRequestParameters.get(key);
                        if (key != null && value != null) {
                            uriBuilder.appendQueryParameter(key, value);
                        }
                    }

                    if(mAssetList != null && mAssetList.size() > 0) {
                        uriBuilder.appendQueryParameter(Parameters.ASSET_FIELDS, TextUtils.join(",", mAssetList));
                    }

                    if(mMetaList != null && mMetaList.size() > 0) {
                        uriBuilder.appendQueryParameter(Parameters.META_FIELDS, TextUtils.join(",", mMetaList));
                    }

                    // Final URL
                    url = uriBuilder.build().toString();
                    break;
                default:
                    // Error: Invalid ENDPOINT
                    Log.e(TAG, "getRequestURL - type not recognized");
                    break;
            }
        }
        return url;
    }

    /**
     * This function will create and send the network request.
     * It consider that <code>type</code> is already provided so that it can prepare the request URL.
     */
    protected void sendNetworkRequest() {

        Log.v(TAG, "sendNetworkRequest");
        String url = getRequestURL();
        if (url == null) {
            invokeOnFail(new Exception("PubnativeRequest - Error: invalid request URL"));
        } else {
            mRequest = new PubnativeHttpRequest();
            mRequest.start(mContext, url, this);
        }
    }

    //==============================================================================================
    // Listener Helpers
    //==============================================================================================
    protected void invokeOnSuccess(List<PubnativeAPIV3AdModel> ads) {

        Log.v(TAG, "invokeOnSuccess");
        mIsRunning = false;
        if (mListener != null) {
            mListener.onPubnativeRequestSuccess(this, ads);
        }
    }

    protected void invokeOnFail(Exception exception) {

        Log.v(TAG, "invokeOnFail: " + exception);
        mIsRunning = false;
        if (mListener != null) {
            mListener.onPubnativeRequestFailed(this, exception);
        }
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // PubnativeHttpRequest.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeHttpRequestStart(PubnativeHttpRequest request) {

        Log.v(TAG, "onPubnativeHttpRequestStart");
    }

    @Override
    public void onPubnativeHttpRequestFinish(PubnativeHttpRequest request, String result) {

        Log.v(TAG, "onPubnativeHttpRequestFinish");
        try {
            PubnativeAPIV3ResponseModel apiResponseModel = new Gson().fromJson(result, PubnativeAPIV3ResponseModel.class);
            if (apiResponseModel == null) {
                invokeOnFail(new Exception("PubnativeRequest - Error: Response JSON error"));
            } else if (PubnativeAPIV3ResponseModel.Status.OK.equals(apiResponseModel.status)) {
                invokeOnSuccess(apiResponseModel.ads);
            } else {
                invokeOnFail(new Exception("PubnativeRequest - Error: Server error: " + apiResponseModel.error_message));
            }
        } catch (Exception exception) {
            invokeOnFail(exception);
        }
    }

    @Override
    public void onPubnativeHttpRequestFail(PubnativeHttpRequest request, Exception exception) {

        Log.v(TAG, "onPubnativeHttpRequestFail: " + exception);
        invokeOnFail(exception);
    }

    // AdvertisingIdClient.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdvertisingIdClientFinish(AdvertisingIdClient.AdInfo adInfo) {
        Log.v(TAG, "onAdvertisingIdClientFinish");
        if (adInfo != null && !adInfo.isLimitAdTrackingEnabled()) {
            String advertisingId = adInfo.getId();
            mRequestParameters.put(Parameters.ANDROID_ADVERTISER_ID, advertisingId);
            mRequestParameters.put(Parameters.ANDROID_ADVERTISER_ID_SHA1, Crypto.sha1(advertisingId));
            mRequestParameters.put(Parameters.ANDROID_ADVERTISER_ID_MD5, Crypto.md5(advertisingId));
        } else {
            mRequestParameters.put(Parameters.NO_USER_ID, "1");
        }
        sendNetworkRequest();
    }

    @Override
    public void onAdvertisingIdClientFail(Exception exception) {
        Log.v(TAG, "onAdvertisingIdClientFail");
        mRequestParameters.put(Parameters.NO_USER_ID, "1");
        sendNetworkRequest();
    }
}
