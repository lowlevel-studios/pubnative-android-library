package net.pubnative.library.network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.Executor;

/**
 * Created by jaiswal.anshuman on 2/2/2016.
 */
public class RequestTask {

    //Request
    private Request mRequest;

    //Request runnable
    private Runnable mExecuteRequestRunnable;

    private Executor mResponsePoster;

    /**
     * @return Request runnable
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
     * Sets request object
     * @param request
     */
    public void setRequest(Request request) {
        mRequest = request;
    }

    public void setResponsePoster(Executor mResponsePoster) {
        this.mResponsePoster = mResponsePoster;
    }

    private void executeRequest() {

        Response response = new Response();
        try {

            HttpURLConnection connection = (HttpURLConnection) mRequest.getUrl().openConnection();
            connection.setConnectTimeout(Request.TIME_OUT);
            connection.setReadTimeout(Request.TIME_OUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();

            int responseCode = connection.getResponseCode();

            if(responseCode == -1) {
                throw new IOException("Could not retrieve response code from HttpUrlConnection.");
            } else {
                response.setResult(connection.getInputStream());
            }
        } catch (IOException e) {

            e.printStackTrace();
            response.setError(e);
        } finally {

            this.mResponsePoster.execute(new ResponseDeliveryRunnable(mRequest, response));
        }
    }

    private class ResponseDeliveryRunnable implements Runnable {

        private final Request mRequest;
        private final Response mResponse;

        public ResponseDeliveryRunnable(Request request, Response response) {
            this.mRequest = request;
            this.mResponse = response;
        }

        @Override
        public void run() {
            if(this.mResponse.isSuccess()) {
                this.mRequest.deliverResponse(this.mResponse.result);
            } else {
                this.mRequest.deliverError(this.mResponse.error);
            }
        }
    }
}
