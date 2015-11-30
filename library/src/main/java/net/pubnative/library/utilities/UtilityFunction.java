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

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Created by daffodiliphone on 27/11/15.
 */
public class UtilityFunction {

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
    public static void getAndroidAdvertisingID(Context context, AndroidAdvertisingIDTask.AndroidAdvertisingIDTaskListener listener) {
        new AndroidAdvertisingIDTask().setListener(listener).execute(context);
    }

    public static class AndroidAdvertisingIDTask extends AsyncTask<Context, Void, String> {
        private AndroidAdvertisingIDTaskListener listener;

        public interface AndroidAdvertisingIDTaskListener {
            void onAndroidAdvertisingIDTaskFinished(String result);
        }

        public AndroidAdvertisingIDTask setListener(AndroidAdvertisingIDTaskListener listener) {
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


    /**
     * Encrypts the given input string using SHA-1 algorithm
     *
     * @param input String to be encrypted
     * @return Encrypted string
     */
    public static String sha1(String input) {
        String result = "";
        StringBuilder stringBuilder = new StringBuilder();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = input.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();
            for (final byte b : bytes) {
                stringBuilder.append(String.format("%02X", b));
            }
            result = stringBuilder.toString().toLowerCase(Locale.US);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Encrypts the given input string using md5 algorithm
     *
     * @param input String to be encrypted
     * @return Encrypted string
     */
    public static String md5(String input) {
        String result = "";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            result = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

}
