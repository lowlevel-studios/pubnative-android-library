package net.pubnative.library.request.model;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PubnativeAPIV3AdModel extends PubnativeAdModel {

    private static String                   TAG         = PubnativeAPIV3AdModel.class.getSimpleName();

    protected String                        link;
    protected List<PubnativeAPIV3DataModel> assets;
    protected List<PubnativeAPIV3DataModel> beacons;
    protected List<PubnativeAPIV3DataModel> meta;

    //==============================================================================================
    // Public
    //==============================================================================================

    @Override
    public String getClickUrl() {

        Log.v(TAG, "getClickUrl");
        return link;
    }

    @Override
    public String getTitle() {

        Log.v(TAG, "getTitle");
        String title = null;
        PubnativeAPIV3DataModel asset = findAsset(PubnativeAssetType.TITLE);
        if(asset != null) {
            title = asset.getData().get("text");
        }
        return title;
    }

    @Override
    public String getDescription() {

        Log.v(TAG, "getDescription");
        String description = null;
        PubnativeAPIV3DataModel asset = findAsset(PubnativeAssetType.DESCRIPTION);
        if(asset != null) {
            description = asset.getData().get("text");
        }
        return description;
    }

    @Override
    public String getCta() {

        Log.v(TAG, "getCta");
        String cta = null;
        PubnativeAPIV3DataModel asset = findAsset(PubnativeAssetType.CTA);
        if(asset != null) {
            cta = asset.getData().get("text");
        }
        return cta;
    }

    @Override
    public String getRating() {

        Log.v(TAG, "getRating");
        String cta = null;
        PubnativeAPIV3DataModel asset = findAsset(PubnativeAssetType.RATING);
        if(asset != null) {
            cta = asset.getData().get("number");
        }
        return cta;
    }

    @Override
    public PubnativeImage getIcon() {

        Log.v(TAG, "getIcon");
        PubnativeImage icon = null;
        PubnativeAPIV3DataModel asset = findAsset(PubnativeAssetType.ICON);
        if(asset != null) {

            icon = new PubnativeImage();
            icon.width = Integer.valueOf(asset.getData().get("w"));
            icon.height = Integer.valueOf(asset.getData().get("h"));
            icon.url = asset.getData().get("url");
        }
        return icon;
    }

    @Override
    public PubnativeImage getBanner() {

        Log.v(TAG, "getBanner");
        PubnativeImage banner = null;
        PubnativeAPIV3DataModel asset = findAsset(PubnativeAssetType.BANNER);
        if(asset != null) {

            banner = new PubnativeImage();
            banner.width = Integer.valueOf(asset.getData().get("w"));
            banner.height = Integer.valueOf(asset.getData().get("h"));
            banner.url = asset.getData().get("url");
        }
        return banner;
    }

    @Override
    public PubnativeAPIV3DataModel getMetaField(String metaType) {

        Log.v(TAG, "getMetaField");
        PubnativeAPIV3DataModel result = null;

        if(meta != null) {
            for(PubnativeAPIV3DataModel model: meta) {
                if(model.getType().equals(metaType)) {
                    result = model;
                }
            }
        }

        return result;
    }

    @Override
    protected List<String> getAllBeacons() {

        Log.v(TAG, "getAllBeacons");
        List<String> allBeacons = null;
        if (beacons != null) {
            allBeacons = new ArrayList<String>();
            for (PubnativeAPIV3DataModel beacon : beacons) {
                if(!TextUtils.isEmpty(beacon.getData().get("url"))) {
                    allBeacons.add(beacon.getData().get("url"));
                }
            }
        }
        return allBeacons;
    }

    //==============================================================================================
    // Private
    //==============================================================================================
    private PubnativeAPIV3DataModel findAsset(String assetType) {

        PubnativeAPIV3DataModel result = null;

        if(assets != null) {
            for(PubnativeAPIV3DataModel model: assets) {
                if(model.getType().equals(assetType)) {
                    result = model;
                }
            }
        }

        return result;
    }
}
