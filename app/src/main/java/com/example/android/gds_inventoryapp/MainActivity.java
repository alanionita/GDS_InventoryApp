package com.example.android.gds_inventoryapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.gds_inventoryapp.Data.BikeContract;
import com.example.android.gds_inventoryapp.Data.BikeDbHelper;
import com.example.android.gds_inventoryapp.Data.BikeContract.BikeEntry;

public class MainActivity extends AppCompatActivity {
    private BikeDbHelper bikeDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        bikeDbHelper = new BikeDbHelper(this);
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
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

            // Perform this raw SQL query "SELECT * FROM pets"
            // to get a Cursor that contains all rows from the pets table.
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
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);

            try {
                // Create a header in the Text View that looks like this:
                //
                // The pets table contains <number of rows in Cursor> pets.
                // _id - name - breed - gender - weight
                //
                // In the while loop below, iterate through the rows of the cursor and display
                // the information from each column in this order.
                displayView.setText("The bikes table contains " + cursor.getCount() + " pets.\n\n");
                displayView.append(BikeEntry._ID + " - "
                        + BikeEntry.COLUMN_MAKE + " - "
                        + BikeEntry.COLUMN_MODEL + " - "
                        + BikeEntry.COLUMN_PRICE
                        + "\n");

                // Figure out the index of each column
                int idColumnIndex = cursor.getColumnIndex(BikeEntry._ID);
                int makeColumnIndex = cursor.getColumnIndex(BikeEntry.COLUMN_MAKE);
                int modelColumnIndex = cursor.getColumnIndex(BikeEntry.COLUMN_MODEL);
                int priceColumnIndex = cursor.getColumnIndex(BikeEntry.COLUMN_PRICE);

                // Iterate through all the returned rows in the cursor
                while (cursor.moveToNext()) {
                    // Use that index to extract the String or Int value of the word
                    // at the current row the cursor is on.
                    int currentID = cursor.getInt(idColumnIndex);
                    String currentMake = cursor.getString(makeColumnIndex);
                    String currentModel = cursor.getString(modelColumnIndex);
                    String currentPrice = cursor.getString(priceColumnIndex);
                    // Display the values from each column of the current row in the cursor in the TextView
                    displayView.append((
                            "\n" + currentID + " - "
                                    + currentMake + " - "
                                    + currentModel + " - "
                                    + currentPrice
                    ));
                }
            } finally {
                // Always close the cursor when you're done reading from it. This releases all its
                // resources and makes it invalid.
                cursor.close();
            }
        }
    }
