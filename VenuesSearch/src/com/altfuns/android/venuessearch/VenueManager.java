package com.altfuns.android.venuessearch;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.location.Location;

import com.altfuns.android.venuessearch.bo.Venue;
import com.altfuns.android.venuessearch.bo.VenueSearchResult;
import com.altfuns.android.venuessearch.core.JsonUtil;
import com.altfuns.android.venuessearch.core.LogIt;
import com.altfuns.android.venuessearch.core.RestClientHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * A manager to handle the service and local database operations of the venues.
 * @author altfuns
 *
 */
public class VenueManager {

    /**
     * Use the foursquare venue search endpoint to get the venues near the given location and that match query parameter
     * @param context
     * @param location
     * @param query
     * @return
     * @throws IOException
     */
    public static VenueSearchResult search(Context context, Location location,
            String query) throws IOException {
        String response = RestClientHelper.get(buildVenueSearchQuery(context,
                location, query));
        return JsonUtil.fromJson(VenueSearchResult.class, response);
    }

    /**
     * Save the venues into the local database
     * @param venues
     */
    public static void saveVenues(List<Venue> venues) {
        try {
            Dao<Venue, Long> dao = VenuesSearchApp.getInstance().getHelper()
                    .getDao();
            for (Venue venue : venues) {
                // Load the distance field
                venue.getDistance();
                dao.createOrUpdate(venue);
            }
        } catch (SQLException e) {
            LogIt.e(VenueManager.class, e, e.getMessage());
        }
    }

    /**
     * Load the latest searched venues
     */
    public static List<Venue> loadLastSearch() {
        try {
            Dao<Venue, Long> dao = VenuesSearchApp.getInstance().getHelper()
                    .getDao();
            QueryBuilder<Venue, Long> builder = dao.queryBuilder();
            builder.orderBy("_id", false);
            builder.limit(10L);
            return builder.query();
        } catch (SQLException e) {
            LogIt.e(VenueManager.class, e, e.getMessage());
        }

        return null;
    }

    /**
     * Builds the venue search query string with the foursquare OAuth parameters, user location and the query term
     * @param context
     * @param location
     * @param query
     * @return
     */
    private static String buildVenueSearchQuery(Context context,
            Location location, String query) {
        return String.format(
                context.getString(R.string.foursquare_venues_search),
                context.getString(R.string.foursquare_client_id),
                context.getString(R.string.foursquare_client_secret),
                location.getLatitude(), location.getLongitude(), query);
    }

}
