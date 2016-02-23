package net.pubnative.library.network;

import android.util.Log;

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

        final PubnativeAPIResponse pubnativeAPIResponse = new PubnativeAPIResponse();
        try {

            HttpURLConnection connection = (HttpURLConnection) mPubnativeAPIRequest.getUrl().openConnection();
            connection.setConnectTimeout(PubnativeAPIRequest.TIME_OUT);
            connection.setReadTimeout(PubnativeAPIRequest.TIME_OUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();

            int responseCode = connection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK) {
                pubnativeAPIResponse.setResult(connection.getInputStream());
            } else {
                throw new Exception("Server error: " + responseCode);
            }
        } catch (Exception e) {

            Log.e(TAG, e.getMessage());
            pubnativeAPIResponse.setError(e);
        } finally {

            mResponsePoster.execute(new Runnable() {

                @Override
                public void run() {
                    processResponse(pubnativeAPIResponse);
                }
            });
        }
    }

    private void processResponse(PubnativeAPIResponse pubnativeAPIResponse) {
        if(pubnativeAPIResponse.isSuccess()) {
            mPubnativeAPIRequest.deliverResponse(pubnativeAPIResponse.getResult());
        } else {
            mPubnativeAPIRequest.deliverError(pubnativeAPIResponse.getError());
        }
        PubnativeAPIRequestManager.getInstance().recycleTask(PubnativeAPIRequestTask.this);
    }
}
