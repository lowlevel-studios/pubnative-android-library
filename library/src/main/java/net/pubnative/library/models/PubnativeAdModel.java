package net.pubnative.library.models;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import net.pubnative.library.tracking.PubnativeAdTracker;

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
     * @param beaconType type of beacon
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

    /**
     * Interface definition for callbacks to be invoked when impression confirmed/failed, ad clicked/clickfailed
     */
    public interface Listener {

        /**
         * Called when impression is confirmed
         * @param view The view where impression confirmed
         */
        void onPubnativeAdModelImpressionConfirmed(View view);

        /**
         * Called when error occurred while impression check
         * @param exception error
         */
        void onPubnativeAdModelImpressionFailed(Exception exception);

        /**
         * Called when click is confirmed
         * @param view The view that was clicked
         */
        void onPubnativeAdModelClicked(View view);

        /**
         * Called when error occurred while click
         * @param exception error
         */
        void onPubnativeAdModelClickFailed(Exception exception);
    }

    /**
     * Start tracking of ad view
     * @param view ad view
     * @param listener listener for callbacks
     */
    public void startTracking(View view, Listener listener) {

        startTracking(view, view, listener);
    }

    private transient PubnativeAdTracker mPubnativeAdTracker;

    /**
     * start tracking of your ad view
     * @param view ad view
     * @param clickableView clickable view
     * @param listener listener for callbacks
     */
    public void startTracking(View view, View clickableView, Listener listener) {

        Log.v(TAG, "startTracking(view, clickableView, listener)");

        mPubnativeAdTracker = new PubnativeAdTracker(view, clickableView, listener, this);
    }

    /**
     * stop tracking of ad view
     */
    public void stopTracking() {

        Log.v(TAG, "stopTracking()");

        mPubnativeAdTracker.stopTracking();
    }

}
