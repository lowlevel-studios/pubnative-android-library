package net.pubnative.library.demo.utils;

/**
 * Created by davidmartin on 25/02/16.
 */
public class Settings
{
    private static final String DEFAULT_APP_TOKEN = "6651a94cad554c30c47427cbaf0b613a967abcca317df325f363ef154a027092";

    private static String mAppToken = null;

    public static String getAppToken() {

        if(mAppToken  == null) {
            mAppToken  = DEFAULT_APP_TOKEN;
        }
        return mAppToken ;
    }

    public static void setAppToken(String appToken) {
        mAppToken = appToken;
    }
}
