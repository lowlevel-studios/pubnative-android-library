package net.pubnative.library;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class Utils {

    /**
     * This function will read the text file from the given <code>path<code/>, and return the string.
     * @param path
     * @param context
     * @return
     */
    public static String stringFromJson(String path, Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
