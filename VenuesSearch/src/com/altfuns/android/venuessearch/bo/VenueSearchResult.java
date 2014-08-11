package com.altfuns.android.venuessearch.bo;

import java.util.List;

public class VenueSearchResult {

    private class Meta {
        public String code;
    }

    private class Response {
        public List<Venue> venues;
    }

    protected Meta meta;

    protected Response response;

    public List<Venue> getVenues() {
        return response.venues;
    }
}
