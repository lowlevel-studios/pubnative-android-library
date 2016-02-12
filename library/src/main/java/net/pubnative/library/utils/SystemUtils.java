package net.pubnative.library.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class SystemUtils {

    /**
     * @param context Context object
     *
     * @return package name
     */
    public static String getPackageName(Context context) {

        PackageInfo pInfo = getPackageInfo(context);
        return (pInfo != null) ? pInfo.packageName : "";
    }

    /**
     * @param context Context object
     *
     * @return package info
     */
    private static PackageInfo getPackageInfo(Context context) {

        try {

            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {

            Log.v("E:", "Error in getting package info");
            return null;
        }
    }

    /**
     * @param context Context object
     *
     * @return true if location permission granted else false
     */
    public static boolean isLocationPermissionGranted(Context context) {

        boolean result = false;

        if (context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED) {

            result = true;
        }

        return result;
    }

    /**
     * Tells if the device running this app is a tablet or not.
     *
     * @param context Context object
     *
     * @return true if the device is a tablet, else false
     */
    public static boolean isTablet(Context context) {

        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large  = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    /**
     * Gets you the last known location of the device.
     *
     * @param context Context object
     *
     * @return Location object if last known location if available, else null
     */
    public static Location getLastLocation(Context context) {

        LocationManager lm  = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location        loc = null;

        for (String prov : lm.getProviders(true)) {

            loc = lm.getLastKnownLocation(prov);

            if (loc != null) {

                break;
            }
        }

        return loc;
    }

    /**
     * Check the visibility of view on screen
     * @param view ciew to be checked
     * @param percentage how much percentage view is visible
     * @return true if view is visible passed <code>percentage</code> on the screen false otherwise
     */
    public static boolean isVisibleOnScreen(View view, float percentage) {

        int location[] = new int[2];

        view.getLocationInWindow(location);

        WindowManager windowManager = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        Rect screenRect = new Rect(0, 0, screenWidth, screenHeight);

        int topLeftX = location[0];
        int topLeftY = location[1];

        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();

        int bottomRightX = topLeftX + viewWidth;
        int bottomRightY = topLeftY + viewHeight;

        // Create Rect with view's current position
        Rect viewRect = new Rect(topLeftX, topLeftY, bottomRightX, bottomRightY);
        // This will store the rect which come after clipping the viewRect with the screen
        Rect intersectionRect = new Rect();

        // CASE 1: When view is Invisible or has no dimensions.
        if (view.getVisibility() == View.VISIBLE && !viewRect.isEmpty()) {

            // CASE 2: When view is completely inside the screen.
            if (screenRect.contains(viewRect)) {

                return true;

                // CASE 3: When the view is clipped by screen, intersectionRect will hold the rect which is formed with edge of screen and visible portion of view
            } else if (intersectionRect.setIntersect(screenRect, viewRect)) {

                // Find the area of that part of view which is visible on the screen
                double visibleArea = intersectionRect.height() * intersectionRect.width();
                double totalAreaOfView = viewRect.height() * viewRect.width();

                double percentageVisible = visibleArea / totalAreaOfView;

                // now the visible area must be 50% or greater then total area of view.
                if (percentageVisible >= percentage) {

                    return true;
                }
            }
        }

        // CASE 4: When the view is outside of screen bounds such that no part of it is visible then the default value(false) of result will return.
        // We don't need to put explicit condition for this case.
        return false;
    }
}
