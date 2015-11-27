package net.pubnative.library.demo.request;


import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.pubnative.library.demo.model.PubNativeAdModel;
import net.pubnative.library.demo.utilities.UtilityFunction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;


/**
 * For every Ad request create new object of this class
 */
public class PubNativeRequest {
    String BASE_URL = "http://api.pubnative.net/api/partner/v2/promotions";
    String NATIVE_TYPE_URL = "native";
    String VIDEO_TYPE_URL = "video";

    private Context context = null;
    private Type type = null;
    private HashMap<String, String> requestParameters;
    private PubNativeRequestListener listener = null;


    /**
     * These are the various types of adds pubnative support
     */
    public enum Type {
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
        void onRequestSuccess(PubNativeRequest request, PubNativeAdModel ads);

        /**
         * Invoked when PubNativeRequest request fails
         *
         * @param request Request object used for making the request
         * @param ex      Exception that caused the failure
         */
        void onRequestFailed(PubNativeRequest request, PubNativeException ex);
    }

    public class PubNativeException extends Exception{
        String errMsg = null;
        String status = null;
        int statusCode;

        public PubNativeException(){

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
            requestParameters = new HashMap<>();
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
    public void start(Type type, PubNativeRequestListener listener){
        this.listener = listener;
        this.type = type;
        if(this.listener != null){
            setOptionalParameters();
            String url = this.toString();
            Log.v("path", url);
            sendRequestToVolley(url);
        }
    }


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
//        if (!requestParameters.containsKey(Parameters.NO_USER_ID)) {
//            requestParameters.put(Parameters.NO_USER_ID, "1");
//        }
        Location location = UtilityFunction.getLastLocation(this.context);
        if (location != null) {
            if (!requestParameters.containsKey(PubNativeRequest.Parameters.LAT)) {
                requestParameters.put(Parameters.LAT, String.valueOf(location.getLatitude()));
            }
            if (!requestParameters.containsKey(PubNativeRequest.Parameters.LONG)) {
                requestParameters.put(Parameters.LONG, String.valueOf(location.getLongitude()));
            }
        }
    }


    @Override
    public String toString() {
        Uri.Builder uriBuilder = Uri.parse(this.BASE_URL).buildUpon();
        switch (type) {
            case NATIVE:
                uriBuilder.appendPath(this.NATIVE_TYPE_URL);
                break;
            case VIDEO:
                uriBuilder.appendPath(this.NATIVE_TYPE_URL);
                uriBuilder.appendPath(this.VIDEO_TYPE_URL);
                break;
            default:
                throw new IllegalArgumentException(type.toString());
        }
        for (String key : requestParameters.keySet()) {
            String value = requestParameters.get(key);
            if (value != null) {
                uriBuilder.appendQueryParameter(key, value);
            }
        }
        return uriBuilder.build().toString();
    }


    private void sendRequestToVolley(String url) {
        RequestQueue queue = Volley.newRequestQueue(this.context);
        StringRequest strRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               Log.v("response", response);
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                   // JSONObject json =  jsonObject.optJSONObject("status");
//                    String status = jsonObject.getString("status");
//                    if()
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                prepareExceptionFromErrorJson(error);
            }
        });
        queue.add(strRequest);
    }

    private void prepareExceptionFromErrorJson(VolleyError error) {
        if(error != null){
            String data = new String(error.networkResponse.data);
            int statusCode = error.networkResponse.statusCode;
            PubNativeException exception = new PubNativeException();
            try {
                JSONObject jsonObject = new JSONObject(data);
                String status = jsonObject.getString("status");
                String errMsg = jsonObject.getString("error_message");
                exception.status = status;
                exception.errMsg = errMsg;
                exception.statusCode = statusCode;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
