package com.example.android.gds_inventoryapp;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.gds_inventoryapp.Data.BikeContract;
import com.example.android.gds_inventoryapp.Data.BikeContract.BikeEntry;

public class EditorActivity extends AppCompatActivity {

    // Set default selection for bike type
    private int bikeTypeDefault = BikeContract.BikeEntry.TYPE_UNKNOWN;

    // Content Uri for current bike
    private Uri currentBikeUri;

    // Find the input views
    private EditText makeEditText;
    private EditText modelEditText;
    private EditText priceEditText;
    private EditText quantityEditText;
    private EditText supplierEditText;
    private EditText supplierPhoneEditText;
    private int bikeType = BikeEntry.TYPE_UNKNOWN;

    // Hides the softkeyboard, useful when dealing with view of different inputs
    // eg. AutoCompleteTextView
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);

        // Depending on the currentBikeUri change the activity title
        if (currentBikeUri == null) {
            setTitle(R.string.editor_activity_title_add);
        } else {
            setTitle(R.string.editor_activity_title_edit);
        }

        // Find all of the required view
        final AutoCompleteTextView bikeTypeAutoCompleteTextView = findViewById(
                R.id.editor_bike_type);
        Button editorButton = findViewById(R.id.save_add_button);
        makeEditText = findViewById(R.id.editor_make_entry);
        modelEditText = findViewById(R.id.editor_model_entry);
        priceEditText = findViewById(R.id.editor_price_entry);
        quantityEditText = findViewById(R.id.editor_quantity_entry);
        supplierEditText = findViewById(R.id.editor_supplier_entry);
        supplierPhoneEditText = findViewById(R.id.editor_supplier_phone_entry);

        // add onClick to editorButton
        editorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBike();
                finish();
            }
        });

        // Add functionality to AutoCompleteTextView
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
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

        // check if it's an existing bike and where data has been entered
        if (currentBikeUri == null &&
                TextUtils.isEmpty(makeString) &&
                TextUtils.isEmpty(modelString) &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(supplierString) &&
                TextUtils.isEmpty(supplierPhoneString)) {
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
        }
    }
}
