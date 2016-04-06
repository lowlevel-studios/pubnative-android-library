package net.pubnative.library.request.model;

import java.util.List;

public class PubnativeAPIV3AdModel extends PubnativeAdModel2 {

    protected String                        link;
    protected List<PubnativeAPIV3DataModel> assets;
    protected List<PubnativeAPIV3DataModel> beacons;
    protected List<PubnativeAPIV3DataModel> meta;

    protected List<String>                  requestedAssets;

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getCtaText() {
        return null;
    }

    @Override
    public String getIconUrl() {
        return null;
    }

    @Override
    public String getBannerUrl() {
        return null;
    }

    @Override
    public String getClickUrl() {
        return null;
    }

    @Override
    public String getRevenueModel() {
        return null;
    }

    @Override
    public List<PubnativeBeacon> getBeacons() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getPortraitBannerUrl() {
        return null;
    }

    public List<String> getRequestedAssets() {
        return requestedAssets;
    }

    public void setRequestedAssets(List<String> requestedAssets) {
        this.requestedAssets = requestedAssets;
    }
}
