package com.altfuns.android.venuessearch;

import java.util.List;

import com.altfuns.android.venuessearch.bo.Venue;

import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * An adapter to show the list fragment or the map fragment
 * @author altfuns
 *
 */
public class VenuePagerAdapter extends FragmentStatePagerAdapter {

    private VenueListFragment listFragment = new VenueListFragment();

    private VenueMapFragment mapFragment = new VenueMapFragment();

    public VenuePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return listFragment;
        }

        return mapFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return position == 0 ? "List" : "Map";
    }

    public void updateVenues(List<Venue> venues) {
        listFragment.updateVenues(venues);
        mapFragment.updateVenues(venues);
    }

    public void updateLocation(Location location) {
        mapFragment.updateLocation(location);
    }
}
