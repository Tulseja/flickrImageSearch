package com.imagegallery.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageItem implements Parcelable {
    private String id;
    private String secret;
    private String server;
    private int farm;

    public String getId() {
        return id;
    }

    public String getSecret() {
        return secret;
    }

    public String getServer() {
        return server;
    }

    public int getFarm() {
        return farm;
    }

    protected ImageItem(Parcel in) {
        id = in.readString();
        secret = in.readString();
        server = in.readString();
        farm = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(secret);
        dest.writeString(server);
        dest.writeInt(farm);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ImageItem> CREATOR = new Parcelable.Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel in) {
            return new ImageItem(in);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };
}