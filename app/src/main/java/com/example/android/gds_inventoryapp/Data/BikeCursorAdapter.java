package com.example.android.gds_inventoryapp.Data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.gds_inventoryapp.Data.BikeContract.BikeEntry;
import com.example.android.gds_inventoryapp.R;

public class BikeCursorAdapter extends CursorAdapter {

    public BikeCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    // Creates a new view from the list_item
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    // Populates the views with the data
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find the required views
        TextView modelView = view.findViewById(R.id.model);
        TextView makeView = view.findViewById(R.id.make);
        TextView priceView = view.findViewById(R.id.price);
        TextView quantityView = view.findViewById(R.id.quantity);

        // Find the relevant data from the cursor
        String modelData = cursor.getString(cursor.getColumnIndex(BikeEntry.COLUMN_MODEL));
        String makeData = cursor.getString(cursor.getColumnIndex(BikeEntry.COLUMN_MAKE));
        Integer priceData = cursor.getInt(cursor.getColumnIndex(BikeEntry.COLUMN_PRICE));
        Integer quantityData = cursor.getInt(cursor.getColumnIndex(BikeEntry.COLUMN_QUANTITY));

        // Build the formatted strings
        String formatedMakeString = view.getResources().getString(R.string.made_by, makeData);
        String formatedQuantityString = view.getResources().getString(R.string.quantity_of, quantityData);
        String formatedPriceString = view.getResources().getString(R.string.price_of, priceData);

        // Bind the data to the TextViews
        modelView.setText(modelData);
        makeView.setText(formatedMakeString);
        priceView.setText(formatedPriceString);
        quantityView.setText(formatedQuantityString);
    }
}
