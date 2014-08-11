package com.altfuns.android.venuessearch;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;

import com.altfuns.android.venuessearch.core.LogIt;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/**
 * An activity representing a list of Venues. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link VenueDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link VenueListFragment} and the item details (if present) is a
 * {@link VenueDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link VenueListFragment.Callbacks} interface to listen for item selections.
 */
public class VenueListActivity extends FragmentActivity implements
        VenueListFragment.Callbacks, ConnectionCallbacks, LocationListener,
        OnConnectionFailedListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    //These settings are the same as the settings for the map. They will in fact give you updates at
    // the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000) // 5 seconds
            .setFastestInterval(16) // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private LocationClient locationClient;

    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_list);

        if (findViewById(R.id.venue_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((VenueListFragment) getSupportFragmentManager().findFragmentById(
                    R.id.venue_list)).setActivateOnItemClick(true);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.venue_list_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        return true;
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            LogIt.d(this, "Search key:" + query);
            // Replace the list fragment with a new fragment with query parameter
            replaceListFragment(query);
        }
    }

    private void replaceListFragment(String query) {
        VenueListFragment fragment = VenueListFragment.newInstance(query,
                this.location);
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        tr.replace(R.id.venue_list, fragment);
        tr.addToBackStack(null);
        tr.commit();
    }

    /**
     * Get the last location from the location client
     * @return
     */
    private Location getLocation() {
        return locationClient != null && locationClient.isConnected() ? locationClient
                .getLastLocation() : null;
    }

    /**
     * Callback method from {@link VenueListFragment.Callbacks} indicating that
     * the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(VenueDetailFragment.ARG_ITEM_ID, id);
            VenueDetailFragment fragment = new VenueDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.venue_detail_container, fragment).commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, VenueDetailActivity.class);
            detailIntent.putExtra(VenueDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpLocationClientIfNeeded();
        locationClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationClient != null) {
            locationClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

    }

    @Override
    public void onConnected(Bundle arg0) {
        locationClient.requestLocationUpdates(REQUEST, this); // LocationListener
        location = getLocation();
    }

    @Override
    public void onDisconnected() {
        // TODO Auto-generated method stub

    }

    /**
     * Set up the location client if the instance is null.
     */
    private void setUpLocationClientIfNeeded() {
        if (locationClient == null) {
            locationClient = new LocationClient(this, this, // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }

}
