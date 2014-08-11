package com.altfuns.android.venuessearch.bo;

import com.google.gson.annotations.SerializedName;

public class Venue {

    @SerializedName("_id")
    protected long id;

    @SerializedName("id")
    protected String _id;

    protected String name;

    protected VenueLocation location;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VenueLocation getLocation() {
        return location;
    }

    public void setLocation(VenueLocation location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return String.format("id: %s, name : %s, location : %s", _id, name,
                location.toString());
    }
}
