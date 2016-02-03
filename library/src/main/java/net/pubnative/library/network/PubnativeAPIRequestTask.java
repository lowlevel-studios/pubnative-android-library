package net.pubnative.library.network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.Executor;

public class PubnativeAPIRequestTask {

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

            if(responseCode == -1) {
                throw new IOException("Could not retrieve response code from HttpUrlConnection.");
            } else {
                pubnativeAPIResponse.setResult(connection.getInputStream());
            }
        } catch (IOException e) {

            e.printStackTrace();
            pubnativeAPIResponse.setError(e);
        } finally {

            this.mResponsePoster.execute(new ResponseDeliveryRunnable(mPubnativeAPIRequest, pubnativeAPIResponse));
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
                this.mPubnativeAPIRequest.deliverResponse(this.mPubnativeAPIResponse.result);
            } else {
                this.mPubnativeAPIRequest.deliverError(this.mPubnativeAPIResponse.error);
            }
        }
    }
}
