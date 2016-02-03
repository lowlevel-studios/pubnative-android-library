package net.pubnative.library.network;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jaiswal.anshuman on 2/2/2016.
 */
public class Request {

    //timeout in ms
    public static int TIME_OUT = 10000;

    // request method
    private int mMethod;

    // request URL
    private String mUrl;

    // success listener
    private Response.Listener mListener;

    // error listener
    private Response.ErrorListener mErrorListener;

    public Request(int method, String url, Response.Listener listener, Response.ErrorListener errorListener) {

        mMethod = method;
        mUrl = url;
        mListener = listener;
        mErrorListener = errorListener;
    }

    public interface Method {
        int GET = 0;
    }

    public int getMethod() {
        return mMethod;
    }

    public URL getUrl() {

        try {

            return new URL(mUrl);
        } catch (MalformedURLException e) {

            e.printStackTrace();
            return null;
        }

    }

    public Response.Listener getListener() {
        return mListener;
    }

    public Response.ErrorListener getErrorListener() {

        return mErrorListener;
    }

    public void deliverResponse(String response) {

        mListener.onResponse(response);
    }

    public void deliverError(Exception error) {

        if(mErrorListener != null) {

            mErrorListener.onErrorResponse(error);
        }
    }
}
