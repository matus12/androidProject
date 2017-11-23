package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieInfo implements Parcelable {
    private String original_title;
    private String vote_average;
    private String poster_path;
    private String release_date;

    public String getOverview() {
        return overview;
    }

    private String overview;

    public MovieInfo(Parcel in) {
        original_title = in.readString();
        vote_average = in.readString();
        poster_path = in.readString();
        release_date = in.readString();
        overview = in.readString();
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getOriginal_title () {
        return original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(original_title);
        dest.writeString(vote_average);
        dest.writeString(poster_path);
        dest.writeString(release_date);
        dest.writeString(overview);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieInfo> CREATOR = new Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };
}
