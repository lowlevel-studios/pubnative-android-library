package net.pubnative.library.model;

import net.pubnative.library.PubnativeContract.Response.VideoNativeAd;

import org.droidparts.annotation.serialize.JSON;

public class VideoAdModel extends NativeAdModel implements VideoNativeAd {

    /**
     *
     */
    private static final long serialVersionUID = 2L;
    //
    // FIELDS
    //
    @JSON(key = VAST)
    VastAdModel[] vast;

    public VastAdModel getVastAdModel(int index) {

        VastAdModel result = null;

        if (this.vast.length > index) {

            result = this.vast[index];
        }
        return result;
    }

    public int getVideoSkipTime() {

        int         result = -1;
        VastAdModel model  = this.getVastAdModel(0);

        if (model != null) {

            result = model.video_skip_time;
        }
        return result;
    }

    public String getSkipVideoButton() {

        String      result = "";
        VastAdModel model  = this.getVastAdModel(0);

        if (model != null) {

            result = model.skip_video_button;
        }
        return result;
    }

    public String getMuteString() {

        String      result = "";
        VastAdModel model  = this.getVastAdModel(0);

        if (model != null) {

            result = model.mute;
        }
        return result;
    }

    public String getLearnMoreButton() {

        String      result = "";
        VastAdModel model  = this.getVastAdModel(0);

        if (model != null) {

            result = model.learn_more_button;
        }
        return result;
    }
}
