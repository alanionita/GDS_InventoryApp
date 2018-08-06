package com.example.android.gds_inventoryapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.android.gds_inventoryapp.Data.BikeContract;

public class EditorActivity extends AppCompatActivity {

    // Set default selection for bike type
    private int bikeTypeDefault = BikeContract.BikeEntry.TYPE_UNKNOWN;

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
        Button editorButton = findViewById(R.id.sale_button);

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
}
