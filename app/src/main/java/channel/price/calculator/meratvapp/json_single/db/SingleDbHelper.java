package channel.price.calculator.meratvapp.json_single.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import channel.price.calculator.meratvapp.json_single.db.SingleContract.SingleEntry;

public class SingleDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "SingleDbHelper";

    // Name of the database file
    private static final String DATABASE_NAME = "chn.db";

    // Database version. If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public SingleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the table
        final String SQL_CREATE_CHN_TABLE = "CREATE TABLE " + SingleEntry.TABLE_NAME + "("
                + SingleEntry.UIDC + " TEXT PRIMARY KEY, "
                + SingleEntry.COLUMN_NAME + " TEXT, "
                + SingleEntry.COLUMN_PRICE + " REAL, "
                + SingleEntry.COLUMN_SDHD + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_CHN_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.
    }



    public void insert(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        Log.d(TAG, "insert: " + db);

        // if it find same UIDC then it will replace that row
        db.insertWithOnConflict(SingleEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public double get_price(String main_uidc){

        SQLiteDatabase db = getReadableDatabase();

        String countQuery = " SELECT * FROM " + SingleEntry.TABLE_NAME +" WHERE "+ SingleEntry.UIDC + " = "+main_uidc ;
        Cursor cursor = db.rawQuery(countQuery, null);
        double main_price = 0.00;
        if( cursor != null && cursor.moveToFirst() ) {
            //todo change if DB edited
            main_price = cursor.getDouble(2);
        }

        return main_price;
    }

    public String get_name(String main_uidc){

        SQLiteDatabase db = getReadableDatabase();

        String countQuery = " SELECT * FROM " + SingleEntry.TABLE_NAME +" WHERE "+ SingleEntry.UIDC + " = "+main_uidc ;
        Cursor cursor = db.rawQuery(countQuery, null);
        String name = "";
        if( cursor != null && cursor.moveToFirst() ) {
            //todo change if DB edited
            name = cursor.getString(1);
        }

        return name;
    }
}
