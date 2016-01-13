package net.pubnative.player.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

/**
 * Created by davidmartin on 11/01/16.
 */
public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG = BitmapDownloaderTask.class.getName();
    public interface Listener {

        void onBitmapDownloaderFinished(Bitmap bitmap);
    }

    private Listener listener;

    public BitmapDownloaderTask setListener(Listener listener){

        this.listener = listener;
        return this;
    }


    @Override
    protected Bitmap doInBackground(String... params) {

        Bitmap result = null;

        if(params.length > 0) {

            try {

                String urlString = params[0];
                URL url = new URL(urlString);
                result = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            } catch (Exception e) {

                Log.e(TAG, "error: " + e);
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(Bitmap result) {

        if(listener != null) {

            listener.onBitmapDownloaderFinished(result);
        }
    }
}
