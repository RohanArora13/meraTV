package channel.price.calculator.meratv.json_single;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import  channel.price.calculator.meratv.json_single.db.SingleContract.SingleEntry;
import  channel.price.calculator.meratv.json_single.db.SingleDbHelper;

// TODO: call this from activity to execute:- new JsonToSqlSingle(context).execute();

public class JsonToSqlSingle extends AsyncTask<Void, Void, Boolean> {
    private Boolean done = false;
    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        delegate.processFinish(aVoid);
    }

    private static final String TAG = "JsonToSqlSingle";

    // Using WeakReference instead of context directly to avoid memory leaks
    private WeakReference<Context> contextRef;

    // sample project.mrp.mrp.data
    private String json = "{\n" +
            "\t\"Single_item_UID\": {\n" +
            "\t\t\"name\": \"detailes\",\n" +
            "\t\t\"price\": \"10\",\n" +
            "\t\t\"time\": \"102018\"\n" +
            "\t},\n" +
            "\t\"Single_item_2UID\": {\n" +
            "\t\t\"name\": \"details\",\n" +
            "\t\t\"price\": \"10\",\n" +
            "\t\t\"time\": \"102018\"\n" +
            "\t}\n" +
            "}";


    public interface AsyncResponse {
        void processFinish(Boolean output);
    }

    private AsyncResponse delegate = null;//Call back interface


    public JsonToSqlSingle(AsyncResponse asyncResponse,Context context) {
        contextRef = new WeakReference<>(context);
        delegate = asyncResponse;//Assigning call back interfacethrough constructor
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.d(TAG,"doInBackground started ");
        // asyncTask name
        Thread.currentThread().setName("fetch_json_single");


        Log.d("version_update","  excecuted ");
        URL ul = null;
        try {
           // ul = new URL("http://mrp.narosys.com/chnjson.txt");
            //https://jsonkeeper.com/b/399V
            ul = new URL("https://jsonkeeper.com/b/399V");

        } catch (MalformedURLException e) {
            Log.d("json_single_","MalformedURLException "+e);
            e.printStackTrace();
        }
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) ul.openConnection();
        } catch (IOException e) {
            Log.d("json_single_","IOException "+e);
            e.printStackTrace();
        }

        InputStream inputStream = null;
        try {
            inputStream = httpURLConnection.getInputStream();
            Log.d("json_single_","inputStream = "+inputStream);
        } catch (IOException e) {
            Log.d("json_single_","IOException "+e);
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String newsline = "";
        String Data = "";


        while (newsline != null) {
            //Log.d(CHANNEL_FETCH, "online project.mrp.mrp.data is available ");

            try {
                newsline = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Data = Data + newsline;
            Data.trim();
        }

       String jsonString= "";

        Log.d(TAG,"Data = "+Data);


        done = jsonFunction(Data);
        return done;
    }

    public Boolean jsonFunction(String json) {
        Boolean procces = false;
        Log.d(TAG, "jsonFunction excecuted ");
        try {
            // get reference to db helper to insert data
            SingleDbHelper dbHelper = new SingleDbHelper(contextRef.get());
            // parse the json string to json array
            JSONArray mainArray = new JSONArray(json);
            // traverse for each element of the array
            for (int i = 0; i < mainArray.length(); i++) {
                // get the object present at i th position of array
                JSONObject object = mainArray.getJSONObject(i);
                Log.d(TAG, "jsonFunction: " + object);

                // get the values from the object
                String name = object.getString("name");
                double price = object.getDouble("price");
                String sdhd = object.getString("SDHD");
                int uidc = object.getInt("UIDC");

                // Create a ContentValues object where column names are the keys
                ContentValues values = new ContentValues();
                values.put(SingleEntry.COLUMN_NAME, name);
                values.put(SingleEntry.COLUMN_PRICE, price);
                values.put(SingleEntry.COLUMN_SDHD, sdhd);
                values.put(SingleEntry.UIDC, uidc);

                // insert the data into the table
                dbHelper.insert(values);
            }
            procces = true;
        } catch (JSONException e) {
            procces = false;
            //todo add via  firebase
            Log.e(TAG, "jsonFunction: " + e.getMessage());
        }

        return procces;
    }
}