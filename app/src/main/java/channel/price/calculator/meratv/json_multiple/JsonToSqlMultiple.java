package channel.price.calculator.meratv.json_multiple;


import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Iterator;

import channel.price.calculator.meratv.json_multiple.db.MultipleContract.MultipleEntry;
import  channel.price.calculator.meratv.json_multiple.db.MultipleDbHelper;

// TODO: call this from activity to execute:- new JsonToSqlMultiple(context).execute();

public class JsonToSqlMultiple extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "JsonToSqlMultiple";

    // Using WeakReference instead of context directly to avoid memory leaks
    private WeakReference<Context> contextRef;

    // sample project.mrp.mrp.data
    private String json = "{\n" +
            "\t\"item1UID\": {\n" +
            "\t\t\"name\": \"detailes\",\n" +
            "\t\t\"price\": \"10\",\n" +
            "\t\t\"time\": \"102018\",\n" +
            "\t\t\"dataentry\": [\"Single_item_2UID\", \"Single_item_2UID\", \"474564\", \"474564\"]\n" +
            "\t},\n" +
            "\t\"item2UID\": {\n" +
            "\t\t\"name\": \"details\",\n" +
            "\t\t\"price\": \"10\",\n" +
            "\t\t\"time\": \"102018\",\n" +
            "\t\t\"dataentry\": [\"Single_item_2UID\", \"Single_item_2UID4\", \"474564\", \"474564\"]\n" +
            "\t}\n" +
            "}";

    public JsonToSqlMultiple(Context context) {
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // asyncTask name
        Thread.currentThread().setName("fetch_json_Multiple");

        jsonFunction(json);
        return null;
    }

    public void jsonFunction(String json) {
        try {
            JSONObject mainObject = new JSONObject(json);
            // getting root keys which are uidp
            Iterator<String> keys = mainObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Log.d(TAG, "jsonFunction: " + key);

                // json object for each key
                JSONObject object = mainObject.getJSONObject(key);

                String name = object.getString("name");
                Double price = Double.parseDouble(object.getString("price"));
                String time = object.getString("time");
                String dataEntry = object.getString("dataentry");

                // Create a ContentValues object where column names are the keys
                ContentValues values = new ContentValues();
                values.put(MultipleEntry.COLUMN_NAME, name);
                values.put(MultipleEntry.COLUMN_PRICE, price);
                values.put(MultipleEntry.COLUMN_TIME, time);
                values.put(MultipleEntry.UIDP, key);
                values.put(MultipleEntry.COLUMN_DATAENTRY, dataEntry);

                // insert project.mrp.mrp.data into sqlite database
                new MultipleDbHelper(contextRef.get()).insert(values);
            }
        } catch (JSONException e) {
            Log.d(TAG, "jsonFunction: " + e.getMessage());
        }
    }
}