package net.pubnative.library.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by daffodiliphone on 30/11/15.
 */
public class Vast implements Parcelable {
    @SerializedName("ad")
    public String mAd;

    @SerializedName("video_skip_time")
    public int mVideoSkipTime;

    @SerializedName("skip_video_button")
    public String mShipVideoButton;

    @SerializedName("mute")
    public String mMute;

    @SerializedName("learn_more_button")
    public String mLearnMoreButton;

    protected Vast(Parcel in) {
        mAd = in.readString();
        mVideoSkipTime = in.readInt();
        mShipVideoButton = in.readString();
        mMute = in.readString();
        mLearnMoreButton = in.readString();
    }

    public static final Creator<Vast> CREATOR = new Creator<Vast>() {
        @Override
        public Vast createFromParcel(Parcel in) {
            return new Vast(in);
        }

        @Override
        public Vast[] newArray(int size) {
            return new Vast[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mAd);
        parcel.writeInt(mVideoSkipTime);
        parcel.writeString(mShipVideoButton);
        parcel.writeString(mMute);
        parcel.writeString(mLearnMoreButton);
    }
}
