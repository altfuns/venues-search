package com.altfuns.android.venuessearch;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.os.Bundle;

import com.altfuns.android.venuessearch.bo.Venue;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A map fragment representing the location of each Venue.
 */
public class VenueMapFragment extends SupportMapFragment {

    private Location location;

    private Marker locationMarker;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VenueMapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (super.getMap() != null) {
            try {
                MapsInitializer.initialize(getActivity());
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Add the venues markers
     * @param venues
     */
    public void updateVenues(List<Venue> venues) {
        List<LatLng> bounds = new ArrayList<LatLng>(venues.size());
        for (Venue venue : venues) {
            if (venue.getLocation() != null) {
                addMarker(venue.getLocation().getLatLng(),
                        R.drawable.venue_location_ico, false);
                bounds.add(venue.getLocation().getLatLng());
            }
        }

        if (location != null) {
            bounds.add(new LatLng(location.getLatitude(), location
                    .getLongitude()));
        }
        centerOnBounds(bounds);
    }

    /**
     * Add a marker for the current location
     * @param location
     */
    public void updateLocation(Location location) {
        this.location = location;
        LatLng position = new LatLng(location.getLatitude(),
                location.getLongitude());
        if (locationMarker == null) {
            locationMarker = addMarker(position, R.drawable.check_in_ico_on,
                    true);
        } else {
            locationMarker.setPosition(position);
        }
    }

    /**
     * Add a market to the map.
     * @param location
     * @param iconResourceId
     * @param centerOnMarker Indicate if the camera should be center on the marker.
     * @return
     */
    private Marker addMarker(LatLng location, int iconResourceId,
            boolean centerOnMarker) {
        Marker result = null;
        if (location != null) {
            result = super.getMap().addMarker(
                    new MarkerOptions()
                            .position(location)
                            .title("")
                            .snippet("")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(iconResourceId)));
            if (centerOnMarker) {
                centerOnMarker(location);
            }

        }
        return result;
    }

    /**
     * Centers the camera on the given location.
     * @param location
     */
    private void centerOnMarker(LatLng location) {
        if (location != null) {
            CameraUpdate center = CameraUpdateFactory.newLatLng(location);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

            super.getMap().moveCamera(center);
            super.getMap().animateCamera(zoom);
        }
    }

    /**
     * Center the map camera to fit all the location bounds.
     * @param bounds
     */
    private void centerOnBounds(List<LatLng> bounds) {
        if (bounds != null && bounds.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng bound : bounds) {
                if (bound != null) {
                    builder.include(bound);
                }
            }

            super.getMap().animateCamera(
                    CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        }
    }

}
