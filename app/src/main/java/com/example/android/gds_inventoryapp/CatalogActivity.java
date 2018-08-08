package com.example.android.gds_inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.gds_inventoryapp.Data.BikeContract.BikeEntry;
import com.example.android.gds_inventoryapp.Data.BikeCursorAdapter;
import com.example.android.gds_inventoryapp.Data.BikeDbHelper;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private BikeDbHelper bikeDbHelper;
    private static final int BIKE_LOADER = 0;
    private BikeCursorAdapter bikeCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog_activity);

        // Create an instance of the cursor adaptor
        bikeCursorAdapter = new BikeCursorAdapter(this, null);

        // Find ListView and set the bikeCursorAdapter
        ListView bikeList = findViewById(R.id.list);
        bikeList.setEmptyView(findViewById(R.id.empty));
        bikeList.setAdapter(bikeCursorAdapter);

        // Find FAB
        FloatingActionButton fab = findViewById(R.id.fab);

        // Set onClick that triggers the editor activity
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Click listener: when clicking a list item trigger DetailsActivity
        bikeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to DetailsActivity
                Intent intent = new Intent(adapterView.getContext(), DetailsActivity.class);
                Uri currentBikeUri = ContentUris.withAppendedId(BikeEntry.CONTENT_URI, id);
                intent.setData(currentBikeUri);
                Log.i("detailsActivityTrigger", "Triggered");
                startActivity(intent);
            }
        });

        // Initialise the loader
        getLoaderManager().initLoader(BIKE_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Handles 'insert test data' menu option
            case R.id.insert_test_data:
                insertTestBikeData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void insertTestBikeData() {
        ContentValues values = new ContentValues();
        values.put(BikeEntry.COLUMN_MAKE, "Cinelli");
        values.put(BikeEntry.COLUMN_MODEL, "Vigorelli");
        values.put(BikeEntry.COLUMN_TYPE, BikeEntry.TYPE_FIXED);
        values.put(BikeEntry.COLUMN_PRICE, 1500);
        values.put(BikeEntry.COLUMN_QUANTITY, 30);
        values.put(BikeEntry.COLUMN_SUPPLIER, "GRUPPO SRL CINELLI");
        values.put(BikeEntry.COLUMN_SUPPLIER_PHONE, "+39 02 95244 1");

        Uri newUri = getContentResolver().insert(BikeEntry.CONTENT_URI, values);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define the projections
        String[] projection = {
                BikeEntry._ID,
                BikeEntry.COLUMN_MAKE,
                BikeEntry.COLUMN_MODEL,
                BikeEntry.COLUMN_PRICE,
                BikeEntry.COLUMN_QUANTITY
        };
        return new CursorLoader(this,
                BikeEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        bikeCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        bikeCursorAdapter.swapCursor(null);
    }
}
