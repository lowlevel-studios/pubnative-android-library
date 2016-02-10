package net.pubnative.library.models;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import net.pubnative.library.tracking.PubnativeTracker;
import net.pubnative.library.utils.SystemUtils;

import java.util.List;

public class PubnativeAdModel {

    private static String           TAG = PubnativeAdModel.class.getSimpleName();

    private String                  title;
    private String                  description;
    private String                  cta_text;
    private String                  icon_url;
    private String                  banner_url;
    private String                  click_url;
    private String                  revenue_model;
    private int                     points;
    private List<PubnativeBeacon>   beacons;
    private String                  type;
    private String                  portrait_banner_url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCtaText() {
        return cta_text;
    }

    public void setCtaText(String cta_text) {
        this.cta_text = cta_text;
    }

    public String getIconUrl() {
        return icon_url;
    }

    public void setIconUrl(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getBannerUrl() {
        return banner_url;
    }

    public void setBannerUrl(String banner_url) {
        this.banner_url = banner_url;
    }

    public String getClickUrl() {
        return click_url;
    }

    public void setClickUrl(String click_url) {
        this.click_url = click_url;
    }

    public String getRevenueModel() {
        return revenue_model;
    }

    public void setRevenueModel(String revenue_model) {
        this.revenue_model = revenue_model;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<PubnativeBeacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<PubnativeBeacon> beacons) {
        this.beacons = beacons;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPortraitBannerUrl() {
        return portrait_banner_url;
    }

    public void setPortraitBannerUrl(String portrait_banner_url) {
        this.portrait_banner_url = portrait_banner_url;
    }

    /**
     * This function will return the Beacon URL on the bases of beacon type.
     * It will traverse all beacons and search for <code><beaconType<code/> beaconType.
     *
     * @param beaconType
     *
     * @return return Beacon URL or null otherwise.
     */
    public String getBeacon(String beaconType) {

        String beaconUrl = null;

        if (!TextUtils.isEmpty(beaconType)) {

            for (PubnativeBeacon beacon : beacons) {

                if (beaconType.equalsIgnoreCase(beacon.type)) {

                    beaconUrl = beacon.url;
                    break;
                }
            }
        }

        return beaconUrl;
    }

    public interface Listener {

        void onPubnativeAdModelImpressionConfirmed();
        void onPubnativeAdModelImpressionFailed(Exception exception);
        void onPubnativeAdModelClicked();
        void onPubnativeAdModelClickFailed(Exception exception);
    }

    public void startTracking(View view, Listener listener) {
        startTracking(view, view, listener);
    }

    public void startTracking(View view, View clickableView, Listener listener) {

        new PubnativeTracker(view, clickableView, listener);
    }

}
