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

import net.pubnative.library.model.PubnativeAdModel;
import net.pubnative.library.utilities.Crypto;
import net.pubnative.library.utilities.SystemUtils;

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
public class PubnativeRequest implements SystemUtils.AndroidAdIDTask.AndroidAdIDTaskListener, Response.Listener<String>, Response.ErrorListener{
    protected static final String BASE_URL        =  "http://api.pubnative.net/api/partner/v2/promotions";
    protected static final String NATIVE_TYPE_URL =  "native";
    protected static final String VIDEO_TYPE_URL  =  "video";

    protected Context context;
    protected EndPoint endpoint;
    protected Map<String, String> requestParameters;
    protected Listener listener;

    /**
     * These are the various types of adds pubnative support
     */
    public enum EndPoint {
        NATIVE
    }

    public PubnativeRequest(){
    }

    /**
     * These are the various types of parameters
     */
    public interface Parameters{
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

    public interface Listener{
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
    public void setParameter(String key, String value){
        if (requestParameters == null) {
            requestParameters = new HashMap<String, String>();
        }
        if (value == null && !value.isEmpty()) {
            requestParameters.remove(key);
        }else {
            requestParameters.put(key, value);
        }
    }


    /**
     * Starts pub native request, This function make the ad request to the pubnative server. It makes asynchronous network request in the background.
     *
     * @param context
     * @param endpoint type of ad (ex: NATIVE)
     * @param listener valid listener to track ad request callbacks.
     */
    public void start(Context context, EndPoint endpoint, @NonNull Listener listener){
        this.context = context;
        this.listener = listener;
        this.endpoint = endpoint;
        if (this.listener != null) {
            setOptionalParameters();
            if (!requestParameters.containsKey(Parameters.ANDROID_ADVERTISER_ID)) {
                setAdvertisingId();
            }
            else {
                createNetworkRequest();
            }
        }
    }


    /**
     *  setting optional parameters to request parameters
     */
    private void setOptionalParameters() {
        if (!requestParameters.containsKey(Parameters.BUNDLE_ID)) {
            this.requestParameters.put(Parameters.BUNDLE_ID, SystemUtils.getPackageName(this.context));
        }

        if (!requestParameters.containsKey(Parameters.OS)) {
            requestParameters.put(Parameters.OS, "android");
        }

        if (!requestParameters.containsKey(Parameters.DEVICE_MODEL)) {
            requestParameters.put(Parameters.DEVICE_MODEL, Build.MODEL);
        }

        if (!requestParameters.containsKey(Parameters.OS_VERSION)) {
            requestParameters.put(Parameters.OS_VERSION, Build.VERSION.RELEASE);
        }

        if (!requestParameters.containsKey(Parameters.DEVICE_RESOLUTION)) {
            DisplayMetrics dm = this.context.getResources().getDisplayMetrics();
            requestParameters.put(Parameters.DEVICE_RESOLUTION, dm.widthPixels + "x" + dm.heightPixels);
        }
        if (!requestParameters.containsKey(Parameters.DEVICE_TYPE)) {
            requestParameters.put(Parameters.DEVICE_TYPE, SystemUtils.isTablet(this.context) ? "tablet" : "phone");
        }
        if (!requestParameters.containsKey(Parameters.LOCALE)) {
            requestParameters.put(Parameters.LOCALE, Locale.getDefault().getLanguage());
        }

        if (!requestParameters.containsKey(Parameters.LAT) || !requestParameters.containsKey(Parameters.LONG)) {
            if (SystemUtils.isLocationPermissionGranted(this.context)) {
                Location location = SystemUtils.getLastLocation(this.context);
                if (location != null) {
                        requestParameters.put(Parameters.LAT, String.valueOf(location.getLatitude()));
                        requestParameters.put(Parameters.LONG, String.valueOf(location.getLongitude()));
                }
            }
        }
    }


    /**
     * This function sets the android advertising id if the user has not given.
     */
    private void setAdvertisingId() {
        SystemUtils.getAndroidAdID(this.context, this);
    }

    /**
     *  Android Advertising Id Task Listener is finished when android ad id is fetched.
     */
    @Override
    public void onAndroidAdvertisingIDTaskFinished(String result) {
        if (!TextUtils.isEmpty(result)) {
            this.requestParameters.put(Parameters.ANDROID_ADVERTISER_ID, result);
            this.requestParameters.put(Parameters.ANDROID_ADVERTISER_ID_SHA1, Crypto.sha1(result));
            this.requestParameters.put(Parameters.ANDROID_ADVERTISER_ID_MD5, Crypto.md5(result));
        } else {
            this.requestParameters.put(Parameters.NO_USER_ID, "1");
        }
        createNetworkRequest();
    }

    /**
     * This function is used to create network request.
     */
    private void createNetworkRequest() {
        String url = null;
        if (endpoint != null) {
            switch (endpoint) {
                case NATIVE:
                        url = createNativeRequest();
                    break;
                default:
                    throw new IllegalArgumentException(endpoint.toString());
            }
            sendNetworkRequest(url);
        }
    }


    /**
     * @return url of native request
     */
    private String createNativeRequest() {
        Uri.Builder uriBuilder = Uri.parse(this.BASE_URL).buildUpon();
        uriBuilder.appendPath(this.NATIVE_TYPE_URL);

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
    private void sendNetworkRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(this.context);
        StringRequest strRequest = new StringRequest(Request.Method.GET, url, this, this);
        queue.add(strRequest);
    }

    /**
     * responseListener callback calls when network request gets response.
     */
    @Override
    public void onResponse(String response) {
        List<PubnativeAdModel> dataModel = parseJsonToAdModel(response);
        sendSuccessEvent(dataModel);
    }

    /**
     * errorListener callback calls when network request fails.
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        String data = new String(error.networkResponse.data);
        int statusCode = error.networkResponse.statusCode;
        Exception exception = prepareExceptionFromErrorJson(data, statusCode);
        sendFailEvent(exception);
    }

    /**
     * @param data is network response data
     * @param statusCode is statusCode from volley response
     * @return exception if status is not ok and if there is error while parsing json
     */
    private Exception prepareExceptionFromErrorJson(String data, int statusCode) {
        Exception exception = null;
        if (data != null) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                String status = jsonObject.getString("status");
                String errMsg = jsonObject.getString("error_message");
                exception = new Exception(new StringBuilder().append(status).append(" ").append(errMsg).append(String.valueOf(statusCode)).toString(), new Throwable());
            } catch (JSONException e) {
                e.printStackTrace();
                exception = e;
            }
        }
        return exception;
    }

    /**
     * @param response parse json response to PubnativeAdModel
     * @return pubNativeModel PubnativeAdModel is returned after parsing json response
     */
    private List<PubnativeAdModel> parseJsonToAdModel(String response) {
        List<PubnativeAdModel> ads = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");
            JSONArray resultsArray = jsonObject.optJSONArray("ads");

            Gson gson = new Gson();
            Type listType = new TypeToken<List<PubnativeAdModel>>() {
            }.getType();
            ads = gson.fromJson(resultsArray.toString(), listType);

        } catch (JSONException e) {
            e.printStackTrace();
            sendFailEvent(e);
        }
        return ads;
    }


    private void sendSuccessEvent(List<PubnativeAdModel> ads) {
        if (this.listener != null) {
            this.listener.onPubnativeRequestSuccess(this, ads);
        }
    }

    private void sendFailEvent(Exception exception) {
        if (this.listener != null) {
            this.listener.onPubnativeRequestFail(this, exception);
        }
    }
}
