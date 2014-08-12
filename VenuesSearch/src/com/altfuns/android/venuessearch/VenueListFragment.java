package com.altfuns.android.venuessearch;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.altfuns.android.venuessearch.bo.Venue;
import com.altfuns.android.venuessearch.bo.VenueSearchResult;
import com.altfuns.android.venuessearch.core.BackgroundTask;
import com.altfuns.android.venuessearch.core.JsonUtil;
import com.altfuns.android.venuessearch.core.LogIt;
import com.altfuns.android.venuessearch.core.RestClientHelper;
import com.altfuns.android.venuessearch.dummy.DummyContent;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * A list fragment representing a list of Venues. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link VenueDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class VenueListFragment extends ListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * Query to search for in the FS API
     */
    private String query;

    /**
     * Current location of the user to filter the venues query
     */
    private Location location;

    private VenueAdapter adapter;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    /**
     * Creates a new instance of the fragment
     * @param query
     * @return
     */
    public static VenueListFragment newInstance(String query, Location location) {
        VenueListFragment result = new VenueListFragment();
        result.setQuery(query);
        result.setLocation(location);
        return result;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VenueListFragment() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Search for venues on the FS API and display the result into list view
     * @param query
     */
    public void loadVenues(final String query) {
        if (TextUtils.isEmpty(query)) {
            loadVenuesFromDatabase();
            return;
        }

        setListShown(false);
        new BackgroundTask() {
            VenueSearchResult queryResult = null;

            @Override
            public void work() {
                try {
                    if (location == null) {
                        fail(getString(R.string.acquiring_current_position));
                        return;
                    }
                    String response = RestClientHelper
                            .get(buildVenueSearchQuery());
                    queryResult = JsonUtil.fromJson(VenueSearchResult.class,
                            response);
                    saveVenues(queryResult.getVenues());
                    LogIt.d(this, response);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void done() {
                if (failed()) {
                    setEmptyText(getExceptionMessage());
                    return;
                }
                adapter.updateItems(queryResult.getVenues());
                setListShown(true);

                if (adapter.getCount() == 0) {
                    setEmptyText(getString(R.string.venues_not_found_message));
                }
            }
        };
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState
                    .getInt(STATE_ACTIVATED_POSITION));
        }

        adapter = new VenueAdapter(getActivity(), null);
        setListAdapter(adapter);
        loadVenues(query);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position,
            long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(
                activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
                        : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    /**
     * Builds the venue search query string with the foursquare OAuth parameters, user location and the query term
     * @return
     */
    private String buildVenueSearchQuery() {
        return String.format(getString(R.string.foursquare_venues_search),
                getString(R.string.foursquare_client_id),
                getString(R.string.foursquare_client_secret),
                location.getLatitude(), location.getLongitude(), query);
    }

    /**
     * Save the venues into the local database
     * @param venues
     */
    private void saveVenues(List<Venue> venues) {
        try {
            Dao<Venue, Long> dao = VenuesSearchApp.getInstance().getHelper()
                    .getDao();
            for (Venue venue : venues) {
                // Load the distance field
                venue.getDistance();
                int id = dao.create(venue);
                LogIt.d(this, "id:" + id);
            }
        } catch (SQLException e) {
            LogIt.e(this, e, e.getMessage());
        }
    }

    /**
     * Load all the venues from the local database
     */
    private void loadVenuesFromDatabase() {
        try {
            Dao<Venue, Long> dao = VenuesSearchApp.getInstance().getHelper()
                    .getDao();
            QueryBuilder<Venue, Long> builder = dao.queryBuilder();
            builder.orderBy("_id", false);
            builder.limit(10L);
            List<Venue> venues = builder.query();
            adapter.updateItems(venues);
            setListShown(true);
        } catch (SQLException e) {
            LogIt.e(this, e, e.getMessage());
        }
    }
}
