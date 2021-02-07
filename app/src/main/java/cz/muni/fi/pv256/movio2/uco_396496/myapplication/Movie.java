package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by matus on 10/19/2017.
 */

public class Movie implements Parcelable {
    public Movie(long releaseDate, String backdrop, String coverPath, String title, Float popularity){
        this.popularity = popularity;
        this.releaseDate = releaseDate;
        this.backdrop = backdrop;
        this.coverPath = coverPath;
        this.title = title;
    }

    public long getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(long releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    private long releaseDate;
    private String coverPath;
    private String title;
    private String backdrop;
    private float popularity;

    protected Movie(Parcel in) {
        releaseDate = in.readLong();
        coverPath = in.readString();
        title = in.readString();
        backdrop = in.readString();
        popularity = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(releaseDate);
        dest.writeString(coverPath);
        dest.writeString(title);
        dest.writeString(backdrop);
        dest.writeFloat(popularity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
