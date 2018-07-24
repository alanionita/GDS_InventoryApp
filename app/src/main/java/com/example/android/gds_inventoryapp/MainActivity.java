package com.example.android.gds_inventoryapp;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.gds_inventoryapp.Data.BikeContract.BikeEntry;
import com.example.android.gds_inventoryapp.Data.BikeDbHelper;

public class MainActivity extends AppCompatActivity {
    private BikeDbHelper bikeDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bikeDbHelper = new BikeDbHelper(this);
        queryDatabase();
    }

    private void queryDatabase() {
            BikeDbHelper dbHelper = new BikeDbHelper(this);

            // Create and/or open a database to read from it
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // define the projections
            String[] projection = {
                    BikeEntry._ID,
                    BikeEntry.COLUMN_MAKE,
                    BikeEntry.COLUMN_MODEL,
                    BikeEntry.COLUMN_PRICE,
            };

        // Perform this raw SQL query "SELECT * FROM bikes"
        // to get a Cursor that contains all rows from the bikes table.
            Cursor cursor = db.query(
                    BikeEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

        TextView displayView = findViewById(R.id.text_view_pet);

            try {
                int bikeCount = cursor.getCount();
                Resources res = getResources();
                String bikesFound = res.getQuantityString(
                        R.plurals.numberOfBikeAvailable, bikeCount, bikeCount
                );
                displayView.setText(bikesFound);

                displayView.append(
                        " | " + BikeEntry._ID + " | "
                                + BikeEntry.COLUMN_MAKE + " | "
                                + BikeEntry.COLUMN_MODEL + " | "
                                + BikeEntry.COLUMN_PRICE + " | "
                        + "\n");

                // Find column indices
                int idColumnIndex = cursor.getColumnIndex(BikeEntry._ID);
                int makeColumnIndex = cursor.getColumnIndex(BikeEntry.COLUMN_MAKE);
                int modelColumnIndex = cursor.getColumnIndex(BikeEntry.COLUMN_MODEL);
                int priceColumnIndex = cursor.getColumnIndex(BikeEntry.COLUMN_PRICE);

                // Run through all the cursor rows
                while (cursor.moveToNext()) {
                    int currentID = cursor.getInt(idColumnIndex);
                    String currentMake = cursor.getString(makeColumnIndex);
                    String currentModel = cursor.getString(modelColumnIndex);
                    String currentPrice = cursor.getString(priceColumnIndex);
                    displayView.append((
                            " | " + currentID + " | "
                                    + currentMake + " | "
                                    + currentModel + " | "
                                    + "£" + currentPrice
                                    + " | " + "\n"
                    ));
                }
            } finally {
                // Always close the cursor when you're done reading from it. This releases all its
                // resources and makes it invalid.
                cursor.close();
            }
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
                insertTestData();
                queryDatabase();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertTestData() {
        SQLiteDatabase db = bikeDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BikeEntry.COLUMN_MAKE, "Cinelli");
        values.put(BikeEntry.COLUMN_MODEL, "Vigorelli");
        values.put(BikeEntry.COLUMN_TYPE, BikeEntry.TYPE_FIXED);
        values.put(BikeEntry.COLUMN_PRICE, 1500);
        values.put(BikeEntry.COLUMN_QUANTITY, 30);
        values.put(BikeEntry.COLUMN_SUPPLIER, "GRUPPO SRL CINELLI");
        values.put(BikeEntry.COLUMN_SUPPLIER_PHONE, "+39 02 95244 1");

        Long newRowID = db.insert(BikeEntry.TABLE_NAME, null, values);
        Log.v("MainActivity", newRowID.toString());
    }
}
