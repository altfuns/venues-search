package com.altfuns.android.venuessearch.bo;

public class VenueLocation {

    protected double lat;

    protected double lng;

    protected int distance;

    @Override
    public String toString() {
        return String.format("lat: %s; lng: %s; distance: %s", lat, lng,
                distance);
    }
}
