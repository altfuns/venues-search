package com.altfuns.android.venuessearch;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

import com.altfuns.android.venuessearch.bo.Venue;

/**
 * A list fragment representing a list of Venues.
 */
public class VenueListFragment extends ListFragment {
    private VenueAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VenueListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new VenueAdapter(getActivity(), null);
        setListAdapter(adapter);
    }

    /**
     * Update the list view with the given venues
     * @param venues
     */
    public void updateVenues(List<Venue> venues) {
        adapter.updateItems(venues);

        if (adapter.getCount() == 0) {
            setEmptyText(getString(R.string.venues_not_found_message));
        }
    }

}
