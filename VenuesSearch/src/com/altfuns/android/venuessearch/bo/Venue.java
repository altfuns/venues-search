package com.altfuns.android.venuessearch.bo;

import com.google.gson.annotations.SerializedName;

public class Venue {

    @SerializedName("id")
    protected String _id;
    
    protected String name;
    
    protected VenueLocation location;
    
    @Override
    public String toString() {
        return String.format("id: %s, name : %s, location : %s",  _id, name, location.toString());
    }
}
