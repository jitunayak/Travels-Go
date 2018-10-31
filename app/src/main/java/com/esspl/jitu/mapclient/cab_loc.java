package com.esspl.jitu.mapclient;

/**
 * Created by jitu on 3/18/18.
 */

public class cab_loc {
    double lat;
    double lng;

    public cab_loc(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }


}
