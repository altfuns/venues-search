package com.altfuns.android.venuessearch;

import java.io.IOException;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;

import com.altfuns.android.venuessearch.bo.Venue;
import com.altfuns.android.venuessearch.bo.VenueSearchResult;
import com.altfuns.android.venuessearch.core.BackgroundTask;
import com.altfuns.android.venuessearch.core.JsonUtil;
import com.altfuns.android.venuessearch.core.LogIt;
import com.altfuns.android.venuessearch.core.RestClientHelper;

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
        VenueListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

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

        // TODO: If exposing deep links into your app, handle intents here.
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

    public void loadVenues(final String query) {
        new BackgroundTask() {

            @Override
            public void work() {
                try {
                    String response = RestClientHelper
                            .get("https://api.foursquare.com/v2/venues/search?client_id=L4QT3R4MUN1PS1VRAZVXV2I0XDCI2QYNLXFDXJJIV4XVMZ50&client_secret=V0FYNJNTEUHEOOSYRZYUOTIQSWJACXGBHXRO1VW0LVHINI05&v=201400810&ll=10.0089857,-84.1370408&query="
                                    + query);
                    VenueSearchResult result = JsonUtil.fromJson(
                            VenueSearchResult.class, response);
                    for (Venue venue : result.getVenues()) {
                        LogIt.d(this, venue.toString());
                    }
                    LogIt.d(this, result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void done() {
                // TODO Auto-generated method stub

            }
        };
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            LogIt.d(this, "Search key:" + query);
            loadVenues(query);
        }
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
}
