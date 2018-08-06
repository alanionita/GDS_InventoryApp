package com.example.android.gds_inventoryapp.Data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.gds_inventoryapp.Data.BikeContract.BikeEntry;
import com.example.android.gds_inventoryapp.R;

public class BikeCursorAdapter extends CursorAdapter {

    private Uri currentBikeUri;

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
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find the required views
        TextView modelView = view.findViewById(R.id.model);
        TextView makeView = view.findViewById(R.id.make);
        TextView priceView = view.findViewById(R.id.price);
        TextView quantityView = view.findViewById(R.id.quantity);
        Button saleButton = view.findViewById(R.id.sale_button);


        // Find the relevant data from the cursor
        final int id = cursor.getInt(cursor.getColumnIndex(BikeEntry._ID));
        final String modelData = cursor.getString(cursor.getColumnIndex(BikeEntry.COLUMN_MODEL));
        final String makeData = cursor.getString(cursor.getColumnIndex(BikeEntry.COLUMN_MAKE));
        final Integer priceData = cursor.getInt(cursor.getColumnIndex(BikeEntry.COLUMN_PRICE));
        final Integer quantityData = cursor.getInt(cursor.getColumnIndex(BikeEntry.COLUMN_QUANTITY));

        // Build the formatted strings
        String formattedMakeString = view.getResources().getString(R.string.made_by, makeData);
        String formattedQuantityString = view.getResources().getString(R.string.quantity_of_long, quantityData);
        String formattedPriceString = view.getResources().getString(R.string.price_of, priceData);

        // Bind the data to the TextViews
        modelView.setText(modelData);
        makeView.setText(formattedMakeString);
        priceView.setText(formattedPriceString);
        quantityView.setText(formattedQuantityString);
        saleButton.setText(R.string.sale_button_text);

        // Bind the click listener to the sale button
        saleButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                sellABike(v);
            }

            private void sellABike(View view) {
                // Create a new ContentValues() object
                ContentValues values = new ContentValues();
                // Append the content Uri with the id;
                currentBikeUri = ContentUris.withAppendedId(BikeEntry.CONTENT_URI, id);

                // Don't allow for quantityData to be less than 0
                if (quantityData > 0) {
                    // Remove one item from the quantity when a sale is made
                    values.put(BikeEntry.COLUMN_QUANTITY, quantityData - 1);
                    view.getContext().getContentResolver().update(currentBikeUri, values, null, null);
                    Toast.makeText(context, context.getString(R.string.sold_one_bike),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.cant_sell_less_than_zero),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
