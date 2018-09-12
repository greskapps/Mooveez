package com.greskapps.android.mooveez;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailers implements Parcelable {
    public static final Parcelable.Creator<Trailers> CREATOR = new Parcelable.Creator<Trailers>() {
        @Override
        public Trailers createFromParcel(Parcel output) {
            return new Trailers(output);
        }

        @Override
        public Trailers[] newArray(int size) {
            return new Trailers[size];

        }

    };
    private String movieID;
    private String trailerName;
    private String trailerType;

    public Trailers(String rv_movieId, String rv_trailerName, String rv_trailerType) {
        movieID = rv_movieId;
        trailerName = rv_trailerName;
        trailerType = rv_trailerType;
    }

    public Trailers(Parcel output) {
        movieID = output.readString();
        trailerName = output.readString();
        trailerType = output.readString();
    }

    public String getMovieID() {
        return movieID;
    }

    public String getTrailerName() {
        return trailerName;
    }

    public String getTrailerType() {
        return trailerType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int i) {
        destination.writeString(movieID);
        destination.writeString(trailerName);
        destination.writeString(trailerType);
    }
}




