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

        // Find the relevant data from the cursor
        String modelData = cursor.getString(cursor.getColumnIndex(BikeEntry.COLUMN_MODEL));
        String makeData = cursor.getString(cursor.getColumnIndex(BikeEntry.COLUMN_MAKE));

        // Bind the data to the TextViews
        modelView.setText(modelData);
        makeView.setText(makeData);
    }
}
