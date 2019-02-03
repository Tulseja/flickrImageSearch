package com.imagegallery.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ImageListResponse implements Parcelable {

    private Photos photos;

    public Photos getPhotos() {
        return photos;
    }

    public class Photos {
        private ArrayList<ImageItem> photo;

        public ArrayList<ImageItem> getPhoto() {
            return photo;
        }

        private int perpage ;

        public int getPerPages(){
            return perpage ;
        }

    }

    protected ImageListResponse(Parcel in) {
        photos = (Photos) in.readValue(Photos.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(photos);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ImageListResponse> CREATOR = new Parcelable.Creator<ImageListResponse>() {
        @Override
        public ImageListResponse createFromParcel(Parcel in) {
            return new ImageListResponse(in);
        }

        @Override
        public ImageListResponse[] newArray(int size) {
            return new ImageListResponse[size];
        }
    };

}
