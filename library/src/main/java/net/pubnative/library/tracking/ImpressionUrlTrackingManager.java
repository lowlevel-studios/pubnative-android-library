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
import net.pubnative.library.tracking.model.TrackingUrls;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ImpressionUrlTrackingManager {

    private static          String TAG                  = ImpressionUrlTrackingManager.class.getSimpleName();
    private static final    String SHARED_FILE          = "net.pubnative.library.tracking.ImpressionUrlTrackingManager";
    private static final    String CURRENT_URLS_SET     = "current_urls";
    private static final    String PENDING_URLS_SET     = "pending_urls";
    private static final    long DISCARD_TIME_THRESHOLD = 30 * 60 * 1000; // 30 minutes

    private static boolean  mIsTracking                 = false;

    //==============================================================================================
    // PRIVATE
    //==============================================================================================

    /**
     * This method returns Trackingurl stored as List in sharedPreference
     *
     * @param context context
     * @param set key for preference value
     * @return TrackingUrl TrackingUrl object
     */
    private static TrackingUrls getSharedList(final Context context, String set) {
        Log.v(TAG, "ImpressionUrlTrackingManager: getSharedList");

        TrackingUrls result = null;
        SharedPreferences preferences = context.getSharedPreferences(SHARED_FILE, 0);

        if (preferences != null && preferences.contains(set)) {
            String sharedListString = preferences.getString(set, null);
            if (sharedListString != null) {
                Gson gson = new Gson();
                result = gson.fromJson(sharedListString, TrackingUrls.class);
            }
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
    private synchronized static void setSharedList(TrackingUrls sharedList, Context context, String set) {
        Log.v(TAG, "ImpressionUrlTrackingManager: setSharedList");
        Gson gson = new Gson();
        String sharedListString = gson.toJson(sharedList);

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
        Log.v(TAG, "ImpressionUrlTrackingManager: putToSharedList");

        TrackingUrls sharedList = getTrackingUrlList(set, context);
        List<ImpressionUrlModel> list = sharedList.getUrlList();

        if (list == null) {
            Log.w(TAG, "ImpressionUrlTrackingManager: list is null");

            list = new ArrayList<ImpressionUrlModel>();
            list.add(new ImpressionUrlModel(value, new Date().getTime()));
        } else {
            for (ImpressionUrlModel item : list) {
                if (!item.getURL().equalsIgnoreCase(value)) {
                    list.add(new ImpressionUrlModel(value, new Date().getTime()));
                }
            }
        }

        sharedList.setUrlList(list);
        setSharedList(sharedList, context, set);
    }

    /**
     * This method is used to remove url from list and update sharedpreference
     *
     * @param context context
     * @param set key for preference value
     * @param value Ad impression url value
     */
    private static void removeFromSharedList(final Context context, String set, String value) {
        Log.v(TAG, "ImpressionUrlTrackingManager: removeFromSharedList");

        TrackingUrls sharedList = getTrackingUrlList(set, context);

        List<ImpressionUrlModel> list = sharedList.getUrlList();
        if (list != null && list.size() > 0) {

            Iterator<ImpressionUrlModel> iterator = list.iterator();
            while (iterator.hasNext()) {
                ImpressionUrlModel impressionUrlModel = iterator.next();
                if (impressionUrlModel.getURL().equals(value)) {
                    iterator.remove();
                }
            }
        }
        sharedList.setUrlList(list);
        setSharedList(sharedList, context, set);
    }

    /**
     * Helper method for getting TrackingUrl object
     *
     * @param set key for preference value
     * @param context context
     * @return TrackingUrls object
     */
    private static TrackingUrls getTrackingUrlList(String set, Context context) {
        Log.v(TAG, "ImpressionUrlTrackingManager: getTrackingUrlList");

        TrackingUrls sharedList = ImpressionUrlTrackingManager.getSharedList(context, set);
        if (sharedList == null) {
            Log.e(TAG, "ImpressionUrlTrackingManager: sharedList is null");
            sharedList = new TrackingUrls();
        }

        return sharedList;
    }

    /**
     * This method is used, when old impression request failed and stored in pending list,
     * to restart impression request for old urls
     *
     * @param context  context
     * @param isFailedPrevious to check request failed or success true: failed,  false: success
     */
    private static void nextImpressionRequest(Context context, boolean isFailedPrevious) {
        Log.v(TAG, "ImpressionUrlTrackingManager: nextImpressionRequest");
        mIsTracking = false;

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
        Log.v(TAG, "ImpressionUrlTrackingManager: getValidImpressionUrl");

        TrackingUrls impressionUrl = ImpressionUrlTrackingManager.getSharedList(context, set);
        List<ImpressionUrlModel> urlList = impressionUrl.getUrlList();

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
            impressionUrl.setUrlList(urlList);
            setSharedList(impressionUrl, context, PENDING_URLS_SET);

            if (impressionUrlModel != null) {
                return impressionUrlModel.getURL();
            }
        }
        return "";
    }

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
        Log.v(TAG, "ImpressionUrlTrackingManager: TrackImpressionUrl");
        if (mIsTracking) {
            // previous request in process, return and store url in pending list
            ImpressionUrlTrackingManager.putToSharedList(context, PENDING_URLS_SET, impressionUrl);
            return;
        }

        mIsTracking = true;
        ImpressionUrlTrackingManager.putToSharedList(context, CURRENT_URLS_SET, impressionUrl);

        PubnativeAPIRequest.send(impressionUrl, new PubnativeAPIRequest.Listener() {

            @Override
            public void onPubnativeAPIRequestResponse(String response) {
                Log.v(TAG, "ImpressionUrlTrackingManager: onPubnativeAPIRequestResponse");
                nextImpressionRequest(context, false);
            }

            @Override
            public void onPubnativeAPIRequestError(Exception error) {
                Log.v(TAG, "ImpressionUrlTrackingManager: onPubnativeAPIRequestError");
                nextImpressionRequest(context, true);
            }
        });
    }
}
