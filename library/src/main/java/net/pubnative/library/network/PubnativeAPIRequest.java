package net.pubnative.library.network;

import java.net.MalformedURLException;
import java.net.URL;

public class PubnativeAPIRequest {

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

            e.printStackTrace();
            return null;
        }

    }

    public PubnativeAPIResponse.Listener getListener() {
        return mListener;
    }

    public void deliverResponse(String response) {

        mListener.onResponse(response);
    }

    public void deliverError(Exception error) {

        mListener.onErrorResponse(error);
    }

    public static void send(Method method, String URL, PubnativeAPIResponse.Listener listener) {

        PubnativeAPIRequestManager.sendRequest(new PubnativeAPIRequest(method, URL, listener));
    }
}
