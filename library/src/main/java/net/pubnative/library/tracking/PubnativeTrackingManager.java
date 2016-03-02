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
import net.pubnative.library.tracking.model.PubnativeTrackingURLModel;

import java.util.ArrayList;
import java.util.List;

public class PubnativeTrackingManager {

    private static final String  TAG                 = PubnativeTrackingManager.class.getSimpleName();
    private static final String  SHARED_PREFERENCES  = "net.pubnative.library.tracking.PubnativeTrackingManager";
    private static final String  SHARED_PENDING_URLS = "pending_urls";
    private static final String  SHARED_FAILED_URLS = "pending_urls";
    private static final long    ITEM_VALIDITY_TIME  = 1800000; // 30 minutes
    private static       boolean sIsTracking         = false;
    //==============================================================================================
    // PUBLIC
    //==============================================================================================

    /**
     * This method is used to send impression request
     *
     * @param context valid context
     * @param url     Url to track
     */
    public synchronized static void track(Context context, String url) {

        Log.v(TAG, "track");
        if (context == null) {
            Log.e(TAG, "track - ERROR: Context parameter is null");
        } else if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "track - ERROR: url parameter is null");
        } else {
            // 1. Enqueue failed items
            enqueueFailedList(context);
            // 2. Enqueue current item
            PubnativeTrackingURLModel model = new PubnativeTrackingURLModel();
            model.url = url;
            model.startTimestamp = System.currentTimeMillis();
            enqueueItem(context, SHARED_PENDING_URLS, model);
            // 3. Try doing next item
            trackNextItem(context);
        }
    }

    //==============================================================================================
    // PRIVATE
    //==============================================================================================
    protected static void trackNextItem(final Context context) {

        Log.v(TAG, "trackNextItem");
        if (sIsTracking) {
            Log.w(TAG, "trackNextItem - Currently tracking, dropping the call, will be resumed soon");
        } else {

            sIsTracking = true;

            // Extract pending item
            final PubnativeTrackingURLModel model = dequeueItem(context, SHARED_PENDING_URLS);
            if(model.startTimestamp + ITEM_VALIDITY_TIME < System.currentTimeMillis()) {
                // Discard item, let's go for the next one
                trackNextItem(context);
            } else {
                PubnativeAPIRequest.send(model.url, new PubnativeAPIRequest.Listener() {

                    @Override
                    public void onPubnativeAPIRequestResponse(String response) {

                        Log.v(TAG, "onPubnativeAPIRequestResponse" + response);
                        trackNextItem(context);
                    }

                    @Override
                    public void onPubnativeAPIRequestError(Exception error) {

                        Log.e(TAG, "onPubnativeAPIRequestError " + error);
                        // Since this failed, we re-enqueue it
                        enqueueItem(context, SHARED_FAILED_URLS, model);
                        trackNextItem(context);
                    }
                });
            }
        }
    }

    //==============================================================================================
    // QUEUE
    //==============================================================================================
    protected static void enqueueFailedList(Context context) {

        Log.v(TAG, "enqueueFailedList");
        List<PubnativeTrackingURLModel> failedList = getList(context, SHARED_FAILED_URLS);
        List<PubnativeTrackingURLModel> pendingList = getList(context, SHARED_PENDING_URLS);
        pendingList.addAll(failedList);
        setList(context, SHARED_PENDING_URLS, failedList);
    }

    protected static void enqueueItem(Context context, String listKey, PubnativeTrackingURLModel model) {

        Log.v(TAG, "enqueueItem: " + listKey);
        List<PubnativeTrackingURLModel> pendingList = getList(context, listKey);
        pendingList.add(model);
        setList(context, listKey, pendingList);
    }

    protected static PubnativeTrackingURLModel dequeueItem(Context context, String listKey) {

        Log.v(TAG, "dequeueItem: " + listKey);
        PubnativeTrackingURLModel result = null;
        List<PubnativeTrackingURLModel> pendingList = getList(context, listKey);
        if (pendingList.size() > 0) {
            result = pendingList.get(0);
            pendingList.remove(0);
            setList(context, listKey, pendingList);
        }
        return result;
    }

    //==============================================================================================
    // SHARED PREFERENCES
    //==============================================================================================
    // List helper
    //----------------------------------------------------------------------------------------------
    protected static List<PubnativeTrackingURLModel> getList(Context context, String key) {

        Log.v(TAG, "getList: " + key);
        List<PubnativeTrackingURLModel> result = null;
        SharedPreferences preferences = getSharedPreferences(context);
        String sharedPendingString = preferences.getString(key, null);
        if (sharedPendingString == null) {
            result = new ArrayList<PubnativeTrackingURLModel>();
        } else {
            result = new Gson().fromJson(sharedPendingString, new TypeToken<List<PubnativeTrackingURLModel>>() {}.getType());
        }
        return result;
    }

    protected static void setList(Context context, String key, List<PubnativeTrackingURLModel> value) {

        Log.v(TAG, "setList: " + key);
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        if (value == null) {
            preferencesEditor.remove(key);
        } else {
            String listString = new Gson().toJson(value);
            preferencesEditor.putString(key, listString);
        }
        preferencesEditor.apply();
    }

    // Base shared preferences
    //----------------------------------------------------------------------------------------------
    protected static SharedPreferences getSharedPreferences(Context context) {

        Log.v(TAG, "getSharedPreferences");
        return context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }
}
