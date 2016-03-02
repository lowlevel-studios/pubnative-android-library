// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.library.tracking;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import net.pubnative.library.network.PubnativeAPIRequest;
import net.pubnative.library.tracking.model.ImpressionUrlModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TrackingUrlManager {

    private static final String TAG                  = TrackingUrlManager.class.getSimpleName();
    private static final String SHARED_FILE          = "net.pubnative.library.tracking.TrackingUrlManager";
    private static final String CURRENT_URLS_SET     = "current_urls";
    private static final String PENDING_URLS_SET     = "pending_urls";
    private static final long DISCARD_TIME_THRESHOLD = 1800000; // 30 minutes

    private static boolean  sIsTracking              = false;

    //==============================================================================================
    // PUBLIC
    //==============================================================================================

    /**
     * This method is used to send impression request
     *
     * @param context context
     * @param impressionUrl Ad impression url fro request
     */
    public synchronized static void TrackImpressionUrl(final Context context, final String impressionUrl) {

        Log.v(TAG, "TrackImpressionUrl(Context context, String impressionUrl)");

        if (sIsTracking) {
            // previous request in process, return and store url in pending list
            TrackingUrlManager.putToSharedList(context, PENDING_URLS_SET, impressionUrl);
            return;
        }

        sIsTracking = true;
        TrackingUrlManager.putToSharedList(context, CURRENT_URLS_SET, impressionUrl);

        PubnativeAPIRequest.send(impressionUrl, new PubnativeAPIRequest.Listener() {

            @Override
            public void onPubnativeAPIRequestResponse(String response) {
                Log.v(TAG, "TrackingUrlManager: onPubnativeAPIRequestResponse");
                nextImpressionRequest(context, false);
            }

            @Override
            public void onPubnativeAPIRequestError(Exception error) {
                Log.v(TAG, "TrackingUrlManager: onPubnativeAPIRequestError");
                nextImpressionRequest(context, true);
            }
        });
    }

    //==============================================================================================
    // PRIVATE
    //==============================================================================================

    /**
     * This method returns Trackingurl stored as List in sharedPreference
     *
     * @param context context
     * @param listKey key for preference value
     * @return TrackingUrl TrackingUrl object
     */
    private static List<ImpressionUrlModel> getSharedList(Context context, String listKey) {
        Log.v(TAG, "TrackingUrlManager: getSharedList");

        List<ImpressionUrlModel> result = null;
        SharedPreferences preferences = context.getSharedPreferences(SHARED_FILE, 0);

        String sharedListString = preferences.getString(listKey, null);
        if (sharedListString != null) {
            result = new Gson().fromJson(sharedListString, List.class);
        }

        return result;
    }

    /**
     * This method is used to save and update values in preference
     *
     * @param sharedList Trackingurl object
     * @param context context
     * @param set key for preference value
     */
    private synchronized static void setSharedList(List<ImpressionUrlModel> sharedList, Context context, String set) {
        Log.v(TAG, "TrackingUrlManager: setSharedList");
        String sharedListString = new Gson().toJson(sharedList);

        SharedPreferences.Editor editablePreferences = context.getSharedPreferences(SHARED_FILE, 0).edit();
        editablePreferences.putString(set, sharedListString);
        editablePreferences.apply();
    }

    /**
     * This method used to store url as list in sharedPreference
     *
     * @param context context
     * @param set key for preference value
     * @param value Ad impression url value
     */
    private static void putToSharedList(final Context context, String set, String value) {
        Log.v(TAG, "TrackingUrlManager: putToSharedList");

        //TrackingUrls sharedList = getTrackingUrlList(set, context);
        List<ImpressionUrlModel> list = getSharedList(context, set);//sharedList.getUrlList();

        if(list == null) {
            list = new ArrayList<ImpressionUrlModel>();
        }

        for (ImpressionUrlModel item : list) {
            if (!item.getURL().equalsIgnoreCase(value)) {
                list.add(new ImpressionUrlModel(value));
            }
        }

        //sharedList.setUrlList(list);
        setSharedList(list, context, set);
    }

    /**
     * This method is used to remove url from list and update sharedpreference
     *
     * @param context context
     * @param set key for preference value
     * @param value Ad impression url value
     */
    private static void removeFromSharedList(final Context context, String set, String value) {
        Log.v(TAG, "TrackingUrlManager: removeFromSharedList");

        //TrackingUrls sharedList = getTrackingUrlList(set, context);

        List<ImpressionUrlModel> list = getSharedList(context, set);//sharedList.getUrlList();
        if (list != null && list.size() > 0) {

            Iterator<ImpressionUrlModel> iterator = list.iterator();
            while (iterator.hasNext()) {
                ImpressionUrlModel impressionUrlModel = iterator.next();
                if (impressionUrlModel.getURL().equals(value)) {
                    iterator.remove();
                }
            }
        }
        //sharedList.setUrlList(list);
        setSharedList(list, context, set);
    }

    /**
     * Helper method for getting TrackingUrl object
     *
     * @param set key for preference value
     * @param context context
     * @return TrackingUrls object
     */
    /*private static TrackingUrls getTrackingUrlList(String set, Context context) {
        Log.v(TAG, "TrackingUrlManager: getTrackingUrlList");

        TrackingUrls sharedList = getSharedList(context, set);
        if (sharedList == null) {
            Log.e(TAG, "TrackingUrlManager: sharedList is null");
            sharedList = new TrackingUrls();
        }

        return sharedList;
    }*/

    /**
     * This method is used, when old impression request failed and stored in pending list,
     * to restart impression request for old urls
     *
     * @param context  context
     * @param isFailedPrevious to check request failed or success true: failed,  false: success
     */
    private static void nextImpressionRequest(Context context, boolean isFailedPrevious) {
        Log.v(TAG, "TrackingUrlManager: nextImpressionRequest");
        sIsTracking = false;

        String currentUrl = getValidImpressionUrl(context, CURRENT_URLS_SET);
        removeFromSharedList(context, currentUrl, CURRENT_URLS_SET);

        if (isFailedPrevious) {
            // current url request failed add to pending list
            putToSharedList(context, currentUrl, PENDING_URLS_SET);
        }

        String impressionUrl = getValidImpressionUrl(context, PENDING_URLS_SET);
        if (!TextUtils.isEmpty(impressionUrl)) {
            TrackImpressionUrl(context, impressionUrl);
        }
    }

    /**
     * This method is used to get impression url from preference and update preferences
     *
     * @param context context
     * @param set     set Key for preference value
     * @return impression url
     */
    private static String getValidImpressionUrl(Context context, String set) {
        Log.v(TAG, "TrackingUrlManager: getValidImpressionUrl");

        List<ImpressionUrlModel> urlList = getSharedList(context, set);//sharedList.getUrlList();

        if (urlList != null && urlList.size() > 0) {
            Iterator<ImpressionUrlModel> itr = urlList.iterator();
            ImpressionUrlModel impressionUrlModel = null;
            while (itr.hasNext()) {
                impressionUrlModel = itr.next();
                // Discard urls, if 30 minutes old
                if (System.currentTimeMillis() - impressionUrlModel.getImpressionTime() > DISCARD_TIME_THRESHOLD) {
                    itr.remove();
                } else {
                    itr.remove();
                    break;
                }
            }

            //update pending list in preferences
            setSharedList(urlList, context, PENDING_URLS_SET);

            if (impressionUrlModel != null) {
                return impressionUrlModel.getURL();
            }
        }
        return "";
    }
}
