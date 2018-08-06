package com.example.android.gds_inventoryapp.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.gds_inventoryapp.Data.BikeContract.BikeEntry;

public class BikeProvider extends ContentProvider {
    // Tag for the log messages
    public static final String LOG_TAG = BikeProvider.class.getSimpleName();
    // URI matcher codes
    private static final int BIKES = 100;
    private static final int SINGLE_BIKE = 101;
    // Match a content URL to a specific code
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        uriMatcher.addURI(
                BikeContract.CONTENT_AUTHORITY,
                BikeContract.PATH_BIKES,
                BIKES);
        uriMatcher.addURI(
                BikeContract.CONTENT_AUTHORITY,
                BikeContract.PATH_BIKES + "/#",
                SINGLE_BIKE);
    }

    BikeDbHelper bikeDbHelper;

    @Override
    public boolean onCreate() {
        bikeDbHelper = new BikeDbHelper(getContext());
        return true;
    }

    // Make a query for a given URI
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = bikeDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = uriMatcher.match(uri);
        switch (match) {
            case BIKES:
                // For BIKES query the entire table
                cursor = database.query(BikeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case SINGLE_BIKE:
                // For the SINGLE_BIKE, get the ID from the URI.
                selection = BikeEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BikeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // Insert new data into the Provider given ContentValues
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BIKES:
                if (contentValues == null) {
                    throw new IllegalArgumentException("ContentValues are null");
                }
                return insertBike(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    // Insert data into db given ContentValues
    private Uri insertBike(Uri uri, ContentValues contentValues) {
        // Get readable database
        SQLiteDatabase database = bikeDbHelper.getReadableDatabase();

        // Check that the make is not null
        String make = contentValues.getAsString(BikeEntry.COLUMN_MAKE);
        if (make == null) {
            throw new IllegalArgumentException("Bike entries require a make");
        }

        // Check that the model is not null
        String model = contentValues.getAsString(BikeEntry.COLUMN_MODEL);
        if (model == null) {
            throw new IllegalArgumentException("Bike entries require a model");
        }

        // Check that the type of bike is one the provided
        int type = contentValues.getAsInteger(BikeEntry.COLUMN_TYPE);
        switch (type) {
            case BikeEntry.TYPE_UNKNOWN:
            case BikeEntry.TYPE_CARGO:
            case BikeEntry.TYPE_CITY:
            case BikeEntry.TYPE_ELECTRIC:
            case BikeEntry.TYPE_FIXED:
            case BikeEntry.TYPE_FOLDING:
            case BikeEntry.TYPE_GRAVEL_AND_CROSS:
            case BikeEntry.TYPE_HYBRID:
            case BikeEntry.TYPE_KIDS:
            case BikeEntry.TYPE_MOUNTAIN:
            case BikeEntry.TYPE_RECUMBENT:
            case BikeEntry.TYPE_ROAD:
            case BikeEntry.TYPE_TANDEM:
            case BikeEntry.TYPE_TOURING:
                Log.i("ContentValues/Gender: ", "Bike entry type was passed correctly!");
                break;
            default:
                throw new IllegalArgumentException("Bike entries require one of the assigned bike types.");
        }

        // Check that the price is not a negative number
        int price = contentValues.getAsInteger(BikeEntry.COLUMN_PRICE);
        if (price < 0) {
            throw new IllegalArgumentException("Bike entries require a price input");
        }

        // Check that the quantity is not a negative number
        int quantity = contentValues.getAsInteger(BikeEntry.COLUMN_QUANTITY);
        if (quantity < 0) {
            throw new IllegalArgumentException("Bike entries require a quantity input");
        }

        // Check that the supplier is not null
        String supplier = contentValues.getAsString(BikeEntry.COLUMN_SUPPLIER);
        if (supplier == null) {
            throw new IllegalArgumentException("Bike entries require a supplier");
        }

        // Check that the supplier phone number is not null
        String supplierPhone = contentValues.getAsString(BikeEntry.COLUMN_SUPPLIER_PHONE);
        if (supplierPhone == null) {
            throw new IllegalArgumentException("Bike entries require a supplier number");
        }

        // This cursor will hold the result of the query
        long id = database.insert(BikeEntry.TABLE_NAME, null, contentValues);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BIKES:
                return updateBike(uri, contentValues, selection, selectionArgs);
            case SINGLE_BIKE:
                selection = BikeEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBike(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBike(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = bikeDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(BikeEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
