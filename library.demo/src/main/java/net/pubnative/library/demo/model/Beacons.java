package net.pubnative.library.demo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by daffodiliphone on 27/11/15.
 */
public class Beacons implements Parcelable{
    @SerializedName("type")
    public String mType;

    @SerializedName("url")
    public String mUrl;

    protected Beacons(Parcel in) {
        mType = in.readString();
        mUrl = in.readString();
    }

    public static final Creator<Beacons> CREATOR = new Creator<Beacons>() {
        @Override
        public Beacons createFromParcel(Parcel in) {
            return new Beacons(in);
        }

        @Override
        public Beacons[] newArray(int size) {
            return new Beacons[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mType);
        parcel.writeString(mUrl);
    }
}
