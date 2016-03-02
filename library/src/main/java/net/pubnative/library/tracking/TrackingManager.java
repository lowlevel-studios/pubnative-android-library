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
    private static final String CURRENT_URL             = "current_url";
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
    public synchronized static void trackImpressionUrl(final Context context, String impressionUrl) {

        Log.v(TAG, "trackImpressionUrl(Context context, String impressionUrl)");
        if (sIsTracking) {
            // previous request in process, return and store url in pending list
            addPendingTrackingUrl(context, impressionUrl);
            return;
        } else {
            // In case last request not processed unconditional (Device hang, power off)
            TrackingURLModel lastFailedRequest = getCurrentUrl(context);

            if(lastFailedRequest != null){
                String lastFailedRequestUrl = lastFailedRequest.getUrl();
                addPendingTrackingUrl(context, lastFailedRequestUrl);
            }
        }

        sIsTracking = true;
        storeCurrentUrl(context, impressionUrl);

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
     * @return List<TrackingURLModel>      List of TrackingUrlModel objects
     */
    private static List<TrackingURLModel> getPendingTrackingUrls(Context context) {

        Log.v(TAG, "getPendingTrackingUrls(Context context, String listKey)");
        List<TrackingURLModel> result;
        SharedPreferences preferences = context.getSharedPreferences(SHARED_FILE, 0);

        String sharedListString = preferences.getString(PENDING_URLS_SET, null);
        if (sharedListString != null) {
            result = new Gson().fromJson(sharedListString, new TypeToken<List<TrackingURLModel>>() {}.getType());
        } else {
            result = new ArrayList<TrackingURLModel>();
        }

        return result;
    }

    /**
     * This method is used to save pendingTrackingUrls in sharedPreference
     *
     * @param context    context
     * @param sharedList pendingTrackingUrl objects list
     */
    private synchronized static void setPendingTrackingUrls(Context context, List<TrackingURLModel> sharedList) {

        Log.v(TAG, "setPendingTrackingUrls(Context context, List<TrackingURLModel> sharedList)");
        String sharedListString = new Gson().toJson(sharedList);
        SharedPreferences.Editor editablePreferences = context.getSharedPreferences(SHARED_FILE, 0).edit();
        editablePreferences.putString(PENDING_URLS_SET, sharedListString).apply();
    }

    /**
     * This method used to add pendingTrackingUrl in pendingList and store in  sharedPreference
     *
     * @param context       context
     * @param trackingUrl   Ad impression url trackingUrl
     */
    private static void addPendingTrackingUrl(Context context, String trackingUrl) {

        Log.v(TAG, "addPendingTrackingUrl(Context context, String trackingUrl)");
        List<TrackingURLModel> trackingURLModelList = getPendingTrackingUrls(context);

        boolean shouldAdd = true;
        for (TrackingURLModel item : trackingURLModelList) {
            if (item.getUrl().equalsIgnoreCase(trackingUrl)) {

                Log.w(TAG, "addPendingTrackingUrl: already added");
                shouldAdd = false;
                break;
            }
        }

        if (shouldAdd) {
            trackingURLModelList.add(new TrackingURLModel(trackingUrl));
        }

        setPendingTrackingUrls(context, trackingURLModelList);
    }

    /**
     * This method is used to store current request Tracking url
     *
     * @param context       context
     * @param trackingUrl   Ad impression trackingUrl
     */
    private static void storeCurrentUrl(Context context, String trackingUrl) {

        Log.v(TAG, "storeCurrentUrl(Context context, String trackingUrl)");
        SharedPreferences.Editor editablePreferences = context.getSharedPreferences(SHARED_FILE, 0).edit();
        editablePreferences.putString(CURRENT_URL, new Gson().toJson(new TrackingURLModel(trackingUrl))).apply();
    }

    /**
     * This method is used to clear last successfully processed Tracking url from SharedPreferences
     *
     * @param context context
     */
    private static void clearCurrentUrl(Context context){

        Log.v(TAG, "clearCurrentUrl(Context context)");
        SharedPreferences.Editor editablePreferences = context.getSharedPreferences(SHARED_FILE, 0).edit();
        editablePreferences.remove(CURRENT_URL).apply();
    }

    /**
     * This method is used to get processing impressionUrl from SharedPreferences
     *
     * @param context           context
     * @return TrackingURLModel Ad impression url and tracking startTime of processing/Processed(Failure case) request
     */
    private static TrackingURLModel getCurrentUrl(Context context) {

        Log.v(TAG, "getCurrentUrl(Context context)");
        TrackingURLModel result = null;

        SharedPreferences preferences = context.getSharedPreferences(SHARED_FILE, 0);
        String trackingURLModelString = preferences.getString(CURRENT_URL, null);

        if(trackingURLModelString != null) {
            result = new Gson().fromJson(trackingURLModelString, TrackingURLModel.class);
        }

        return result;
    }

    /**
     * This method is used, when old impression request failed and stored in pending list,
     * to restart impression request for pendingTrackingUrls
     *
     * @param context          context
     * @param isFailedPrevious to check request failed or success true: failed,  false: success
     */
    private static void nextImpressionRequest(Context context, boolean isFailedPrevious) {

        Log.v(TAG, "nextImpressionRequest(Context context, boolean isFailedPrevious)");
        TrackingURLModel trackingURLModel = getCurrentUrl(context);
        //removing previous stored current url
        clearCurrentUrl(context);

        sIsTracking = false;

        if (isFailedPrevious) {
            // current url request failed add to pending list
            Log.e(TAG, "nextImpressionRequest: Previous failed track url : " + trackingURLModel.getUrl());
            addPendingTrackingUrl(context, trackingURLModel.getUrl());
        }

        String trackingUrl = getNextValidTrackingUrl(context);
        if (trackingUrl != null) { // no url found
            trackImpressionUrl(context, trackingUrl);
        }
    }

    /**
     * This method is used to get valid pendingTrackingUrls from SharedPreferences
     *
     * @param context       context
     * @return String       impression url
     */
    private static String getNextValidTrackingUrl(Context context) {

        Log.v(TAG, "getNextValidTrackingUrl(Context context, String listKey)");
        String validUrlResult = null;
        List<TrackingURLModel> trackingURLModelList = getPendingTrackingUrls(context);

        TrackingURLModel trackingURLModel = null;

        Iterator<TrackingURLModel> urlIterator = trackingURLModelList.iterator();
        while (urlIterator.hasNext()) {
            trackingURLModel = urlIterator.next();

            urlIterator.remove();

            // Discard url, if older more than 30 minutes
            if (System.currentTimeMillis() - trackingURLModel.getTrackingStartTime() < DISCARD_TIME_THRESHOLD) {
                break; // find valid url
            }
        }

        //update keySet list in preferences
        setPendingTrackingUrls(context, trackingURLModelList);

        if (trackingURLModel != null) {
            validUrlResult = trackingURLModel.getUrl();
        }

        return validUrlResult;
    }
}
