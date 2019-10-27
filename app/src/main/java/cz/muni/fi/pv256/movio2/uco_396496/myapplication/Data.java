package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import java.util.ArrayList;

public class Data {
    private static Data instance;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    private String data;

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    private String data2;

    private Data() {
    }

    public static Data getInstance() {
        if (instance == null) {
            instance = new Data();
        }
        return instance;
    }

    public ArrayList<MovieInfo> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<MovieInfo> movies) {
        this.movies = movies;
    }

    private ArrayList<MovieInfo> movies;

    public ArrayList<MovieInfo> getMoviesInTheaters() {
        return moviesInTheaters;
    }

    public void setMoviesInTheaters(ArrayList<MovieInfo> moviesInTheaters) {
        this.moviesInTheaters = moviesInTheaters;
    }

    private ArrayList<MovieInfo> moviesInTheaters;
}
