package net.pubnative.library.network;

import android.util.Log;

import net.pubnative.library.request.PubnativeRequest;

import java.net.MalformedURLException;
import java.net.URL;

public class PubnativeAPIRequest {

    private static String TAG = PubnativeAPIRequest.class.getSimpleName();

    //timeout in ms
    public static int TIME_OUT = 3000;

    // request method
    private Method mMethod;

    // request URL
    private String mUrl;

    // listener
    private PubnativeRequest.APIRequestListener mListener;


    public PubnativeAPIRequest(Method method, String url, PubnativeRequest.APIRequestListener listener) {

        mMethod = method;
        mUrl = url;
        mListener = listener;
    }

    public enum Method {
        GET
    }

    public URL getUrl() {

        try {

            return new URL(mUrl);
        } catch (MalformedURLException e) {

            Log.e(TAG, e.getMessage());
            return null;
        }

    }

    public PubnativeRequest.APIRequestListener getListener() {
        return mListener;
    }

    public void deliverResponse(String response) {

        if(mListener != null) {
            mListener.invokeOnResponse(response);
        }
    }

    public void deliverError(Exception error) {

        if(mListener != null) {
            mListener.invokeOnErrorResponse(error);
        }
    }

    public static void send(Method method, String URL, PubnativeRequest.APIRequestListener listener) {

        PubnativeAPIRequestManager.sendRequest(new PubnativeAPIRequest(method, URL, listener));
    }
}
