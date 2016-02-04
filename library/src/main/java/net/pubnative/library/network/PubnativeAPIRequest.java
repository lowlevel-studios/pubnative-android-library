package net.pubnative.library.network;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public class PubnativeAPIRequest {

    private static String TAG = PubnativeAPIRequest.class.getSimpleName();

    //timeout in ms
    public static int TIME_OUT = 10000;

    // request method
    private Method mMethod;

    // request URL
    private String mUrl;

    // listener
    private PubnativeAPIResponse.Listener mListener;


    public PubnativeAPIRequest(Method method, String url, PubnativeAPIResponse.Listener listener) {

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

    public PubnativeAPIResponse.Listener getListener() {
        return mListener;
    }

    public void deliverResponse(String response) {

        if(mListener != null)
            mListener.onResponse(this, response);
    }

    public void deliverError(Exception error) {

        if(mListener != null)
            mListener.onErrorResponse(this, error);
    }

    public static void send(Method method, String URL, PubnativeAPIResponse.Listener listener) {

        PubnativeAPIRequestManager.sendRequest(new PubnativeAPIRequest(method, URL, listener));
    }
}
