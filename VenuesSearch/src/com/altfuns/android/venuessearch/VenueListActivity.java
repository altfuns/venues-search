package com.altfuns.android.venuessearch;

import java.io.IOException;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.Window;
import android.widget.SearchView;

import com.altfuns.android.venuessearch.bo.Venue;
import com.altfuns.android.venuessearch.bo.VenueSearchResult;
import com.altfuns.android.venuessearch.core.BackgroundTask;
import com.altfuns.android.venuessearch.core.LogIt;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/**
 * An activity representing a list of Venues. This activity has two tabs, 
 * one for the list of venues searched and the other one for view the venues of the map.
 * This activity also request the current location to filter the search results and get the distance between
 * the venue and the user.
 */
public class VenueListActivity extends FragmentActivity implements
        ConnectionCallbacks, LocationListener, OnConnectionFailedListener {

    //These settings are the same as the settings for the map. They will in fact give you updates at
    // the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000) // 5 seconds
            .setFastestInterval(16) // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private VenuePagerAdapter pagerAdapter;

    private ViewPager viewPager;

    private LocationClient locationClient;

    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_venue_list);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        pagerAdapter = new VenuePagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
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

    @Override
    public void onResume() {
        super.onResume();
        setUpLocationClientIfNeeded();
        locationClient.connect();

        // When it's offline load the latest search results
        if (!isOnline()) {
            loadLatestSearch();
        }
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

        if (pagerAdapter != null) {
            pagerAdapter.updateLocation(location);
        }
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
     * Handles the search intent
     * @param intent
     */
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            LogIt.d(this, "Search key:" + query);
            // Search for venues and load it into the fragments
            searchVenues(location, query);
        }
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
     * Set up the location client if the instance is null.
     */
    private void setUpLocationClientIfNeeded() {
        if (locationClient == null) {
            locationClient = new LocationClient(this, this, // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }

    /**
     * Load the latest searched venues
     */
    private void loadLatestSearch() {
        new BackgroundTask() {
            List<Venue> venues;

            @Override
            public void work() {
                venues = VenueManager.loadLastSearch();
            }

            @Override
            public void done() {
                pagerAdapter.updateVenues(venues);
            }
        };
    }

    /**
     * Search for venues on the FS API and display the result into list view
     * @param query
     */
    public void searchVenues(final Location location, final String query) {
        setProgressBarIndeterminateVisibility(true);
        new BackgroundTask() {
            VenueSearchResult searchResult = null;

            @Override
            public void work() {
                try {
                    searchResult = VenueManager.search(VenueListActivity.this,
                            location, query);
                    VenueManager.saveVenues(searchResult.getVenues());
                } catch (IOException e) {
                    LogIt.e(this, e, e.getMessage());
                }

            }

            @Override
            public void done() {
                if (searchResult != null) {
                    pagerAdapter.updateVenues(searchResult.getVenues());
                }
                setProgressBarIndeterminateVisibility(false);
            }
        };
    }

    /**
     * Checks the connectivity service for availability
     * @return
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

}
