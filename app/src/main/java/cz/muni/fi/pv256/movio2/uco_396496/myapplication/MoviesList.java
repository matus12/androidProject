package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MoviesList implements Parcelable {
    private ArrayList<MovieInfo> results;

    public ArrayList<MovieInfo> getResults() {
        return results;
    }

    public MoviesList(Parcel in) {
        results = in.createTypedArrayList(MovieInfo.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeTypedList(results);
    }

    public static final Creator<MoviesList> CREATOR = new Creator<MoviesList>() {
        @Override
        public MoviesList createFromParcel(Parcel in) {
            return new MoviesList(in);
        }

        @Override
        public MoviesList[] newArray(int size) {
            return new MoviesList[size];
        }
    };
}
