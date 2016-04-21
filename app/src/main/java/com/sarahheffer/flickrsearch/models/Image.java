package com.sarahheffer.flickrsearch.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable {

    int farm;
    String server;
    String id;
    String secret;

    protected Image(Parcel in) {
        farm = in.readInt();
        server = in.readString();
        id = in.readString();
        secret = in.readString();
    }

    public int getFarm() {
        return farm;
    }

    public String getServer() {
        return server;
    }

    public String getId() {
        return id;
    }

    public String getSecret() {
        return secret;
    }

    private String getImageName() {
        return getId().concat("_").concat(getSecret()).concat(".jpg");
    }

    public String getImageURL() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("farm".concat(String.valueOf(getFarm())).concat(".static.flickr.com"))
                .appendPath(getServer())
                .appendPath(getImageName());
        return builder.build().toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(farm);
        dest.writeString(server);
        dest.writeString(id);
        dest.writeString(secret);
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
