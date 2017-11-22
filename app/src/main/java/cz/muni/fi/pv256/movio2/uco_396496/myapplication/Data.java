package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

/**
 * Created by matus on 11/22/2017.
 */

public class Data {
    private static Data instance;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    private String data;
    private Data(){}

    public static Data getInstance() {
        if(instance == null) {
            instance = new Data();
        }
        return instance;
    }
}
