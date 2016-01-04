package net.pubnative.library.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

public class AndroidAdvertisingIDTask extends AsyncTask<Context, Void, String> {

    private Listener listener;

    public interface Listener {

        void onAndroidAdIdTaskFinished(String result);
    }

    public AndroidAdvertisingIDTask setListener(Listener listener) {

        this.listener = listener;
        return this;
    }

    @Override
    protected String doInBackground(Context... contexts) {

        String  result  = null;
        Context context = contexts[0];

        if (context != null && this.listener != null) {

            try {

                AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);

                if (adInfo != null) {

                    result = adInfo.getId();
                }

            } catch (Exception e) {

                Log.e("Pubnative", "Error retrieving androidAdvertisingID: " + e.toString());
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {

        if (this.listener != null) {

            this.listener.onAndroidAdIdTaskFinished(result);
        }
    }
}