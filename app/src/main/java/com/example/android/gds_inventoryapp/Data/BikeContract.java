package com.example.android.gds_inventoryapp.Data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class BikeContract {

    // prevent accidental instantiating
    private BikeContract() {}

    // Content URI variables
    public static final String CONTENT_AUTHORITY = "com.example.android.gds_inventoryapp";
    public static final String PATH_BIKES = "bikes";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // defines constant values for the db table where one entry = one bike
    public static final class BikeEntry implements BaseColumns {

        // content URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BIKES);

        // defined table name
        public final static String TABLE_NAME = "bikes";

        // column names
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_MAKE = "make";
        public final static String COLUMN_MODEL = "model";
        public final static String COLUMN_TYPE = "type";
        public final static String COLUMN_PRICE = "price";
        public final static String COLUMN_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER = "supplier";
        public final static String COLUMN_SUPPLIER_PHONE = "supplier_phone";

        // possible values for COLUMN_TYPE entries
        public final static int TYPE_UNKNOWN = 0;
        public final static int TYPE_ROAD = 1;
        public final static int TYPE_MOUNTAIN = 2;
        public final static int TYPE_HYBRID = 3;
        public final static int TYPE_FIXED = 4;
        public final static int TYPE_CITY = 5;
        public final static int TYPE_GRAVEL_AND_CROSS = 6;
        public final static int TYPE_TANDEM = 7;
        public final static int TYPE_RECUMBENT = 8;
        public final static int TYPE_CARGO = 9;
        public final static int TYPE_ELECTRIC = 10;
        public final static int TYPE_FOLDING = 11;
        public final static int TYPE_KIDS = 12;
        public final static int TYPE_TOURING = 13;

        // default value for COLUMN_IMAGE entries
        // TODO: add a default image to drawable
        // TODO: replace the string below with a URI to that default image
        public final static String IMAGE_DEFAULT = "default image";


    }
}
