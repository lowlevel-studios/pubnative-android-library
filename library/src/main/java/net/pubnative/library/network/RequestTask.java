package net.pubnative.library.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by jaiswal.anshuman on 2/2/2016.
 */
public class RequestTask {

    //Request
    private Request mRequest;

    //Request runnable
    private Runnable mExecuteRequestRunnable;

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

    private void executeRequest() {
        InputStream is = null;

        try {

            HttpURLConnection connection = (HttpURLConnection) mRequest.getUrl().openConnection();
            connection.setConnectTimeout(Request.TIME_OUT);
            connection.setReadTimeout(Request.TIME_OUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();

            int responseCode = connection.getResponseCode();
            is = connection.getInputStream();

            if(responseCode == -1) {
                throw new IOException("Could not retrieve response code from HttpUrlConnection.");
            } else {

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
