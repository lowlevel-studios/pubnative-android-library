package net.pubnative.library.request.model;

import android.util.Log;
import android.view.View;

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
    public PubnativeIcon getIcon() {

        Log.v(TAG, "getIcon");
        PubnativeIcon icon = null;
        PubnativeAPIV3DataModel asset = findAsset(PubnativeAssetType.ICON);
        if(asset != null) {

            icon = new PubnativeIcon();
            icon.setWidth(Integer.valueOf(asset.getData().get("w")));
            icon.setHeight(Integer.valueOf(asset.getData().get("h")));
            icon.setUrl(asset.getData().get("url"));
        }
        return icon;
    }

    @Override
    public PubnativeBanner getBanner() {

        Log.v(TAG, "getBanner");
        PubnativeBanner banner = null;
        PubnativeAPIV3DataModel asset = findAsset(PubnativeAssetType.BANNER);
        if(asset != null) {

            banner = new PubnativeBanner();
            banner.setWidth(Integer.valueOf(asset.getData().get("w")));
            banner.setHeight(Integer.valueOf(asset.getData().get("h")));
            banner.setUrl(asset.getData().get("url"));
        }
        return banner;
    }

    @Override
    public String getPoints() {

        Log.v(TAG, "getPoints");
        String points = null;
        PubnativeAPIV3DataModel meta = findMeta(PubnativeMetaType.POINTS);
        if(meta != null) {
            points = meta.getData().get("number");
        }
        return points;
    }

    @Override
    public String getRevenueModel() {

        Log.v(TAG, "getRevenueModel");
        String revenueModel = null;
        PubnativeAPIV3DataModel meta = findMeta(PubnativeMetaType.REVENU_MODEL);
        if(meta != null) {
            revenueModel = meta.getData().get("text");
        }
        return revenueModel;
    }

    @Override
    public String getCampaignId() {

        Log.v(TAG, "getCampaignId");
        String campaignId = null;
        PubnativeAPIV3DataModel meta = findMeta(PubnativeMetaType.CAMPAIGN_ID);
        if(meta != null) {
            campaignId = meta.getData().get("number");
        }
        return campaignId;
    }

    @Override
    public String getCreativeId() {

        Log.v(TAG, "getCreativeId");
        String creativeId = null;
        PubnativeAPIV3DataModel meta = findMeta(PubnativeMetaType.CREATIVE_ID);
        if(meta != null) {
            creativeId = meta.getData().get("number");
        }
        return creativeId;
    }

    /**
     * Start tracking of ad view
     *
     * @param view     ad view
     * @param listener listener for callbacks
     */
    public void startTracking(View view, Listener listener) {

        Log.v(TAG, "startTracking");
        super.startTracking(view, view, listener, beacons);
    }

    /**
     * start tracking of your ad view
     *
     * @param view          ad view
     * @param clickableView clickable view
     * @param listener      listener for callbacks
     */
    public void startTracking(View view, View clickableView, Listener listener) {

        Log.v(TAG, "startTracking");
        super.startTracking(view, clickableView, listener, beacons);
    }

    //==============================================================================================
    // Private
    //==============================================================================================

    private PubnativeAPIV3DataModel findAsset(String assetType) {

        PubnativeAPIV3DataModel result = null;
        for(PubnativeAPIV3DataModel model: assets) {
            if(model.getType().equals(assetType)) {
                result = model;
            }
        }
        return result;
    }

    private PubnativeAPIV3DataModel findMeta(String assetType) {

        PubnativeAPIV3DataModel result = null;
        for(PubnativeAPIV3DataModel model: meta) {
            if(model.getType().equals(assetType)) {
                result = model;
            }
        }
        return result;
    }
}
