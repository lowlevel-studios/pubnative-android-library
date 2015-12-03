package net.pubnative.library.request;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.pubnative.library.models.PubnativeAdModel;
import net.pubnative.library.utils.Crypto;
import net.pubnative.library.utils.SystemUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * For every Ad request create new object of this class
 */
public class PubnativeRequest implements SystemUtils.AndroidAdIdTask.AndroidAdIdListener, Response.Listener<String>, Response.ErrorListener {
    private static final String   BASE_URL        =  "http://api.pubnative.net/api/partner/v2/promotions";
    private static final String   NATIVE_TYPE_URL =  "native";
    protected Context             context;
    protected EndPoint            endpoint;
    protected Map<String, String> requestParameters;
    protected Listener            listener;

    /**
     * These are the various types of adds pubnative support
     */
    public enum EndPoint {
        NATIVE
    }

    /**
     * These are the various types of parameters
     */
    public interface Parameters {
        String APP_TOKEN                  =      "app_token";
        String BUNDLE_ID                  =      "bundle_id";
        String APPLE_IDFA                 =      "apple_idfa";
        String APPLE_IDFA_SHA1            =      "apple_idfa_sha1";
        String APPLE_IDFA_MD5             =      "apple_idfa_md5";
        String ANDROID_ADVERTISER_ID      =      "android_advertiser_id";
        String ANDROID_ADVERTISER_ID_SHA1 =      "android_advertiser_id_sha1";
        String ANDROID_ADVERTISER_ID_MD5  =      "android_advertiser_id_md5";
        String ICON_SIZE                  =      "icon_size";
        String BANNER_SIZE                =      "banner_size";
        String OS                         =      "os";
        String DEVICE_MODEL               =      "device_model";
        String OS_VERSION                 =      "os_version";
        String NO_USER_ID                 =      "no_user_id";
        String PARTNER                    =      "partner";
        String LOCALE                     =      "locale";
        String PORTRAIT_BANNER_SIZE       =      "portrait_banner_size";
        String DEVICE_RESOLUTION          =      "device_resolution";
        String DEVICE_TYPE                =      "device_type";
        String AD_COUNT                   =      "ad_count";
        String ZONE_ID                    =      "zone_id";
        String LAT                        =      "lat";
        String LONG                       =      "long";
        String GENDER                     =      "gender";
        String AGE                        =      "age";
        String KEYWORDS                   =      "keywords";
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
        void onPubnativeRequestFail(PubnativeRequest request, Exception ex);
    }

    /**
     * Sets parameters required to make the pub native request
     *
     * @param key key name of parameter
     * @param value actual value of parameter
     */
    public void setParameter(String key, String value) {
        if (requestParameters == null) {
            requestParameters = new HashMap<String, String>();
        }
        if (value == null || TextUtils.isEmpty(value)) {
            requestParameters.remove(key);
        } else {
            requestParameters.put(key, value);
        }
    }

    /**
     * Starts pub native request, This function make the ad request to the pubnative server. It makes asynchronous network request in the background.
     *
     * @param context is application context
     * @param endpoint type of ad (ex: NATIVE)
     * @param listener valid listener to track ad request callbacks.
     */
    public void start(Context context, EndPoint endpoint, @NonNull Listener listener) {
        this.context = context;
        this.listener = listener;
        this.endpoint = endpoint;
        if (this.listener != null) {
            if (requestParameters == null) {
                requestParameters = new HashMap<String, String>();
            }
            setOptionalParameters();
            if (!requestParameters.containsKey(Parameters.ANDROID_ADVERTISER_ID)) {
                SystemUtils.getAndroidAdID(this.context, this);
            } else {
                String url = createNetworkRequest();
                sendNetworkRequest(url);
            }
        }
    }

    /**
     *  setting optional parameters to request parameters
     */
    protected void setOptionalParameters() {
        if (!this.requestParameters.containsKey(Parameters.BUNDLE_ID)) {
            this.requestParameters.put(Parameters.BUNDLE_ID, SystemUtils.getPackageName(this.context));
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
            DisplayMetrics dm = this.context.getResources().getDisplayMetrics();
            this.requestParameters.put(Parameters.DEVICE_RESOLUTION, dm.widthPixels + "x" + dm.heightPixels);
        }
        if (!this.requestParameters.containsKey(Parameters.DEVICE_TYPE)) {
            this.requestParameters.put(Parameters.DEVICE_TYPE, SystemUtils.isTablet(this.context) ? "tablet" : "phone");
        }
        if (!this.requestParameters.containsKey(Parameters.LOCALE)) {
            this.requestParameters.put(Parameters.LOCALE, Locale.getDefault().getLanguage());
        }

        if (SystemUtils.isLocationPermissionGranted(this.context)) {
            Location location = SystemUtils.getLastLocation(this.context);
            if (location != null) {
                if (!this.requestParameters.containsKey(Parameters.LAT) && !this.requestParameters.containsKey(Parameters.LONG)) {
                    this.requestParameters.put(Parameters.LAT, String.valueOf(location.getLatitude()));
                    this.requestParameters.put(Parameters.LONG, String.valueOf(location.getLongitude()));
                } else {
                    if (!this.requestParameters.containsKey(Parameters.LAT)) {
                        this.requestParameters.remove(Parameters.LONG);
                    } else if (!this.requestParameters.containsKey(Parameters.LONG)) {
                        this.requestParameters.remove(Parameters.LAT);
                    }
                }
            }
        }
    }

    /**
     *  Android Advertising Id Task Listener is finished when android ad id is fetched.
     */
    @Override
    public void onAndroidAdIdTaskFinished(String result) {
        if (!TextUtils.isEmpty(result)) {
            this.requestParameters.put(Parameters.ANDROID_ADVERTISER_ID, result);
            this.requestParameters.put(Parameters.ANDROID_ADVERTISER_ID_SHA1, Crypto.sha1(result));
            this.requestParameters.put(Parameters.ANDROID_ADVERTISER_ID_MD5, Crypto.md5(result));
        } else {
            this.requestParameters.put(Parameters.NO_USER_ID, "1");
        }
        String url =  createNetworkRequest();
        sendNetworkRequest(url);
    }

    /**
     * This function is used to create network request.
     */
    protected String createNetworkRequest() {
        String url = null;
        if (endpoint != null) {
            switch (endpoint) {
                case NATIVE:
                        url = createNativeRequest();
                    break;
                default:
                    throw new IllegalArgumentException(endpoint.toString());
            }
        }
        return url;
    }

    /**
     * @return url of native request
     */
    protected String createNativeRequest() {
        Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon();
        uriBuilder.appendPath(NATIVE_TYPE_URL);

        for (String key : requestParameters.keySet()) {
            String value = requestParameters.get(key);
            if (value != null) {
                uriBuilder.appendQueryParameter(key, value);
            }
        }
        return uriBuilder.build().toString();
    }

    /**
     * @param url used to send network request
     */
    protected void sendNetworkRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(this.context);
        StringRequest strRequest = new StringRequest(Request.Method.GET, url, this, this);
        queue.add(strRequest);
    }

    /**
     * responseListener callback calls when network request gets response.
     */
    @Override
    public void onResponse(String response) {
        List<PubnativeAdModel> dataModel = parseResponse(response);
        if (dataModel != null) {
            invokeOnPubnativeRequestSuccess(dataModel);
        }
    }

    /**
     * errorListener callback calls when network request fails.
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        String data = new String(error.networkResponse.data);
        int statusCode = error.networkResponse.statusCode;
        Exception exception = prepareExceptionFromErrorJson(data, statusCode);
        invokeOnPubnativeRequestFailure(exception);
    }

    /**
     * @param data is network response data
     * @param statusCode is statusCode from volley response
     * @return exception if status is not ok and if there is error while parsing json
     */
    protected Exception prepareExceptionFromErrorJson(String data, int statusCode) {
        Exception exception = null;
        if (data != null && !TextUtils.isEmpty(data)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                String status = jsonObject.getString("status");
                String errMsg = jsonObject.getString("error_message");
                exception = new Exception(new StringBuilder().append(status).append(" ").append(errMsg).append(String.valueOf(statusCode)).toString());
            } catch (JSONException e) {
                exception = e;
            }
        } else {
            exception = new Exception("Data is null");
        }
        return exception;
    }

    /**
     * @param response parse json response to PubnativeAdModel
     * @return pubNativeModel PubnativeAdModel is returned after parsing json response
     */
    protected List<PubnativeAdModel> parseResponse(String response) {
        List<PubnativeAdModel> ads = null;
        if (response != null && !TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                JSONArray resultsArray = jsonObject.optJSONArray("ads");

                Gson gson = new Gson();
                Type listType = new TypeToken<List<PubnativeAdModel>>() {
                }.getType();
                ads = gson.fromJson(resultsArray.toString(), listType);

            } catch (JSONException e) {
                this.invokeOnPubnativeRequestFailure(e);
            }
        }
        return ads;
    }

    protected void invokeOnPubnativeRequestSuccess(List<PubnativeAdModel> ads) {
        if (this.listener != null) {
            this.listener.onPubnativeRequestSuccess(this, ads);
        }
    }

    protected void invokeOnPubnativeRequestFailure(Exception exception) {
        if (this.listener != null) {
            this.listener.onPubnativeRequestFail(this, exception);
        }
    }
}
