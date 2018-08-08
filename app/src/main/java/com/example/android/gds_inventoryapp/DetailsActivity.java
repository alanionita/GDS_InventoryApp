package com.example.android.gds_inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.android.gds_inventoryapp.Data.BikeContract.BikeEntry;

import java.util.Arrays;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int BIKE_LOADER = 1;
    private Uri currentBikeUri;

    // Define the input views
    private TextView makeTextView;
    private TextView modelTextView;
    private TextView typeTextView;
    private TextView priceTextView;
    private TextView quantityTextView;
    private TextView supplierTextView;
    private TextView supplierPhoneTextView;

    // List of bike types
    private List<String> bikeTypes;

    // Define global Resources
    private Resources res;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        // Assign Resources
        res = getResources();

        // Extract the date from the Uri
        Intent intent = getIntent();
        currentBikeUri = intent.getData();

        // Find the views and assign them to global holders
        makeTextView = findViewById(R.id.details_content_make);
        modelTextView = findViewById(R.id.details_content_model);
        typeTextView = findViewById(R.id.details_content_type);
        priceTextView = findViewById(R.id.details_content_price);
        quantityTextView = findViewById(R.id.details_content_quantity);
        supplierTextView = findViewById(R.id.details_content_supplier);
        supplierPhoneTextView = findViewById(R.id.details_content_supplier_number);

        // Populate the list of bike types
        bikeTypes = Arrays.asList(getResources().getStringArray(R.array.bike_type_options));

        // Initialise loader
        getLoaderManager().initLoader(BIKE_LOADER, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // define projection
        String[] projection = {
                BikeEntry._ID,
                BikeEntry.COLUMN_MODEL,
                BikeEntry.COLUMN_MAKE,
                BikeEntry.COLUMN_TYPE,
                BikeEntry.COLUMN_PRICE,
                BikeEntry.COLUMN_QUANTITY,
                BikeEntry.COLUMN_SUPPLIER,
                BikeEntry.COLUMN_SUPPLIER_PHONE,
        };

        // Execute the contentProvider query
        return new CursorLoader(this,
                currentBikeUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Early exit is the cursor is empty
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Go to the first row and start reading from cursor
        if (cursor.moveToFirst()) {
            // Extract out the value from the Cursor for the given column index
            String makeData = cursor.getString(
                    cursor.getColumnIndex(
                            BikeEntry.COLUMN_MAKE));
            String modelData = cursor.getString(
                    cursor.getColumnIndex(
                            BikeEntry.COLUMN_MODEL));
            Integer typeData = cursor.getInt(
                    cursor.getColumnIndex(
                            BikeEntry.COLUMN_TYPE));
            Integer priceData = cursor.getInt(
                            cursor.getColumnIndex(
                                    BikeEntry.COLUMN_PRICE));
            Integer quantityData = cursor.getInt(
                            cursor.getColumnIndex(
                                    BikeEntry.COLUMN_QUANTITY));
            String supplierData = cursor.getString(
                    cursor.getColumnIndex(
                            BikeEntry.COLUMN_SUPPLIER));
            String supplierPhoneData = cursor.getString(
                    cursor.getColumnIndex(
                            BikeEntry.COLUMN_SUPPLIER_PHONE));

            // Get string from bikeTypeList using typeData
            String bikeTypeString = bikeTypes.get(typeData);

            // Generate formatted strings
            String priceFormattedString = res.getString(
                    R.string.details_price_content, priceData);
            String quantityFormattedString = res.getString(
                    R.string.details_quantity_content, quantityData);

            // Edit title
            setTitle(getString(R.string.details_activity_title));

            // Update the views on the screen with the values from the database
            makeTextView.setText(makeData);
            modelTextView.setText(modelData);
            typeTextView.setText(bikeTypeString);
            priceTextView.setText(priceFormattedString);
            quantityTextView.setText(quantityFormattedString);
            supplierTextView.setText(supplierData);
            supplierPhoneTextView.setText(supplierPhoneData);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        makeTextView.setText("");
        modelTextView.setText("");
        typeTextView.setText("");
        priceTextView.setText("");
        quantityTextView.setText("");
        supplierTextView.setText("");
        supplierPhoneTextView.setText("");
    }
}
