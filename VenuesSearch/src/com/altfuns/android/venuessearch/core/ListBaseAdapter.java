package com.altfuns.android.venuessearch.core;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * ListBaseAdapter is the abstract to support the base function of a List Adapter.
 *
 * @param <T> Class of adapter data source.
 */
public abstract class ListBaseAdapter<T> extends BaseAdapter {

    protected List<T> items;

    protected LayoutInflater inflater;

    protected Context context;

    public ListBaseAdapter(Context context, List<T> items) {
        this.items = items;

        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items != null? items.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return items != null && items.size() > position ? items.get(position)
                : null;
    }

    /**
     * Get the view layout id.
     * @return
     */
    protected abstract int getLayoutResource();

    /**
     * Initialize the view holder instance with view components.
     * @return
     */
    protected abstract Object initViewHolder(View view);

    /**
     * Bind the view holder with the item properties.
     * @param view
     * @param viewHolder
     * @param item
     */
    protected abstract void bindViewHolder(int position, View view,
            Object viewHolder, T item);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object holder = null;

        // Create the view holder with the view components.
        if (convertView == null) {
            convertView = inflater.inflate(getLayoutResource(), parent, false);
            holder = initViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = convertView.getTag();
        }

        T item = getItem(position);

        bindViewHolder(position, convertView, holder, item);

        return convertView;
    }

    /**
     * Update the items data source.
     * @param items
     */
    public void updateItems(List<T> items) {
        this.items = items;
        this.notifyDataSetChanged();
    }
    
    /**
     * Build a single view using the row layout and view holder
     * @param item
     * @param parent
     * @return
     */
    public View buildView(T item, ViewGroup parent){
    	View convertView = inflater.inflate(getLayoutResource(), parent, false);
    	Object holder = initViewHolder(convertView);
    	bindViewHolder(0, convertView, holder, item);
    	return convertView;
    }
}
