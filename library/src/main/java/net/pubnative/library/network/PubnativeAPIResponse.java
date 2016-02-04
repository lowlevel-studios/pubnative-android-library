package net.pubnative.library.network;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PubnativeAPIResponse {

    private static String TAG = PubnativeAPIResponse.class.getSimpleName();

    public String mResult;
    public Exception mError;

    public void setResult(String result) {
        mResult = result;
    }

    public void setResult(InputStream is) {

        BufferedInputStream inputStream = new BufferedInputStream(is);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {

            int result = inputStream.read();
            while(result != -1) {
                byte byteResult = (byte)result;
                outputStream.write(byteResult);
                result = inputStream.read();
            }
            mResult = outputStream.toString();

        } catch (IOException e) {

            Log.e(TAG, e.getMessage());
            mError = e;
        }
    }

    public void setError(Exception error) {
        mError = error;
    }

    public interface Listener {
        void onResponse(PubnativeAPIRequest request, String response);

        void onErrorResponse(PubnativeAPIRequest request, Exception error);
    }

    public boolean isSuccess() {
        return mError == null;
    }

}
