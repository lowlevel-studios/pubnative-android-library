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
import com.google.gson.reflect.TypeToken;

import net.pubnative.library.network.PubnativeAPIRequest;
import net.pubnative.library.tracking.model.TrackingURLModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TrackingManager {

    private static final String TAG                     = TrackingManager.class.getSimpleName();
    private static final String SHARED_FILE             = "net.pubnative.library.tracking.TrackingManager";
    private static final String CURRENT_URLS_SET        = "current_urls";
    private static final String PENDING_URLS_SET        = "pending_urls";
    private static final long DISCARD_TIME_THRESHOLD    = 1800000; // 30 minutes

    private static boolean sIsTracking                  = false;

    //==============================================================================================
    // PUBLIC
    //==============================================================================================

    /**
     * This method is used to send impression request
     *
     * @param context       context
     * @param impressionUrl Ad impression url for request
     */
    public synchronized static void trackImpressionUrl(final Context context, final String impressionUrl) {

        Log.v(TAG, "trackImpressionUrl(Context context, String impressionUrl)");

        if (sIsTracking) {
            // previous request in process, return and store url in pending list
            TrackingManager.storeTrackingUrls(context, PENDING_URLS_SET, impressionUrl);
            return;
        }

        sIsTracking = true;
        TrackingManager.storeTrackingUrls(context, CURRENT_URLS_SET, impressionUrl);

        PubnativeAPIRequest.send(impressionUrl, new PubnativeAPIRequest.Listener() {

            @Override
            public void onPubnativeAPIRequestResponse(String response) {

                Log.v(TAG, "onPubnativeAPIRequestResponse");
                nextImpressionRequest(context, false);
            }

            @Override
            public void onPubnativeAPIRequestError(Exception error) {

                Log.v(TAG, "onPubnativeAPIRequestError " + error);
                nextImpressionRequest(context, true);
            }
        });
    }

    //==============================================================================================
    // PRIVATE
    //==============================================================================================

    /**
     * This method returns TrackingUrl stored as List in sharedPreference
     *
     * @param context                      context
     * @param listKey                      key for preference value
     * @return List<TrackingURLModel>      List of TrackingUrlModel objects
     */
    private static List<TrackingURLModel> getSharedList(Context context, String listKey) {

        Log.v(TAG, "getSharedList(Context context, String listKey)");

        List<TrackingURLModel> result;
        SharedPreferences preferences = context.getSharedPreferences(SHARED_FILE, 0);

        String sharedListString = preferences.getString(listKey, null);
        if (sharedListString != null) {
            result = new Gson().fromJson(sharedListString, new TypeToken<List<TrackingURLModel>>() {}.getType());
        } else {
            result = new ArrayList<TrackingURLModel>();
        }

        return result;
    }

    /**
     * This method is used to save and update values in preference
     *
     * @param sharedList Trackingurl object
     * @param context    context
     * @param listKey    key for preference value
     */
    private synchronized static void setSharedList(Context context, String listKey, List<TrackingURLModel> sharedList) {

        Log.v(TAG, "setSharedList: store trackinUrl in " + listKey);

        String sharedListString = new Gson().toJson(sharedList);
        SharedPreferences.Editor editablePreferences = context.getSharedPreferences(SHARED_FILE, 0).edit();
        editablePreferences.putString(listKey, sharedListString).apply();
    }

    /**
     * This method used to store url as list in sharedPreference
     *
     * @param context       context
     * @param listKey       key for preference trackingUrl
     * @param trackingUrl         Ad impression url trackingUrl
     */
    private static void storeTrackingUrls(Context context, String listKey, String trackingUrl) {

        Log.v(TAG, "storeTrackingUrls: add trackingUrl in " + listKey + ", trackingUrl is " + trackingUrl);
        List<TrackingURLModel> trackingURLModelList = getSharedList(context, listKey);

        boolean shouldAdd = true;
        for (TrackingURLModel item : trackingURLModelList) {
            if (item.getUrl().equalsIgnoreCase(trackingUrl)) {
                shouldAdd = false;
                break;
            }
        }

        if (shouldAdd) {
            trackingURLModelList.add(new TrackingURLModel(trackingUrl));
        }

        setSharedList(context, listKey, trackingURLModelList);
    }

    /**
     * This method is used to remove url from list and update sharedpreference
     *
     * @param context       context
     * @param listKey       key for preference storedTrackingUrl
     * @param storedTrackingUrl         Ad impression url storedTrackingUrl
     */
    private static void removeStoredTrackingUrl(Context context, String listKey, String storedTrackingUrl) {

        Log.v(TAG, "removeStoredTrackingUrl: remove " + storedTrackingUrl + " from " + listKey);
        List<TrackingURLModel> ltrackingURLModelList = getSharedList(context, listKey);

        Iterator<TrackingURLModel> iterator = ltrackingURLModelList.iterator();
        while (iterator.hasNext()) {
            TrackingURLModel trackingURLModel = iterator.next();
                if (trackingURLModel.getUrl().equals(storedTrackingUrl)) {
                    iterator.remove();
                }
            }

        setSharedList(context, listKey, ltrackingURLModelList);
    }

    /**
     * This method is used, when old impression request failed and stored in pending list,
     * to restart impression request for old urls
     *
     * @param context          context
     * @param isFailedPrevious to check request failed or success true: failed,  false: success
     */
    private static void nextImpressionRequest(Context context, boolean isFailedPrevious) {

        Log.v(TAG, "nextImpressionRequest(Context context, boolean isFailedPrevious)");
        sIsTracking = false;

        String currentUrl = getValidImpressionUrl(context, CURRENT_URLS_SET);
        removeStoredTrackingUrl(context, currentUrl, CURRENT_URLS_SET);

        if (isFailedPrevious) {
            // current url request failed add to pending list
            Log.v(TAG, "Previous failed track url : " + currentUrl);
            storeTrackingUrls(context, currentUrl, PENDING_URLS_SET);
        }

        String pendingTrackingUrl = getValidImpressionUrl(context, PENDING_URLS_SET);
        if (!TextUtils.isEmpty(pendingTrackingUrl)) {
            trackImpressionUrl(context, pendingTrackingUrl);
        }
    }

    /**
     * This method is used to get impression url from preference and update preferences
     *
     * @param context       context
     * @param listKey       listKey Key for preference value
     * @return String       impression url
     */
    private static String getValidImpressionUrl(Context context, String listKey) {

        Log.v(TAG, "getValidImpressionUrl(Context context, String listKey)");

        List<TrackingURLModel> trackingURLModelList = getSharedList(context, listKey);//sharedList.getUrlList();

        if (trackingURLModelList.size() > 0) {
            Iterator<TrackingURLModel> itr = trackingURLModelList.iterator();
            TrackingURLModel trackingURLModel = null;
            while (itr.hasNext()) {
                trackingURLModel = itr.next();
                boolean shouldBreak = false;
                // Discard urls, if 30 minutes old
                if (!(System.currentTimeMillis() - trackingURLModel.getTrackingStartTime() > DISCARD_TIME_THRESHOLD)) {
                    shouldBreak = true;
                }

                itr.remove();
                if(shouldBreak){
                    break;
                }
            }

            //update keySet list in preferences
            setSharedList(context, listKey, trackingURLModelList);

            if (trackingURLModel != null) {
                return trackingURLModel.getUrl();
            }
        }
        return "";
    }
}
