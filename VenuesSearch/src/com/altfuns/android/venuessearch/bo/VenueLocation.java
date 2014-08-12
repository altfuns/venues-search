package com.altfuns.android.venuessearch.bo;

import com.google.android.gms.maps.model.LatLng;

public class VenueLocation {

    protected double lat;

    protected double lng;

    protected int distance;

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

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    @Override
    public String toString() {
        return String.format("lat: %s; lng: %s; distance: %s", lat, lng,
                distance);
    }
}
