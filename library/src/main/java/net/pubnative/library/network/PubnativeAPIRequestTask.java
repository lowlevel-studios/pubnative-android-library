package net.pubnative.library.network;

import android.util.Log;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.Executor;

public class PubnativeAPIRequestTask {

    private static String TAG = PubnativeAPIRequestTask.class.getSimpleName();

    //PubnativeAPIRequest
    private PubnativeAPIRequest mPubnativeAPIRequest;

    //PubnativeAPIRequest runnable
    private Runnable mExecuteRequestRunnable;

    private Executor mResponsePoster;

    /**
     * @return PubnativeAPIRequest runnable
     */
    public Runnable getExecuteRequestRunnable() {

        if(mExecuteRequestRunnable == null) {

            mExecuteRequestRunnable = new Runnable() {

                @Override
                public void run() {
                    executeRequest();
                }
            };
        }
        return mExecuteRequestRunnable;
    }

    /**
     * Sets pubnativeAPIRequest object
     * @param pubnativeAPIRequest Request object
     */
    public void setRequest(PubnativeAPIRequest pubnativeAPIRequest) {
        mPubnativeAPIRequest = pubnativeAPIRequest;
    }

    public void setResponsePoster(Executor mResponsePoster) {
        this.mResponsePoster = mResponsePoster;
    }

    private void executeRequest() {

        PubnativeAPIResponse pubnativeAPIResponse = new PubnativeAPIResponse();
        try {

            HttpURLConnection connection = (HttpURLConnection) mPubnativeAPIRequest.getUrl().openConnection();
            connection.setConnectTimeout(PubnativeAPIRequest.TIME_OUT);
            connection.setReadTimeout(PubnativeAPIRequest.TIME_OUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();

            int responseCode = connection.getResponseCode();

            if(responseCode == HttpStatus.SC_OK) {
                pubnativeAPIResponse.setResult(connection.getInputStream());
            } else {
                throw new IOException("Could not retrieve response code from HttpUrlConnection.");
            }
        } catch (IOException e) {

            Log.e(TAG, e.getMessage());
            pubnativeAPIResponse.setError(e);
        } finally {

            this.mResponsePoster.execute(new ResponseDeliveryRunnable(mPubnativeAPIRequest, pubnativeAPIResponse));
            PubnativeAPIRequestManager.getInstance().recycleTask(this);
        }
    }

    private class ResponseDeliveryRunnable implements Runnable {

        private final PubnativeAPIRequest mPubnativeAPIRequest;
        private final PubnativeAPIResponse mPubnativeAPIResponse;

        public ResponseDeliveryRunnable(PubnativeAPIRequest pubnativeAPIRequest, PubnativeAPIResponse pubnativeAPIResponse) {
            this.mPubnativeAPIRequest = pubnativeAPIRequest;
            this.mPubnativeAPIResponse = pubnativeAPIResponse;
        }

        @Override
        public void run() {
            if(this.mPubnativeAPIResponse.isSuccess()) {
                this.mPubnativeAPIRequest.deliverResponse(this.mPubnativeAPIResponse.mResult);
            } else {
                this.mPubnativeAPIRequest.deliverError(this.mPubnativeAPIResponse.mError);
            }
        }
    }
}
