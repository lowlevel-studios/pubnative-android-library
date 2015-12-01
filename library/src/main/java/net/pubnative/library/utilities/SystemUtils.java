package net.pubnative.library.utilities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

/**
 * Created by daffodiliphone on 27/11/15.
 */
public class SystemUtils {

    public static String getPackageName(Context context) {
        PackageInfo pInfo = getPackageInfo(context);
        return (pInfo != null) ? pInfo.packageName : "";
    }

    private static PackageInfo getPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.v("E:","Error in getting package info");
            return null;
        }
    }

    /**
     * @param context Context object
     * @return true if location permission granted else false
     */
    public static boolean isLocationPermissionGranted(Context context){
        if(context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;

    }


    /**
     * Tells if the device running this app is a tablet or not.
     * @param context Context object
     * @return true if the device is a tablet, else false
     */
    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    /**
     * Gets you the last known location of the device.
     * @param context Context object
     * @return Location object if last known location if available, else null
     */
    public static Location getLastLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location loc = null;
        for (String prov : lm.getProviders(true)) {
            loc = lm.getLastKnownLocation(prov);
            if (loc != null) {
                break;
            }
        }
        return loc;
    }


    /**
     * Gives you the android advertising id.
     * @param context  Context object
     * @param listener Listener to get callback when android ad id is fetched.
     */
    public static void getAndroidAdID(Context context, AndroidAdIDTask.AndroidAdIDTaskListener listener) {
        new AndroidAdIDTask().setListener(listener).execute(context);
    }

    public static class AndroidAdIDTask extends AsyncTask<Context, Void, String> {
        private AndroidAdIDTaskListener listener;

        public interface AndroidAdIDTaskListener {
            void onAndroidAdvertisingIDTaskFinished(String result);
        }

        public AndroidAdIDTask setListener(AndroidAdIDTaskListener listener) {
            this.listener = listener;
            return this;
        }

        @Override
        protected String doInBackground(Context... contexts) {
            String result = null;
            Context context = contexts[0];
            if (context != null) {
                AdvertisingIdClient.Info adInfo = null;
                try {
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
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
                this.listener.onAndroidAdvertisingIDTaskFinished(result);
            }
        }
    }

}
