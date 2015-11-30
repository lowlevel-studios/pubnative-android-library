package net.pubnative.library.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by daffodiliphone on 27/11/15.
 */
public class PubNativeAdModel implements Parcelable{
    @SerializedName("title")
    public String mTitle;

    @SerializedName("description")
    public String mDescription;

    @SerializedName("cta_text")
    public String mCtaText;

    @SerializedName("icon_url")
    public String mIconUrl;

    @SerializedName("banner_url")
    public String mBannerUrl;

    @SerializedName("click_url")
    public String mClickUrl;

    @SerializedName("revenue_model")
    public String mRevenueModel;

    @SerializedName("points")
    public int mPoints;

    @SerializedName("beacons")
    public ArrayList<Beacons> mBeacons;

    @SerializedName("type")
    public String mType;

    @SerializedName("portrait_banner_url")
    public String mPortraitBannerUrl;

    @SerializedName("vast")
    public ArrayList<Vast> mVast;


    protected PubNativeAdModel(Parcel in) {
        mTitle = in.readString();
        mDescription = in.readString();
        mCtaText = in.readString();
        mIconUrl = in.readString();
        mBannerUrl = in.readString();
        mClickUrl = in.readString();
        mRevenueModel = in.readString();
        mPoints = in.readInt();
        mBeacons = in.createTypedArrayList(Beacons.CREATOR);
        mType = in.readString();
        mPortraitBannerUrl = in.readString();
       // mVast = in.createTypedArrayList(Vast.CREATOR);
    }

    public static final Creator<PubNativeAdModel> CREATOR = new Creator<PubNativeAdModel>() {
        @Override
        public PubNativeAdModel createFromParcel(Parcel in) {
            return new PubNativeAdModel(in);
        }

        @Override
        public PubNativeAdModel[] newArray(int size) {
            return new PubNativeAdModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mDescription);
        parcel.writeString(mCtaText);
        parcel.writeString(mIconUrl);
        parcel.writeString(mBannerUrl);
        parcel.writeString(mClickUrl);
        parcel.writeString(mRevenueModel);
        parcel.writeInt(mPoints);
        parcel.writeTypedList(mBeacons);
        parcel.writeString(mType);
        parcel.writeString(mPortraitBannerUrl);
        //parcel.writeTypedList(mVast);
    }
}
