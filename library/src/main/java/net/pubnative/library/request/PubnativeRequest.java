package net.pubnative.library.request;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.pubnative.library.models.APIRequestResponseModel;
import net.pubnative.library.models.PubnativeAdModel;
import net.pubnative.library.network.PubnativeAPIRequest;
import net.pubnative.library.utils.AndroidAdvertisingIDTask;
import net.pubnative.library.utils.Crypto;
import net.pubnative.library.utils.SystemUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * For every Ad request create new object of this class
 */
public class PubnativeRequest implements AndroidAdvertisingIDTask.Listener {

    private static String TAG = PubnativeRequest.class.getSimpleName();

    protected static final String   BASE_URL        = "http://api.pubnative.net/api/partner/v2/promotions";
    private static final   String   NATIVE_TYPE_URL = "native";

    protected Context  mContext;
    protected Endpoint mEndpoint;
    protected Map<String, String>   requestParameters = new HashMap<String, String>();
    protected Listener              mListener;

    /**
     * These are the various types of adds pubnative support
     */
    public enum Endpoint {

        NATIVE,
    }

    /**
     * These are the various types of parameters
     */
    public interface Parameters {

        String APP_TOKEN                  = "app_token";
        String BUNDLE_ID                  = "bundle_id";
        String APPLE_IDFA                 = "apple_idfa";
        String APPLE_IDFA_SHA1            = "apple_idfa_sha1";
        String APPLE_IDFA_MD5             = "apple_idfa_md5";
        String ANDROID_ADVERTISER_ID      = "android_advertiser_id";
        String ANDROID_ADVERTISER_ID_SHA1 = "android_advertiser_id_sha1";
        String ANDROID_ADVERTISER_ID_MD5  = "android_advertiser_id_md5";
        String ICON_SIZE                  = "icon_size";
        String BANNER_SIZE                = "banner_size";
        String OS                         = "os";
        String DEVICE_MODEL               = "device_model";
        String OS_VERSION                 = "os_version";
        String NO_USER_ID                 = "no_user_id";
        String PARTNER                    = "partner";
        String LOCALE                     = "locale";
        String PORTRAIT_BANNER_SIZE       = "portrait_banner_size";
        String DEVICE_RESOLUTION          = "device_resolution";
        String DEVICE_TYPE                = "device_type";
        String AD_COUNT                   = "ad_count";
        String ZONE_ID                    = "zone_id";
        String LAT                        = "lat";
        String LONG                       = "long";
        String GENDER                     = "gender";
        String AGE                        = "age";
        String KEYWORDS                   = "keywords";
    }

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
        void onPubnativeRequestSuccess(PubnativeRequest request, List<PubnativeAdModel> ads);

        /**
         * Invoked when PubnativeRequest request fails
         *
         * @param request Request object used for making the request
         * @param ex      Exception that caused the failure
         */
        void onPubnativeRequestFailed(PubnativeRequest request, Exception ex);
    }

    public interface APIRequestListener {
        void invokeOnResponse(String response);

        void invokeOnErrorResponse(Exception error);
    }

    /**
     * Creates object of PubnativeRequest
     */
    public PubnativeRequest(Context context) {
        mContext = context;
    }

    /**
     * Sets parameters required to make the pub native request
     *
     * @param key   key name of parameter
     * @param value actual value of parameter
     */
    public void setParameter(String key, String value) {

        if (TextUtils.isEmpty(key)) {

            Log.e(TAG, "Invalid key passed for parameter");
            return;
        }

        if (TextUtils.isEmpty(value)) {

            requestParameters.remove(key);

        } else {

            requestParameters.put(key, value);
        }
    }

    /**
     * Starts pub native request, This function make the ad request to the pubnative server. It makes asynchronous network request in the background.
     *
     * @param endpoint endpoint of ad (ex: NATIVE)
     * @param listener valid nativeRequestListener to track ad request callbacks.
     */
    public void start(Endpoint endpoint, Listener listener) {

        if (listener != null) {

            mListener = listener;

            if (endpoint != null && mContext != null) {

                mEndpoint = endpoint;

                setDefaultParameters();

                if (!requestParameters.containsKey(Parameters.ANDROID_ADVERTISER_ID)) {

                    new AndroidAdvertisingIDTask().setListener(this).execute(mContext);

                } else {

                    sendNetworkRequest();
                }

            } else {

                invokeOnPubnativeRequestFailure(new IllegalArgumentException("start - Arguments cannot be null"));
            }

        } else {

            Log.e(TAG, "start - Request started without listener, dropping call");
        }
    }

    /**
     * setting optional parameters to request parameters
     */
    protected void setDefaultParameters() {

        if (!this.requestParameters.containsKey(Parameters.BUNDLE_ID)) {

            this.requestParameters.put(Parameters.BUNDLE_ID, SystemUtils.getPackageName(mContext));
        }

        if (!this.requestParameters.containsKey(Parameters.OS)) {

            this.requestParameters.put(Parameters.OS, "android");
        }

        if (!this.requestParameters.containsKey(Parameters.DEVICE_MODEL)) {

            this.requestParameters.put(Parameters.DEVICE_MODEL, Build.MODEL);
        }

        if (!this.requestParameters.containsKey(Parameters.OS_VERSION)) {

            this.requestParameters.put(Parameters.OS_VERSION, Build.VERSION.RELEASE);
        }

        if (!this.requestParameters.containsKey(Parameters.DEVICE_RESOLUTION)) {

            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
            this.requestParameters.put(Parameters.DEVICE_RESOLUTION, dm.widthPixels + "x" + dm.heightPixels);
        }

        if (!this.requestParameters.containsKey(Parameters.DEVICE_TYPE)) {

            this.requestParameters.put(Parameters.DEVICE_TYPE, SystemUtils.isTablet(mContext) ? "tablet" : "phone");
        }

        if (!this.requestParameters.containsKey(Parameters.LOCALE)) {

            this.requestParameters.put(Parameters.LOCALE, Locale.getDefault().getLanguage());
        }

        // If none of lat and long is sent by the client then only we add default values. We can't alter client's parameters.
        if (SystemUtils.isLocationPermissionGranted(mContext)) {

            Location location = SystemUtils.getLastLocation(mContext);

            if (location != null &&
                !this.requestParameters.containsKey(Parameters.LAT) &&
                !this.requestParameters.containsKey(Parameters.LONG)) {

                this.requestParameters.put(Parameters.LAT, String.valueOf(location.getLatitude()));
                this.requestParameters.put(Parameters.LONG, String.valueOf(location.getLongitude()));
            }
        }
    }

    /**
     * This function is used to create network request.
     */
    protected String getRequestURL() {

        String url = null;

        if (mEndpoint != null) {

            switch (mEndpoint) {

                case NATIVE:

                    // Creating base URI
                    Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon();
                    uriBuilder.appendPath(NATIVE_TYPE_URL);

                    // Appending parameters
                    for (String key : requestParameters.keySet()) {

                        String value = requestParameters.get(key);

                        if (key != null && value != null) {

                            uriBuilder.appendQueryParameter(key, value);
                        }
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
     * This function will create and send the network request, It uses Volley internally for network communication.
     * It consider that <code>type<code/> is already provided so that it can prepare the request URL.
     */
    protected void sendNetworkRequest() {

        String url = getRequestURL();

        if (url == null) {

            invokeOnPubnativeRequestFailure(new Exception("sendNetworkRequest - Error getting request URL"));

        } else {

            PubnativeAPIRequest.send(PubnativeAPIRequest.Method.GET, url, apiRequestListener);
        }
    }

    //===================================
    // Callback Helpers
    //===================================

    protected void invokeOnPubnativeRequestSuccess(List<PubnativeAdModel> ads) {

        if (mListener != null) {

            mListener.onPubnativeRequestSuccess(this, ads);
        }
    }

    protected void invokeOnPubnativeRequestFailure(Exception exception) {

        Log.e(TAG, "Request error: " + exception);

        if (mListener != null) {

            mListener.onPubnativeRequestFailed(this, exception);
        }
    }

    //===================================
    // Callbacks
    //===================================

    // AndroidAdvertisingIDTask.Listener
    //-----------------------------------

    /**
     * Android Advertising Id Task Listener is finished when android ad id is fetched.
     */
    @Override
    public void onAndroidAdIdTaskFinished(String result) {

        if (TextUtils.isEmpty(result)) {

            this.requestParameters.put(Parameters.NO_USER_ID, "1");

        } else {

            this.requestParameters.put(Parameters.ANDROID_ADVERTISER_ID, result);
            this.requestParameters.put(Parameters.ANDROID_ADVERTISER_ID_SHA1, Crypto.sha1(result));
            this.requestParameters.put(Parameters.ANDROID_ADVERTISER_ID_MD5, Crypto.md5(result));
        }

        sendNetworkRequest();
    }

    protected APIRequestListener apiRequestListener = new APIRequestListener() {

        @Override
        public void invokeOnResponse(String response) {
            if (!TextUtils.isEmpty(response)) {

                try {

                    APIRequestResponseModel model = new Gson().fromJson(response, APIRequestResponseModel.class);

                    if (model != null) {

                        if (APIRequestResponseModel.Status.OK.equals(model.status)) {

                            // SUCCESS
                            invokeOnPubnativeRequestSuccess(model.ads);

                        } else {

                            // ERROR: request error
                            invokeOnPubnativeRequestFailure(new Exception(model.error_message));
                        }

                    } else {

                        // ERROR: parsing error
                        invokeOnPubnativeRequestFailure(new Exception("Response error"));
                    }

                } catch (JsonSyntaxException exception) {

                    // ERROR: json error
                    invokeOnPubnativeRequestFailure(exception);
                }

            } else {

                // ERROR: empty response
                invokeOnPubnativeRequestFailure(new Exception("Server response empty"));
            }
        }

        @Override
        public void invokeOnErrorResponse(Exception error) {
            invokeOnPubnativeRequestFailure(error);
        }
    };
}
