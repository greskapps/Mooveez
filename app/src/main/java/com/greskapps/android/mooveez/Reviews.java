package com.greskapps.android.mooveez;

import android.os.Parcel;
import android.os.Parcelable;

public class Reviews implements Parcelable {
    public static final Parcelable.Creator<Reviews> CREATOR = new Parcelable.Creator<Reviews>() {
        @Override
        public Reviews createFromParcel(Parcel out) {
            return new Reviews(out);
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };
    private String author;
    private String content;
    private String url;

    public Reviews(String rec_author, String rec_content, String rec_url) {
        author = rec_author;
        content = rec_content;
        url = rec_url;
    }

    private Reviews(Parcel output) {
        author = output.readString();
        content = output.readString();
        url = output.readString();
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }

}
