/*******************************************************************************
 * Copyright (C) Rohan Arora - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Rohan Arora <rohanarora1313@gmail.com> on 2/1/19 9:45 PM
 *
 * Last modified 2/1/19 9:45 PM
 *
 ******************************************************************************/

package channel.price.calculator.meratv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import channel.price.calculator.meratv.DBconstants.DBentry;

public class DBhelper extends SQLiteOpenHelper {

    // strings to save constant
    public final static String DATABASE_NAME= "select.db";
    public final static int DATABASE_VERSION= 1 ;


    public DBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //SQLiteDatabase db = this.getWritableDatabase();
        Log.d("dbhelper","musicDBhelper executed");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQLITE_MUSIC_DATA =" CREATE TABLE " + DBentry.TABLE_NAME_SELECT +
                " ( "+
                DBentry.COLUMN_UIDC +" TEXT, " +
                DBentry.COLUMN_UIDP +" TEXT);";


        db.execSQL(SQLITE_MUSIC_DATA);

        //db.execSQL("PRAGMA journal_mode=WAL");

        Log.d("dbhelper","db.execSQL(SQLITE_MUSIC_DATA) executed");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(" DROP TABLE "+DBentry.TABLE_NAME+";");
        onCreate(db);

    }


    public void add_UDIC(String uidc){
        Log.d("dbhelper","add uidc excecuted");

        SQLiteDatabase db = this.getWritableDatabase();

        // add id
        Cursor cr = db.rawQuery(
                "SELECT * FROM " + DBentry.TABLE_NAME_SELECT + " WHERE " + DBentry.COLUMN_UIDC + " = " + uidc,null);

        // Content Values
        ContentValues cv = new ContentValues();


        if (cr.getCount() != 0) {

            cr.close();
            db.close();
            // close the cursor
        }
        else {
            cv.put(DBentry.COLUMN_UIDC, uidc);
           // cv.put(DBentry.COLUMN_UIDP,"");
        }

        db.insert(DBentry.TABLE_NAME_SELECT, null, cv);


    }

    // get uidc list from database
    public ArrayList get_UDIC(){
        Log.d("dbhelper","get uidc excecuted");

        ArrayList<String> arrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor res = db.rawQuery("select * from " + DBentry.TABLE_NAME_SELECT + " where " + DBentry.COLUMN_UIDC + " not null ", null);
           // res.moveToFirst();
            Log.d("dbhelper", res.toString());

            while (res.moveToNext()) {
                Log.d("dbhelper", res.toString());
                arrayList.add(res.getString(res.getColumnIndex(DBentry.COLUMN_UIDC)));
                Log.d("dbhelper", "fetched from database = " + arrayList.toString());

            }
            res.close();
        }
        catch (Exception e){
            Log.d("dbhelper", "get_uidc error "+e);
        }
        return arrayList;
    }

    public void remove(String uidc_remove){
        SQLiteDatabase db = this.getWritableDatabase();


        db.rawQuery("DELETE FROM " + DBentry.TABLE_NAME_SELECT + " WHERE " + DBentry.COLUMN_UIDC + " = " + uidc_remove, null);

//            ContentValues cv = new ContentValues();
//
//
//            if (cr.getCount() != 0) {
//
//                cr.close();
//                db.close();
//                // close the cursor
//            }
//            else {
//                cv.put(DBentry.COLUMN_UIDC, uidc);
//                // cv.put(DBentry.COLUMN_UIDP,"");
//            }

    }



    }
