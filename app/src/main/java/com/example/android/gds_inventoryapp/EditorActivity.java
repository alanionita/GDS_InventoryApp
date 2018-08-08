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
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.gds_inventoryapp.Data.BikeContract;
import com.example.android.gds_inventoryapp.Data.BikeContract.BikeEntry;

import java.util.Arrays;
import java.util.List;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for Bike data loader
    private static final int EXISTING_BIKE_LOADER = 0;
    // Set default selection for bike type
    private int bikeTypeDefault = BikeContract.BikeEntry.TYPE_UNKNOWN;
    // Content Uri for current bike
    private Uri currentBikeUri;

    // Define the input views
    private EditText makeEditText;
    private EditText modelEditText;
    private EditText priceEditText;
    private EditText quantityEditText;
    private EditText supplierEditText;
    private EditText supplierPhoneEditText;
    private AutoCompleteTextView bikeTypeAutoCompleteTextView;
    private int bikeType = BikeEntry.TYPE_UNKNOWN;

    // view was changed
    private boolean viewHasChanged = false;

    // touch listener that checks is any view was touched
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            viewHasChanged = true;
            return false;
        }
    };

    // Define global Resources
    private Resources res;
    private Context context;

    // List of bike types
    private List<String> bikeTypes;

    // Hides the softkeyboard, useful when dealing with view of different inputs
    // eg. AutoCompleteTextView
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null &&
                activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);

        // Extract the data from the Uri
        Intent intent = getIntent();
        currentBikeUri = intent.getData();

        // Initiate the res variables
        res = getResources();
        context = getApplicationContext();

        // Populate the list of bike types
        bikeTypes = Arrays.asList(getResources().getStringArray(R.array.bike_type_options));

        // Depending on the currentBikeUri change the activity title
        if (currentBikeUri == null) {
            setTitle(R.string.editor_activity_title_add);
        } else {
            setTitle(R.string.editor_activity_title_edit);

            // Initialize a loader to read from db
            getLoaderManager().initLoader(EXISTING_BIKE_LOADER, null, this);
        }

        // Find all of the required view


        makeEditText = findViewById(R.id.editor_make_entry);
        modelEditText = findViewById(R.id.editor_model_entry);
        priceEditText = findViewById(R.id.editor_price_entry);
        quantityEditText = findViewById(R.id.editor_quantity_entry);
        supplierEditText = findViewById(R.id.editor_supplier_entry);
        supplierPhoneEditText = findViewById(R.id.editor_supplier_phone_entry);
        bikeTypeAutoCompleteTextView = findViewById(
                R.id.editor_bike_type);
        Button saveAddButton = findViewById(R.id.save_add_button);

        // Set touch listener to all views
        makeEditText.setOnTouchListener(touchListener);
        modelEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        quantityEditText.setOnTouchListener(touchListener);
        supplierEditText.setOnTouchListener(touchListener);
        supplierPhoneEditText.setOnTouchListener(touchListener);
        bikeTypeAutoCompleteTextView.setOnTouchListener(touchListener);

        // add onClick to saveAddButton
        saveAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBike();
                finish();
            }
        });

        // Add functionality to AutoCompleteTextView
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.bike_type_options));

        bikeTypeAutoCompleteTextView.setAdapter(adapter);
        bikeTypeAutoCompleteTextView.setKeyListener(null);

        // Depending on what option is touched update the displayed text
        bikeTypeAutoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(EditorActivity.this);
                // Adds a timer so that the AutoCompleteTextView opens after soft keyboard close
                // implemented in order to reduce the amount of movement and
                // distraction on the screen
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bikeTypeAutoCompleteTextView.showDropDown();
                    }
                }, 100);

                return false;
            }
        });

        // Depending on what option is clicked update the bikeType holder value
        bikeTypeAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                String selection = (String) parent.getItemAtPosition(pos);

                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(
                            getString(R.string.bike_type_road))) {
                        bikeType = BikeEntry.TYPE_ROAD;
                    } else if (selection.equals(
                            getString(R.string.bike_type_mountain))) {
                        bikeType = BikeEntry.TYPE_MOUNTAIN;
                    } else if (selection.equals(
                            getString(R.string.bike_type_hybrid))) {
                        bikeType = BikeEntry.TYPE_HYBRID;
                    } else if (selection.equals(
                            getString(R.string.bike_type_fixed))) {
                        bikeType = BikeEntry.TYPE_FIXED;
                    } else if (selection.equals(
                            getString(R.string.bike_type_city))) {
                        bikeType = BikeEntry.TYPE_CITY;
                    } else if (selection.equals(
                            getString(R.string.bike_type_gravel_cross))) {
                        bikeType = BikeEntry.TYPE_GRAVEL_AND_CROSS;
                    } else if (selection.equals(
                            getString(R.string.bike_type_tandem))) {
                        bikeType = BikeEntry.TYPE_TANDEM;
                    } else if (selection.equals(
                            getString(R.string.bike_type_recumbent))) {
                        bikeType = BikeEntry.TYPE_RECUMBENT;
                    } else if (selection.equals(
                            getString(R.string.bike_type_cargo))) {
                        bikeType = BikeEntry.TYPE_CARGO;
                    } else if (selection.equals(
                            getString(R.string.bike_type_electric))) {
                        bikeType = BikeEntry.TYPE_ELECTRIC;
                    } else if (selection.equals(
                            getString(R.string.bike_type_folding))) {
                        bikeType = BikeEntry.TYPE_FOLDING;
                    } else if (selection.equals(
                            getString(R.string.bike_type_kids))) {
                        bikeType = BikeEntry.TYPE_KIDS;
                    } else if (selection.equals(
                            getString(R.string.bike_type_touring))) {
                        bikeType = BikeEntry.TYPE_TOURING;
                    } else {
                        bikeType = BikeEntry.TYPE_UNKNOWN;
                    }
                }
            }
        });
    }

    private void saveBike() {
        // read the input and clean it up a bit
        String makeString = makeEditText.getText().toString().trim();
        String modelString = modelEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String supplierString = supplierEditText.getText().toString().trim();
        String supplierPhoneString = supplierPhoneEditText.getText().toString().trim();
        int bikeTypeSelection = bikeTypeAutoCompleteTextView.getListSelection();

        // check if it's an existing bike and where data has been entered
        if (currentBikeUri == null &&
                TextUtils.isEmpty(makeString) &&
                TextUtils.isEmpty(modelString) &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(supplierString) &&
                TextUtils.isEmpty(supplierPhoneString) &&
                (bikeTypeSelection == 0)) {
            // Since no fields were modified, we can return early without creating a new entry.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Build the content values object
        ContentValues values = new ContentValues();
        values.put(BikeEntry.COLUMN_MAKE, makeString);
        values.put(BikeEntry.COLUMN_MODEL, modelString);
        values.put(BikeEntry.COLUMN_TYPE, bikeType);
        values.put(BikeEntry.COLUMN_PRICE, Integer.parseInt(priceString));
        values.put(BikeEntry.COLUMN_QUANTITY, Integer.parseInt(quantityString));
        values.put(BikeEntry.COLUMN_SUPPLIER, supplierString);
        values.put(BikeEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);

        // Determine if this is a new or existing bike by checking if currentBikeUri is null or not
        if (currentBikeUri == null) {
            Uri newUri = getContentResolver().insert(BikeEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this,
                        getString(R.string.save_bike_add_message_fail),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this,
                        getString(R.string.save_bike_add_message_success),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // add logic here for when you click on an existing bike entry
            int rowsAffected = getContentResolver().update(currentBikeUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(
                        R.string.save_bike_update_message_fail),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this,
                        getString(R.string.save_bike_update_message_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Early exit is the cursor is empty
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Go to the first row and start reading from cursor
        if (cursor.moveToFirst()) {
            // Extract out the value from the Cursor for the given column index
            Integer id = cursor.getInt(
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
            Integer quantityData = cursor.getInt(
                    cursor.getColumnIndex(
                            BikeEntry.COLUMN_QUANTITY));
            String supplierData = cursor.getString(
                    cursor.getColumnIndex(
                            BikeEntry.COLUMN_SUPPLIER));
            String supplierPhoneData = cursor.getString(
                    cursor.getColumnIndex(
                            BikeEntry.COLUMN_SUPPLIER_PHONE));

            // Edit title
            setTitle(getString(R.string.details_activity_title));

            // Update the views on the screen with the values from the database
            makeEditText.setText(makeData);
            modelEditText.setText(modelData);
            priceEditText.setText(String.valueOf(priceData));
            quantityEditText.setText(String.valueOf(quantityData));
            supplierEditText.setText(supplierData);
            supplierPhoneEditText.setText(supplierPhoneData);

            // For the AutoCompleteTextView dropdown we need to show the dropdown first
            bikeTypeAutoCompleteTextView.showDropDown();
            // Trigger this method becase .setSelection doesn't produce the right result
            bikeTypeAutoCompleteTextView.onCommitCompletion(new CompletionInfo(0, typeData, null));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        makeEditText.getText().clear();
        modelEditText.getText().clear();
        priceEditText.getText().clear();
        quantityEditText.getText().clear();
        supplierEditText.getText().clear();
        supplierPhoneEditText.getText().clear();
        bikeTypeAutoCompleteTextView.setListSelection(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!viewHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                triggerUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!viewHasChanged) {
            super.onBackPressed();
            return;
        }

        // Else warn the user about the discarding of entered data
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        triggerUnsavedChangesDialog(discardButtonClickListener);
    }

    // Creates a Dialog when changes are about to be discarded
    private void triggerUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_dialog_message);
        builder.setPositiveButton(R.string.discard_dialog_confirm, discardButtonClickListener);
        builder.setNegativeButton(R.string.discard_dialog_deny, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
