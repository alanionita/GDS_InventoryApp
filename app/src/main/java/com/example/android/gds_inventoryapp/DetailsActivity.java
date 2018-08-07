package com.example.android.gds_inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<CursorLoader> {
    private static final int BIKE_LOADER = 0;
    private Uri currentBikeUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        // Extract the date from the Uri
        Intent intent = getIntent();
        currentBikeUri = intent.getData();

        if (currentBikeUri == null) {
            setTitle("Bike Not Found");
        } else {
            setTitle("Bike details");
            getLoaderManager().initLoader(BIKE_LOADER, null, this);
        }
    }

    @Override
    public Loader<CursorLoader> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<CursorLoader> loader, CursorLoader cursorLoader) {

    }

    @Override
    public void onLoaderReset(Loader<CursorLoader> loader) {

    }
}
