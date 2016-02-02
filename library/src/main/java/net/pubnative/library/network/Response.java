package net.pubnative.library.network;

/**
 * Created by jaiswal.anshuman on 2/2/2016.
 */
public class Response {

    public interface ErrorListener {

        void onErrorResponse(Exception error);

    }

    public interface Listener<T> {

        void onResponse(T response);

    }

}
