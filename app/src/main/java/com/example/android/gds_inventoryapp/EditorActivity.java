package com.example.android.gds_inventoryapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

        // Find all of the required view
        final AutoCompleteTextView bikeTypeAutoCompleteTextView = findViewById(
                R.id.editor_bike_type);
        Button editorButton = findViewById(R.id.save_add_button);

        // add onClick to editorButton
        editorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBike();
                finish();
            }
        });

        // Add functionality to AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.bike_type_options));

        bikeTypeAutoCompleteTextView.setAdapter(adapter);
        bikeTypeAutoCompleteTextView.setKeyListener(null);
        bikeTypeAutoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(EditorActivity.this);
                // Adds a timer so that the AutoCompleteTextView opens after softkeyboard close
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
                Toast.makeText(this, "Insertion failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Insertion succesfull",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
           // add logic here for when you click on an existing bike entry
        }
    }
}
