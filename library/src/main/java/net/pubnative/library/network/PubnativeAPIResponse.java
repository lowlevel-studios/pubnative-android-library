package net.pubnative.library.network;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PubnativeAPIResponse {

    private static String TAG = PubnativeAPIResponse.class.getSimpleName();

    private String mResult;
    private Exception mError;

    public String getResult() {
        return mResult;
    }

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
            setResult(outputStream.toString());

        } catch (IOException e) {

            Log.e(TAG, e.getMessage());
            mError = e;
        }
    }

    public Exception getError() {
        return mError;
    }

    public void setError(Exception error) {
        mError = error;
    }

    public interface Listener {
        void onResponse(String response);

        void onErrorResponse(Exception error);
    }

    public boolean isSuccess() {
        return mError == null;
    }

}
