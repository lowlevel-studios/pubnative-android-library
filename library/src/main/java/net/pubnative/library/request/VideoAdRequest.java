package net.pubnative.library.request;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.Gson;

import net.pubnative.library.model.APIV3Response;
import net.pubnative.library.model.APIV3VideoAd;
import net.pubnative.library.task.AsyncHttpTask;

import java.util.List;

/**
 * Created by davidmartin on 30/11/15.
 */
public class VideoAdRequest extends AdRequest {

    public VideoAdRequestListener videoListener;

    public APIV3Response response;
    public int parsingAds = 0;

    /**
     * Creates a new ad request object
     *
     * @param context valid Context object
     */
    public VideoAdRequest(Context context) {

        super(context);
    }

    public void start(VideoAdRequestListener  listener) {

        this.parsingAds = 0;
        this.videoListener = listener;
        super.start(Endpoint.VIDEO, this.videoListener);
    }

    @Override
    protected String getBaseURL(){
        return "http://api.pubnative.net/api/partner/v3/promotions";
    }

    @Override
    public String toString() {

        Uri.Builder uriBuilder = Uri.parse(this.getBaseURL()).buildUpon();
        uriBuilder.appendPath(this.VIDEO_ENDPOINT_URL);

        for (String key : requestParameters.keySet()) {
            String value = requestParameters.get(key);
            if (value != null) {
                uriBuilder.appendQueryParameter(key, value);
            }
        }
        return uriBuilder.build().toString();
    }

    @Override
    public void onAsyncHttpTaskFinished(AsyncHttpTask task, String result) {

        if (!TextUtils.isEmpty(result)) {

            try {

                APIV3Response response = new Gson().fromJson(result, APIV3Response.class);

                if(response.status.equals(APIV3Response.Status.OK)){

                    invokeOnVideoAdRequestFinished(response.ads);

                } else {

                    this.invokeOnAdRequestFailed(new Exception(response.error_message));
                }

            } catch (Exception e) {

                this.invokeOnAdRequestFailed(e);
            }

        } else {

            this.invokeOnAdRequestFailed(new Exception("Pubnative - Error: empty response"));
        }
    }

    protected void invokeOnVideoAdRequestFinished(List<APIV3VideoAd> ads) {

        if (this.videoListener != null) {
            this.videoListener.onVideoAdRequestFinished(this, ads);
        }
    }
}
