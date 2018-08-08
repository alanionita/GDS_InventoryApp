package com.example.android.gds_inventoryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.gds_inventoryapp.Data.BikeContract.BikeEntry;

import java.util.Arrays;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int BIKE_LOADER = 1;
    private Uri currentBikeUri;

    // Define the input views and buttons
    private TextView makeTextView;
    private TextView modelTextView;
    private TextView typeTextView;
    private TextView priceTextView;
    private TextView quantityTextView;
    private TextView supplierTextView;
    private TextView supplierPhoneTextView;
    private Button orderButton;
    private Button increaseQuantityButton;
    private Button decreaseQuantityButton;
    private EditText quantityIncrementEditText;
    private Button deleteButton;

    // List of bike types
    private List<String> bikeTypes;

    // Define global Resources
    private Resources res;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        // Assign Resources and Context
        res = getResources();
        context = getApplicationContext();

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
        orderButton = findViewById(R.id.details_order_button);
        increaseQuantityButton = findViewById(R.id.increase_quantity_button);
        decreaseQuantityButton = findViewById(R.id.decrease_quantity_button);
        quantityIncrementEditText = findViewById(R.id.quantity_increment);
        deleteButton = findViewById(R.id.details_delete_button);

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
            final Integer id = cursor.getInt(
                    cursor.getColumnIndex(
                            BikeEntry._ID));
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
            final Integer quantityData = cursor.getInt(
                            cursor.getColumnIndex(
                                    BikeEntry.COLUMN_QUANTITY));
            String supplierData = cursor.getString(
                    cursor.getColumnIndex(
                            BikeEntry.COLUMN_SUPPLIER));
            final String supplierPhoneData = cursor.getString(
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

            // Define order button click functionality
            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Check for permissions
                    if(ContextCompat.checkSelfPermission(
                        v.getContext(),
                            android.Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity)
                                v.getContext(),
                                new String[]{android.Manifest.permission.CALL_PHONE}, 0);
                    } else {
                        startActivity(
                                // Start a new intent in the relevant app
                                new Intent(
                                        Intent.ACTION_CALL,
                                        Uri.parse("tel:" + supplierPhoneData)));
                    }
                }
            });

            // Define the deleteButton functionality
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteConfirmationDialog();
                }
            });

            // Define the quantity increase and decrease functionality
            increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer increment;
                    // If EditText is empty default to 1
                    if (quantityIncrementEditText.getText().toString().isEmpty()) {
                        increment = 1;
                    } else {
                        increment = Integer.parseInt(
                                quantityIncrementEditText.getText().toString());
                    }

                    // Change the quantity in the database
                    changeQuantity(view, quantityData, id, context, increment,
                            Operator.PLUS);

                    // Build the formatted string
                    String formattedString = context.getString(
                            R.string.details_increase_quantity_toast_success, increment);

                    // Send some feedback about the action
                    Toast.makeText(context,
                            formattedString,
                            Toast.LENGTH_SHORT).show();
                }
            });

            decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer increment;

                    // If EditText is empty default to 1
                    if (quantityIncrementEditText.getText().toString().isEmpty()) {
                        increment = 1;
                    } else {
                        increment = Integer.parseInt(
                                quantityIncrementEditText.getText().toString());
                    }

                    // Quantity cannot be lower than 0
                    if (quantityData - increment >= 0) {
                        // Change the quantity in the database
                        changeQuantity(view, quantityData, id, context, increment,
                                Operator.MINUS);
                        // Build formatted string
                        String formattedString = context.getString(
                                R.string.details_decrease_quantity_toast_success,
                                increment);

                        // Give some positive feedback to the user
                        Toast.makeText(context,
                                formattedString,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Send fail notice
                        Toast.makeText(context,
                                R.string.details_decrease_quantity_toast_fail,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
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

    // Changes the quantity of the item in stock
    private void changeQuantity(View view, Integer quantity, int id, Context context,
                                int increment, Operator operation) {
        // Create a new ContentValues() object
        ContentValues values = new ContentValues();
        // Remove one item from the quantity when a sale is made
        values.put(BikeEntry.COLUMN_QUANTITY, calculate(operation, quantity, increment));
        view.getContext().getContentResolver().update(currentBikeUri, values, null, null);
    }

    private String calculate(Operator op, int a, int b) {
        return String.valueOf(op.apply(a, b));
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder, set the message, and click listeners
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.details_delete_message);
        builder.setPositiveButton(
                R.string.details_delete_positive_confirmation,
                new DialogInterface.OnClickListener() {
                    // User clicked the "Delete" button, so delete the bike
                    public void onClick(DialogInterface dialog, int id) {
                        deleteBike();
                    }
                });
        builder.setNegativeButton(
                R.string.details_delete_negative_confirmation,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked the "Cancel" button, so dismiss the dialog
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBike() {
        // Only perform the delete if this is an existing bike.
        if (currentBikeUri != null) {
            // Delete the bike via the contentResolver
            int rowsDeleted = getContentResolver().delete(
                    currentBikeUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, R.string.details_delete_fail_toast,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, R.string.details_delete_confirmation_toast,
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }
}
