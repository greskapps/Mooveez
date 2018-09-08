package com.greskapps.android.mooveez;

import android.os.Parcel;
import android.os.Parcelable;

public class Thumbnail implements Parcelable {

    public static final Parcelable.Creator<Thumbnail> CREATOR = new Parcelable.Creator<Thumbnail>() {
        @Override
        public Thumbnail createFromParcel(Parcel out) {
            return new Thumbnail(out);
        }

        @Override
        public Thumbnail[] newArray(int size) {
            return new Thumbnail[size];
        }
    };
    private String title;
    private String url;
    private String wideUrl;
    private String details;
    private String release_date;
    private float vote_average;
    private String movieId;

    public Thumbnail(String rec_title, String rec_url, String rec_wideUrl, String rec_details, String rec_release_date, float rec_vote_average, String rec_movie_id) {
        title = rec_title;
        url = rec_url;
        wideUrl = rec_wideUrl;
        details = rec_details;
        release_date = rec_release_date;
        vote_average = rec_vote_average;
        movieId = rec_movie_id;
    }

    private Thumbnail(Parcel output) {
        title = output.readString();
        url = output.readString();
        wideUrl = output.readString();
        details = output.readString();
        release_date = output.readString();
        vote_average = output.readFloat();
        movieId = output.readString();
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getwideUrl() {
        return wideUrl;
    }

    public String getDetails() {
        return details;
    }

    public String getRelease_date() {
        return release_date;
    }

    public float getVote_average() {
        return vote_average;
    }

    public String getmovieId() {
        return movieId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel output, int flags) {
        output.writeString(title);
        output.writeString(url);
        output.writeString(wideUrl);
        output.writeString(details);
        output.writeString(release_date);
        output.writeFloat(vote_average);
        output.writeString(movieId);
    }
}
