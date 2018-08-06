package com.example.android.gds_inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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
        setContentView(R.layout.activity_main);

        // Create an instance of the cursor adaptor
        bikeCursorAdapter = new BikeCursorAdapter(this, null);

        // Find ListView and set the bikeCursorAdapter
        ListView bikeList = findViewById(R.id.list);
        bikeList.setEmptyView(findViewById(R.id.empty));
        bikeList.setAdapter(bikeCursorAdapter);

        // Initialise the loader
        getSupportLoaderManager().initLoader(BIKE_LOADER, null, this);

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
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
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
