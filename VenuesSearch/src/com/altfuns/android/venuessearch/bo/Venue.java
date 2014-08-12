package com.altfuns.android.venuessearch.bo;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

public class Venue {

    @SerializedName("_id")
    @DatabaseField(generatedId = true)
    protected long _id;

    @SerializedName("id")
    @DatabaseField(index = true)
    protected String id;

    @DatabaseField
    protected String name;

    @DatabaseField
    protected Integer distance = null;

    protected VenueLocation location;

    /**
     * @return the _id
     */
    public long get_id() {
        return _id;
    }

    /**
     * @param _id the _id to set
     */
    public void set_id(long _id) {
        this._id = _id;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
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

    public Integer getDistance() {
        if (distance == null && location != null) {
            this.distance = location.getDistance();
        }
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return String.format("id: %s, name : %s, location : %s", _id, name,
                location.toString());
    }
}
