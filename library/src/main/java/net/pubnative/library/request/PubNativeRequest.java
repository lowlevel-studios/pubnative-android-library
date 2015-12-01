package net.pubnative.library.request;


import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.pubnative.library.model.PubNativeAdModel;
import net.pubnative.library.utilities.UtilityFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


/**
 * For every Ad request create new object of this class
 */
public class PubNativeRequest {
    private String BASE_URL = "http://api.pubnative.net/api/partner/v2/promotions";
    private String NATIVE_TYPE_URL = "native";
    private String VIDEO_TYPE_URL = "video";

    private Context context;
    private AdType type;
    private HashMap<String, String> requestParameters;
    private PubNativeRequestListener listener;
    private boolean isLogging;


    /**
     * get isLogging
     */
    public boolean isLogging() {
        return isLogging;
    }

    /**
     * set isLogging true to use Log
     */
    public void setIsLogging(boolean isLogging) {
        this.isLogging = isLogging;
    }


    /**
     * These are the various types of adds pubnative support
     */
    public enum AdType {
        NATIVE, VIDEO
    }

    public PubNativeRequest(Context context){
        this.context = context;
    }

    /**
     * These are the various types of parameters
     */
    public interface Parameters{
        String APP_TOKEN = "app_token";
        String BUNDLE_ID = "bundle_id";
        String APPLE_IDFA = "apple_idfa";
        String APPLE_IDFA_SHA1 = "apple_idfa_sha1";
        String APPLE_IDFA_MD5 = "apple_idfa_md5";
        String ANDROID_ADVERTISER_ID = "android_advertiser_id";
        String ANDROID_ADVERTISER_ID_SHA1 = "android_advertiser_id_sha1";
        String ANDROID_ADVERTISER_ID_MD5 = "android_advertiser_id_md5";
        String ICON_SIZE = "icon_size";
        String BANNER_SIZE = "banner_size";
        String OS = "os";
        String DEVICE_MODEL = "device_model";
        String OS_VERSION = "os_version";
        String NO_USER_ID = "no_user_id";
        String PARTNER = "partner";
        String LOCALE = "locale";
        String PORTRAIT_BANNER_SIZE = "portrait_banner_size";
        String DEVICE_RESOLUTION = "device_resolution";
        String DEVICE_TYPE = "device_type";
        String AD_COUNT = "ad_count";
        String ZONE_ID = "zone_id";
        String LAT = "lat";
        String LONG = "long";
        String GENDER = "gender";
        String AGE = "age";
        String KEYWORDS = "keywords";
    }

    public interface PubNativeRequestListener{
        /**
         * Invoked when PubNativeRequest request is success
         *
         * @param request Request object used for making the request
         * @param ads     List of ads received
         */
        void onRequestSuccess(PubNativeRequest request, ArrayList<PubNativeAdModel> ads);

        /**
         * Invoked when PubNativeRequest request fails
         *
         * @param request Request object used for making the request
         * @param ex      Exception that caused the failure
         */
        void onRequestFailed(PubNativeRequest request, PubNativeException ex);
    }

    /**
     *  class used to handle exception at the time of failure
     */
    public static class PubNativeException extends Exception{
        private String mErrMsg;
        private String mStatus;
        private int mStatusCode;

        public PubNativeException(){
        }

        public void setErrMsg(String errMsg){
            mErrMsg = errMsg;
        }

        public void setStatus(String status){
            mStatus = status;
        }

        public void setStatusCode(int statusCode){
            mStatusCode = statusCode;
        }

        public String getErrMsg(){
            return mErrMsg;
        }

        public String getStatus(){
            return mStatus;
        }

        public int getStatusCode(){
            return mStatusCode;
        }

    }

    /**
     * Sets parameters required to make the pub native request
     *
     * @param key key name of parameter
     * @param value actual value of parameter
     */
    public void setParameter(String key, String value){
        if(requestParameters == null){
            requestParameters = new HashMap<String, String>();
        }
        if(value == null){
            requestParameters.remove(key);
        }else {
            requestParameters.put(key, value);
        }
    }


    /**
     * Starts pub native request, This function make the ad request to the pubnative server. It makes asynchronous network request in the background.
     *
     * @param type type of ad (ex: NATIVE)
     * @param listener valid listener to track ad request callbacks.
     */
    public void start(AdType type, @NonNull PubNativeRequestListener listener){
        this.listener = listener;
        this.type = type;
        if(this.listener != null){
            setOptionalParameters();
            if (!requestParameters.containsKey(Parameters.ANDROID_ADVERTISER_ID))
                setAdvertisingId();
            else
                createNetworkRequest();
        }

    }


    /**
     *  setting optional parameters to request parameters
     */
    private void setOptionalParameters() {
        if (!requestParameters.containsKey(Parameters.BUNDLE_ID)) {
            this.requestParameters.put(Parameters.BUNDLE_ID, UtilityFunction.getPackageName(this.context));
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
            requestParameters.put(Parameters.DEVICE_TYPE, UtilityFunction.isTablet(this.context) ? "tablet" : "phone");
        }
        if (!requestParameters.containsKey(Parameters.LOCALE)) {
            requestParameters.put(Parameters.LOCALE, Locale.getDefault().getLanguage());
        }

        if(!requestParameters.containsKey(Parameters.LAT) || !requestParameters.containsKey(Parameters.LONG)){
            if(UtilityFunction.checkLocationPermissionGranted(this.context)){
                Location location = UtilityFunction.getLastLocation(this.context);
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
        UtilityFunction.getAndroidAdvertisingID(this.context, advertisingListener);
    }

    /**
     *  Android Advertising Id Task Listener is finished when android ad id is fetched.
     */
    private UtilityFunction.AndroidAdvertisingIDTask.AndroidAdvertisingIDTaskListener advertisingListener = new UtilityFunction.AndroidAdvertisingIDTask.AndroidAdvertisingIDTaskListener() {
        @Override
        public void onAndroidAdvertisingIDTaskFinished(String result) {
            if (!TextUtils.isEmpty(result)) {
                requestParameters.put(Parameters.ANDROID_ADVERTISER_ID, result);
                requestParameters.put(Parameters.ANDROID_ADVERTISER_ID_SHA1, UtilityFunction.sha1(result));
                requestParameters.put(Parameters.ANDROID_ADVERTISER_ID_MD5, UtilityFunction.md5(result));
            } else {
                requestParameters.put(Parameters.NO_USER_ID, "1");
            }

            createNetworkRequest();
        }
    };


    /**
     * This function is used to create network request.
     */
    private void createNetworkRequest() {
        String url = null;
        if(type != null){
            switch (type) {
                case NATIVE:
                        url = createNativeRequest();
                    break;
                case VIDEO:
                        url = createVideoRequest();
                    break;
                default:
                    throw new IllegalArgumentException(type.toString());
            }
//            if(isLogging()){
//                Log.v("path", url);
//            }

            sendNetworkRequest(url);
        }
    }

    /**
     * @return url of video request
     */
    private String createVideoRequest() {
        Uri.Builder uriBuilder = Uri.parse(this.BASE_URL).buildUpon();
        uriBuilder.appendPath(this.NATIVE_TYPE_URL);
        uriBuilder.appendPath(this.VIDEO_TYPE_URL);

        for (String key : requestParameters.keySet()) {
            String value = requestParameters.get(key);
            if (value != null) {
                uriBuilder.appendQueryParameter(key, value);
            }
        }
        return uriBuilder.build().toString();
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
        StringRequest strRequest = new StringRequest(Request.Method.GET, url, responseListener, errorListener);
        queue.add(strRequest);
    }

    /**
     * responseListener callback calls when network request gets response.
     */
    private Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            ArrayList<PubNativeAdModel> dataModel = parseJsonToAdModel(response);
            sendSuccessEvent(dataModel);
        }
    };

    /**
     * errorListener callback calls when network request fails.
     */
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            String data = new String(error.networkResponse.data);
            int statusCode = error.networkResponse.statusCode;
            PubNativeException exception = new PubNativeException();
            exception.setStatusCode(statusCode);
            exception = prepareExceptionFromErrorJson(data, exception);
            sendFailEvent(exception);
        }
    };

    /**
     * @param data is network response data
     * @param exception is pub native exception
     * @return exception if status is not ok and if there is error while parsing json
     */
    private PubNativeException prepareExceptionFromErrorJson(String data, PubNativeException exception) {
        if(data != null){
            try {
                JSONObject jsonObject = new JSONObject(data);
                String status = jsonObject.getString("status");
                String errMsg = jsonObject.getString("error_message");
                exception.setStatus(status);
                exception.setErrMsg(errMsg);
            } catch (JSONException e) {
                e.printStackTrace();
                exception.setErrMsg("Error while parsing Json");
                exception.setStatus("error");
                exception.setStatusCode(101);
            }
        }
        return exception;
    }

    /**
     * @param response parse json response to PubNativeAdModel
     * @return pubNativeModel PubNativeAdModel is returned after parsing json response
     */
    private ArrayList<PubNativeAdModel> parseJsonToAdModel(String response) {
        ArrayList<PubNativeAdModel> ads = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");
            JSONArray resultsArray = jsonObject.optJSONArray("ads");

            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<PubNativeAdModel>>() {
            }.getType();
            ads = gson.fromJson(resultsArray.toString(), listType);

        } catch (JSONException e) {
            e.printStackTrace();
            PubNativeException exception = new PubNativeException();
            exception.setErrMsg("Error while parsing Json");
            exception.setStatus("error");
            exception.setStatusCode(101);
            sendFailEvent(exception);
        }

        return ads;
    }


    private void sendSuccessEvent(ArrayList<PubNativeAdModel> ads) {
        if(this.listener != null){
            this.listener.onRequestSuccess(this, ads);
        }
    }


    private void sendFailEvent(PubNativeException exception) {
        if(this.listener != null){
            this.listener.onRequestFailed(this, exception);
        }
    }


}
