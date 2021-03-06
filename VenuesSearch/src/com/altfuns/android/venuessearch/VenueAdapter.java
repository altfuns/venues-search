package com.altfuns.android.venuessearch;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.altfuns.android.venuessearch.bo.Venue;
import com.altfuns.android.venuessearch.core.ListBaseAdapter;

/**
 * Adapter the show the name and the distance of venues
 * @author altfuns
 *
 */
public class VenueAdapter extends ListBaseAdapter<Venue> {

    public VenueAdapter(Context context, List<Venue> items) {
        super(context, items);
    }

    @Override
    public long getItemId(int position) {
        if (this.items != null && this.items.size() > position) {
            return this.items.get(position).get_id();
        }
        return -1;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.venue_row;
    }

    @Override
    protected Object initViewHolder(View view) {
        ViewHolder holder = new ViewHolder();
        if (view != null) {
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.distance = (TextView) view.findViewById(R.id.distance);
        }
        return holder;
    }

    @Override
    protected void bindViewHolder(int position, View view, Object viewHolder,
            Venue item) {
        ViewHolder holder = (ViewHolder) viewHolder;

        holder.name.setText(item.getName());

        if (item.getDistance() != null) {
            double distance = item.getDistance() < 1000 ? item.getDistance()
                    : item.getDistance() / 1000;
            String measure = item.getDistance() < 1000 ? "m" : "km";
            holder.distance.setText(String.format("%s %s", distance, measure));
        }

    }

    private class ViewHolder {
        TextView name;

        TextView distance;
    }

}
