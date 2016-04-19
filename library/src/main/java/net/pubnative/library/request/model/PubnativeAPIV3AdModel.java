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

package net.pubnative.library.request.model;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PubnativeAPIV3AdModel implements PubnativeAdDataModel {

    private static String                   TAG     = PubnativeAPIV3AdModel.class.getSimpleName();

    protected String                        link;
    protected List<PubnativeAPIV3DataModel> assets;
    protected List<PubnativeAPIV3DataModel> beacons;
    protected List<PubnativeAPIV3DataModel> meta;
    private   List<String>                  assetTrackingUrls = null;

    @Override
    public String getClickUrl() {

        Log.v(TAG, "getClickUrl");
        return link;
    }

    @Override
    public String getTitle() {

        Log.v(TAG, "getTitle");
        String title = null;
        PubnativeAPIV3DataModel asset = findAsset(PubnativeAPIV3AdModel.AssetType.TITLE, true);
        if(asset != null) {
            title = asset.data.get("text");
        }
        return title;
    }

    @Override
    public String getDescription() {

        Log.v(TAG, "getDescription");
        String description = null;
        PubnativeAPIV3DataModel asset = findAsset(PubnativeAPIV3AdModel.AssetType.DESCRIPTION, true);
        if(asset != null) {
            description = asset.data.get("text");
        }
        return description;
    }

    @Override
    public String getCta() {

        Log.v(TAG, "getCta");
        String cta = null;
        PubnativeAPIV3DataModel asset = findAsset(PubnativeAPIV3AdModel.AssetType.CTA, true);
        if(asset != null) {
            cta = asset.data.get("text");
        }
        return cta;
    }

    @Override
    public String getRating() {

        Log.v(TAG, "getRating");
        String rating = null;
        PubnativeAPIV3DataModel asset = findAsset(PubnativeAPIV3AdModel.AssetType.RATING, true);
        if(asset != null) {
            rating = asset.data.get("number");
        }
        return rating;
    }

    @Override
    public PubnativeImage getIcon() {

        Log.v(TAG, "getIcon");
        PubnativeImage icon = null;
        PubnativeAPIV3DataModel asset = findAsset(PubnativeAPIV3AdModel.AssetType.ICON, true);
        if(asset != null) {

            icon = new PubnativeImage();
            icon.width = Integer.valueOf(asset.data.get("w"));
            icon.height = Integer.valueOf(asset.data.get("h"));
            icon.url = asset.data.get("url");
        }
        return icon;
    }

    @Override
    public PubnativeImage getBanner() {

        Log.v(TAG, "getBanner");
        PubnativeImage banner = null;
        PubnativeAPIV3DataModel asset = findAsset(PubnativeAPIV3AdModel.AssetType.BANNER, true);
        if(asset != null) {

            banner = new PubnativeImage();
            banner.width = Integer.valueOf(asset.data.get("w"));
            banner.height = Integer.valueOf(asset.data.get("h"));
            banner.url = asset.data.get("url");
        }
        return banner;
    }

    @Override
    public Map getMetaField(String metaType) {

        Log.v(TAG, "getMetaField");
        HashMap<String, String> result = null;

        if(meta != null) {
            for(PubnativeAPIV3DataModel model: meta) {
                if(model.type.equals(metaType)) {
                    result = model.data;
                }
            }
        }
        return result;
    }

    @Override
    public List getBeacons(String type) {

        Log.v(TAG, "getBeacons");
        List<PubnativeBeacon> allBeacons = null;
        if (beacons != null) {

            allBeacons = new ArrayList<PubnativeBeacon>();
            for (PubnativeAPIV3DataModel beacon : beacons) {
                if(beacon.type.equals(type)) {
                    boolean shouldAdd = false;
                    PubnativeBeacon pubnativeBeacon = new PubnativeBeacon();
                    pubnativeBeacon.type = beacon.type;
                    if(!TextUtils.isEmpty(beacon.data.get("url"))) {
                        pubnativeBeacon.url = beacon.data.get("url");
                        shouldAdd = true;
                    }
                    if(!TextUtils.isEmpty(beacon.data.get("js"))) {
                        pubnativeBeacon.js = beacon.data.get("js");
                        shouldAdd = true;
                    }
                    if(shouldAdd) {
                        allBeacons.add(pubnativeBeacon);
                    }
                }
            }
        }
        return allBeacons;
    }

    @Override
    public List getAssetTrackingUrls() {
        return assetTrackingUrls;
    }

    //==============================================================================================
    // Private
    //==============================================================================================
    private PubnativeAPIV3DataModel findAsset(String assetType, boolean getTrackingUrl) {

        PubnativeAPIV3DataModel result = null;

        if(assets != null) {
            for(PubnativeAPIV3DataModel model: assets) {
                if(model.type.equals(assetType)) {
                    result = model;
                    if(getTrackingUrl && model.data.containsKey("tracking") && !TextUtils.isEmpty(model.data.get("tracking"))) {
                        assetTrackingUrls.add(model.data.get("tracking"));
                    }
                }
            }
        }
        return result;
    }
}
