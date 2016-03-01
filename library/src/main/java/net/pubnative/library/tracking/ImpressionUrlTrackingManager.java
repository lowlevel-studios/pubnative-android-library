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

import com.google.gson.Gson;

import net.pubnative.library.network.PubnativeAPIRequest;
import net.pubnative.library.tracking.model.ImpressionUrlModel;
import net.pubnative.library.tracking.model.TrackingUrls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImpressionUrlTrackingManager {

    private static final String SHARED_FILE = "net.pubnative.library.tracking.ImpressionUrlTrackingManager";
    private static final String CONFIRMED_URLS_SET = "confirmed_urls";
    private static final String PENDING_URLS_SET = "pending_urls";

    private static boolean isTracking = false;

    private static TrackingUrls getSharedList(final Context context, String set) {
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

    private static void putToSharedList(final Context context, String set, String value) {
        TrackingUrls sharedList = ImpressionUrlTrackingManager.getSharedList(context, set);
        if (sharedList == null) {
            sharedList = new TrackingUrls();
        }

        List<ImpressionUrlModel> list = sharedList.getUrlList();

        if(list == null) {
            list = new ArrayList<ImpressionUrlModel>();
            list.add(new ImpressionUrlModel(value, new Date().getTime()));
        } else {
            for(ImpressionUrlModel item: list) {
                if (!item.getURL().equalsIgnoreCase(value)) {
                    list.add(new ImpressionUrlModel(value, new Date().getTime()));
                }
            }
        }

        Gson gson = new Gson();
        String sharedListString = gson.toJson(sharedList);

        SharedPreferences.Editor editablePreferences = context.getSharedPreferences(SHARED_FILE, 0).edit();
        editablePreferences.putString(set, sharedListString);
        editablePreferences.apply();

    }

    /*private static void removeFromSharedList(final Context context, String set, String value) {
        List<String> sharedList = ImpressionUrlTrackingManager.getSharedList(context, set);
        if (sharedList != null && sharedList.contains(value)) {
            sharedList.remove(value);
            Gson gson = new Gson();
            String sharedListString = gson.toJson(sharedList);

            SharedPreferences.Editor editablePreferences = context.getSharedPreferences(SHARED_FILE, 0).edit();
            editablePreferences.putString(set, sharedListString);
            editablePreferences.apply();
        }
    }*/

    public synchronized static void TrackImpressionUrl(Context context, String impressionUrl) {
        TrackingUrls confirmedAds = ImpressionUrlTrackingManager.getSharedList(context, CONFIRMED_URLS_SET);
        if (confirmedAds == null) {
            confirmedAds = new TrackingUrls();
        }

        if(isTracking) {
            // TODO store this url in pending;
            return;
        }

        PubnativeAPIRequest.send(impressionUrl, new PubnativeAPIRequest.Listener(){

            @Override
            public void onPubnativeAPIRequestResponse(String response) {

            }

            @Override
            public void onPubnativeAPIRequestError(Exception error) {

            }
        });
    }
}
