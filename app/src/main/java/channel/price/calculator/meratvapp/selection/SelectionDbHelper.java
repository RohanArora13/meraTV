package channel.price.calculator.meratvapp.selection;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import  channel.price.calculator.meratvapp.selection.SelectionContract.SelectionEntry;

public class SelectionDbHelper extends SQLiteOpenHelper {
    // Name of the database file
    private static final String DATABASE_NAME = "selection.db";
    public String SELECTION_TAG = "selection_tag";

    // Database version. If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public SelectionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the table
        final String SQL_CREATE_SELECTION_TABLE = "CREATE TABLE " + SelectionEntry.TABLE_NAME + "("
                + SelectionEntry.COLUMN_SELECTION_UID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + SelectionEntry.COLUMN_ENTRY_UID + " TEXT, "
                + SelectionEntry.COLUMN_NAME + " TEXT, "
                + SelectionEntry.COLUMN_TOTAL_PRICE + " REAL, "
                + SelectionEntry.COLUMN_MTN_CHARGE + " REAL, "
                + SelectionEntry.COLUMN_GST + " TEXT, "
                + SelectionEntry.COLUMN_TIME + " LONG, "
                + SelectionEntry.COLUMN_MONTHLY_REPORT + " INTEGER);";

        /**
         *
         * monthly report default value 00
         * jan 11 , feb 12 .... dec 22
         * **/

        // Execute the SQL statement
        try {
            db.execSQL(SQL_CREATE_SELECTION_TABLE);
           // db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 100 WHERE name = "+SelectionEntry.TABLE_NAME );
        }catch (Exception e){
            Log.e(SELECTION_TAG,"db execSQL error = "+e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        db.insertWithOnConflict(SelectionEntry.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void delete(String currentUid) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(SelectionEntry.TABLE_NAME, SelectionEntry.COLUMN_ENTRY_UID + "=?", new String[]{currentUid});
    }
    public Integer current_uidc(long time){
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + SelectionEntry.TABLE_NAME + " Where "+SelectionEntry.COLUMN_TIME + " = "+time ;

        Cursor cursor = db.rawQuery(selectQuery, null);
        Integer id= 0;
        if( cursor != null && cursor.moveToFirst() ) {
             id = cursor.getInt(0);
            cursor.close();
        }
        // close db connection

        db.close();

        return id;
    }
    // selection id , add , remove , channel price
    public void update_selection(Integer current_selection,Integer uidc_add,Integer uidc_remove,Double total_price){

        SQLiteDatabase db = getWritableDatabase();

        // query and cursor creation
        String selectQuery = "SELECT  * FROM " + SelectionEntry.TABLE_NAME + " Where "+SelectionEntry.COLUMN_SELECTION_UID + " = "+current_selection ;
        Log.d(SELECTION_TAG,"database query = "+selectQuery);
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

       if( cursor != null && cursor.moveToFirst() ) {

           // getting json data from database to edit
           String list_String = cursor.getString(cursor.getColumnIndex(SelectionEntry.COLUMN_ENTRY_UID));
           Log.d(SELECTION_TAG,"db_json  data = "+list_String);

           //converting to string
           ArrayList<String> db_uidc_array = new ArrayList<String>();
           if(list_String.length()>0) {
               db_uidc_array = convertJsonStringToArray(list_String);
               Log.d(SELECTION_TAG, "db_uidc_array  data = " + db_uidc_array);
           }

           // add to database
            if(uidc_add!=0 && uidc_remove==0 && !db_uidc_array.contains(Integer.toString(uidc_add))){
                Log.d(SELECTION_TAG, "value added ");
                db_uidc_array.add(Integer.toString(uidc_add));

            }
            // remove from database
            else if (uidc_remove!=0 && uidc_add==0 && db_uidc_array.contains(Integer.toString(uidc_remove))){
                Log.d(SELECTION_TAG, "value removed");
                db_uidc_array.remove(Integer.toString(uidc_remove));
            }

           Log.d(SELECTION_TAG,"convertArrayToJsonString = "+convertArrayToJsonString(db_uidc_array));
            // insert in database
           try {
               ContentValues newValues = new ContentValues();
               newValues.put(SelectionEntry.COLUMN_ENTRY_UID, convertArrayToJsonString(db_uidc_array));
               newValues.put(SelectionEntry.COLUMN_TOTAL_PRICE, total_price);

               Log.d(SELECTION_TAG, "update_selection new uidc entry = " + convertArrayToJsonString(db_uidc_array));
               db.update(SelectionEntry.TABLE_NAME, newValues,SelectionEntry.COLUMN_SELECTION_UID+" = ?",new String[]{Integer.toString(current_selection)});
               db.close();
           }catch (Exception e){
               Log.e(SELECTION_TAG, "update_selection databse_Add error = "+e);
           }
        }
        else {
            //todo error while inserting data
           db.close();
       }

    }

    // array to string
    private static String convertArrayToJsonString(ArrayList<String> arraylist){
        // gson is google library
        Gson gson = new Gson();
        String json_string = gson.toJson(arraylist);
        return json_string;
    }

    // string to array
    private static ArrayList<String> convertJsonStringToArray(String str){
        Gson gson = new Gson();
        ArrayList array = gson.fromJson(str,ArrayList.class);
        ArrayList<String> strings = new ArrayList<>();
        if(str!=null && str.length()>0) {
           strings = new ArrayList<>(array.size());
            for (Object object : array) {
                strings.add(object != null ? object.toString() : null);
            }
        }
        return strings;
    }

    // send uidc from selectionDB
    public ArrayList<String> get_uidc_from_selection(Integer selection_uid){

        SQLiteDatabase db = getReadableDatabase();
        // query and cursor creation
        String selectQuery = "SELECT  * FROM " + SelectionEntry.TABLE_NAME + " Where "+SelectionEntry.COLUMN_SELECTION_UID + " = "+selection_uid ;
        Log.d(SELECTION_TAG,"database query = "+selectQuery);
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        String array_in_database = null;
        // get uidc from list
        if( cursor != null && cursor.moveToFirst() ) {
             array_in_database = cursor.getString(cursor.getColumnIndex(SelectionEntry.COLUMN_ENTRY_UID));
        }
        // converting data to arraylist

        return convertJsonStringToArray(array_in_database);
    }


}
