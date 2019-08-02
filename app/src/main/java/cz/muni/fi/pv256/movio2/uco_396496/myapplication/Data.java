package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import com.squareup.picasso.RequestCreator;

import java.util.List;

public class Data {
    private static Data instance;

    public List<RequestCreator> getData() {
        return data;
    }

    public void setData(List<RequestCreator> data) {
        this.data = data;
    }

    private List<RequestCreator> data;

    public RequestCreator getDefaultCreator() {
        return defaultCreator;
    }

    public void setDefaultCreator(RequestCreator defaultCreator) {
        this.defaultCreator = defaultCreator;
    }

    private RequestCreator defaultCreator;

    private Data() {}

    public static Data getInstance() {
        if (instance == null) {
            instance = new Data();
        }
        return instance;
    }

}
