package channel.price.calculator.meratv.json_multiple.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import channel.price.calculator.meratv.json_multiple.db.MultipleContract.MultipleEntry;

public class MultipleDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "MultipleDbHelper";

    // Name of the database file
    private static final String DATABASE_NAME = "pkg.db";

    // Database version. If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public MultipleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the table
        final String SQL_CREATE_PKG_TABLE = "CREATE TABLE " + MultipleEntry.TABLE_NAME + "("
                + MultipleEntry.UIDP + " TEXT PRIMARY KEY, "
                + MultipleEntry.COLUMN_NAME + " TEXT, "
                + MultipleEntry.COLUMN_PRICE + " REAL, "
                + MultipleEntry.COLUMN_DATAENTRY + " TEXT, "
                + MultipleEntry.COLUMN_TIME + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PKG_TABLE);
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

        // if it find same UIDP then it will replace that row
        db.insertWithOnConflict(MultipleEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
}
