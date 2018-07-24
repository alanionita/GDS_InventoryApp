package com.example.android.gds_inventoryapp.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.gds_inventoryapp.Data.BikeContract.BikeEntry;

public class BikeDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = BikeDbHelper.class.getSimpleName();

    // Name of the database file
    private static final String DATABASE_NAME = "bikeshop.db";

    // Database version.
    // If you change the database schema, you must increment the database version.

    private static final int DATABASE_VERSION = 1;


    public BikeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a String that contains the SQL statement to create the bikes table
        String SQL_CREATE_BIKES_TABLE = "CREATE TABLE " +
                BikeContract.BikeEntry.TABLE_NAME + " ("
                + BikeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BikeEntry.COLUMN_MAKE + " TEXT NOT NULL, "
                + BikeEntry.COLUMN_MODEL + " TEXT NOT NULL, "
                + BikeEntry.COLUMN_TYPE + " INTEGER DEFAULT "
                    + BikeEntry.TYPE_UNKNOWN + ", "
                + BikeEntry.COLUMN_PRICE + " INTEGER DEFAULT 0, "
                + BikeEntry.COLUMN_QUANTITY + " INTEGER DEFAULT 0, "
                + BikeEntry.COLUMN_SUPPLIER + " TEXT NOT NULL, "
                + BikeEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL);";

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_BIKES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
