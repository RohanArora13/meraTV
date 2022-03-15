/*******************************************************************************
 * Copyright (C) Rohan Arora - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Rohan Arora <rohanarora1313@gmail.com> on 27/12/18 7:40 PM
 *
 * Last modified 27/12/18 7:32 PM
 *
 ******************************************************************************/

package channel.price.calculator.meratv;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.itextpdf.text.DocumentException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import channel.price.calculator.meratv.json_single.JsonToSqlSingle;
import channel.price.calculator.meratv.json_single.db.SingleDbHelper;
import channel.price.calculator.meratv.selection.SelectionAdapter;
import channel.price.calculator.meratv.selection.SelectionContract;
import channel.price.calculator.meratv.selection.SelectionDbHelper;

//import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
//import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

public class MainActivity extends AppCompatActivity implements MeFragment.channel_fragment_listner, SelectionAdapter.restore_notify {

    //TAG STRING
    public final String CHANNEL_FETCH = "channel_json_fetch";
    public String TAG_MAIN = "MainActivity";
    public String SELECTION_TAG = "selection_tag";
    public String version_check = "version_check";

    // layout variables
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    public ProgressDialog progDailog;
    public BottomSheetBehavior bottomSheetBehavior;
    public RelativeLayout bottom_sheet;
    public TextView total_price;
    public Button view_selection_btn,share_pdf_btn,clear_selection_btn;
    private LinearLayout button_layout;

    // class variables
    final public String version_code = "version_code";
    final public String mainpref = "Main__pref";

    public Integer current_uidc_add;
    public Integer current_uidc_remove;
    public Integer current_selection_uid_s = 2000;

    public double channel_price_final = 0.00;
    public double current_total_price = 0.00;
    DecimalFormat precision = new DecimalFormat("0.00");

    public Boolean Selection_added = false;
    public static final int STORAGE_PERMISSION_RC = 1;
    Boolean expandthestate = false;
    private Integer pager_colapse;
    private Integer last_percent_scrolled = 0;
    Integer increment_height=1;

    // sharedpref variables
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public final String current_selection_pref = "current_selection_uid_s";


    // calculation variables
    public Double base_price = 130.00;  // base price
    public Double maintanence_price = 0.00; // maintanence price
    public Double slot_price = 0.00; // slot extenstion price
    public Double gst_charges = 0.00;
    public Integer final_height=0;

    // channel info bottom sheet
    public Integer paid_chn = 0,free_chn = 0;
    public Integer total_slot = 100,packages_selected = 0;
    //private Object clearSelectionListener;

    public Context context;

    // clear selection interface
    public clearSelectionListener getFragmentRefreshListener() {
        return  fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(clearSelectionListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    private clearSelectionListener fragmentRefreshListener;


    // Retrive ( Set ) selections
    public SetSelectionListener getFragmentSetSelectionListener() {
        return  fragmentSetSelectionListener;
    }

    public void setFragmentSetSelectionListener(SetSelectionListener fragmentSetSelectionListener) {
        this.fragmentSetSelectionListener = fragmentSetSelectionListener;
    }

    private SetSelectionListener fragmentSetSelectionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //add_pager();

        bottom_sheet = findViewById(R.id.bottom_window);
        total_price= findViewById(R.id.total_price);
        button_layout=findViewById(R.id.button_layout);

        bottom_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bndle = new Bundle();
                // calculation variables
                bndle.putString("base_price",Double.toString(base_price));
                bndle.putString("maintanence_price",Double.toString(maintanence_price));
                bndle.putString("slot_price",Double.toString(slot_price));
                bndle.putString("gst_charges",Double.toString(gst_charges));
                bndle.putString("channel_price_final",Double.toString(channel_price_final));

                // channel variables
                bndle.putString("free_chn",Integer.toString(free_chn));
                bndle.putString("paid_chn",Integer.toString(paid_chn));
                bndle.putString("total_slot",Integer.toString(total_slot));
                bndle.putString("packages_selected",Integer.toString(packages_selected));
                bndle.putInt("current_selection",current_selection_uid_s);

                // total price
                bndle.putString("current_total_price",Double.toString(current_total_price));

                // call bottom sheet
                Bottomsheet_fragment btm_frag= new Bottomsheet_fragment();
                btm_frag.setArguments(bndle);
                btm_frag.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });

        // shared preference defining
        pref = this.getSharedPreferences(mainpref, 0);
        editor = pref.edit();

        // button and onclicks
        view_selection_btn=findViewById(R.id.btn_view_selection);
        share_pdf_btn=findViewById(R.id.btn_share_pdf);
        clear_selection_btn=findViewById(R.id.btn_clear_selection);

        // clear selection
        clear_selection_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Are you sure?");
                builder.setMessage("This will clear your selected channels (NOTE - you can restore this selections from history)");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        // retrive last saved selection from shared prefrence
                        if(getFragmentRefreshListener()!=null){
                            getFragmentRefreshListener().onclear();
                        }

                        create_new_selection();
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this,"Cleared Selections..",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        // share pdf
        // create & share pdf on button click
        share_pdf_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ask for storage permission if it doesn't has any permission
                if (!hasPermissions(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_RC
                    );
                } else {
                    // if storage permission granted then call this to create & share pdf
                    try {// calculation variables
                        new PDFGenerator(MainActivity.this).createPdf(current_selection_uid_s);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        progDailog = new ProgressDialog(MainActivity.this);
        progDailog.setMessage("Updating...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.setCanceledOnTouchOutside(false);

        try {
            Drawable drawable = new ProgressBar(MainActivity.this).getIndeterminateDrawable().mutate();
            drawable.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.green_teal_600),
                    PorterDuff.Mode.SRC_IN);
            progDailog.setIndeterminateDrawable(drawable);

        } catch (Exception e) {
        }

        progDailog.show();

        /* check for internet */
        Log.d(version_check,"checking internet");
//        new checknet().execute();
        isNetworkAvailable();
        Log.d(version_check,"isNetworkAvailable internet"+isNetworkAvailable());
        if(isNetworkAvailable()){
            //new version_check().execute();
            JsonToSqlSingle Json_task = new JsonToSqlSingle(new JsonToSqlSingle.AsyncResponse() {
                @Override
                public void processFinish(Boolean output) {
                    Log.d("version_check", "processFinish started");
                    progDailog.dismiss();
                    add_pager();
                }
            }, getBaseContext());

//                if (!online_vc.equals(offline_c)) {
            // json data saved

            Json_task.execute();

        }else{
            progDailog.dismiss();
            Toast.makeText(this, "Not connected to internet! please connect to internet and try again", Toast.LENGTH_LONG).show();
        }


        Log.d(TAG_MAIN, "strings " + get_select_c());



    }



    public void add_pager() {
        Log.d(TAG_MAIN, "add_pager started");
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        int limit = (viewPagerAdapter.getCount() > 1 ? viewPagerAdapter.getCount() - 1 : 1);

        viewPager.setOffscreenPageLimit(limit);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // animation
        Integer bottom_sheet_height=bottom_sheet.getHeight();
        pager_colapse = viewPager.getHeight();
        final_height=(bottom_sheet_height/3)+button_layout.getHeight()+pager_colapse;

        tabLayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                //todo get below layout heights and add to get required height
                Point windowSize = new Point();
                // Calculate the position if this window
//                getParent().getWindowManager().getDefaultDisplay().getSize(windowSize);
                int scrollX = viewPager.getScrollX(); // Current x scrolling position
                // We know that we have at least one child
                int maxScrollWidth = viewPager.getChildAt(0).getMeasuredWidth() - windowSize.x;
                Log.d("Window","scrollX = "+scrollX+" maxScrollWidth = "+maxScrollWidth);

                Integer view_pager_height= viewPager.getHeight();
                Integer percent_scrolled =(scrollX * 100 )  / maxScrollWidth;
                Log.d("Window","----------------------------------------------------");
                Log.d("Window","percent_scrolled = "+percent_scrolled);
                Log.d("Window","base  = "+percent_scrolled);


                Log.d("Window","increment_height ="+increment_height);
                Log.d("Window","viewpager height  = "+view_pager_height);
                Log.d("Window","final_height  = "+final_height);
                Log.d("Window","pager_colapse = "+pager_colapse);


                if(percent_scrolled>last_percent_scrolled && view_pager_height!=final_height){ //&& viewPager.getHeight()<final_height){
                    increment_height = (final_height-view_pager_height)/50;
                    last_percent_scrolled=percent_scrolled;
                    //last_percent_scrolled=percent_scrolled;
                    Log.d("Window","expand");
                    ViewGroup.LayoutParams params= viewPager.getLayoutParams();
                    params.height=view_pager_height+(increment_height*(percent_scrolled));
                    viewPager.setLayoutParams(params);

                }
                else if(percent_scrolled<50&&percent_scrolled<last_percent_scrolled&&view_pager_height!=pager_colapse) {
                    increment_height = (view_pager_height-pager_colapse)/50;
                    last_percent_scrolled=percent_scrolled;
                  //  last_percent_scrolled=percent_scrolled;
                    Log.d("Window","collapsethestate");
                    ViewGroup.LayoutParams params=viewPager.getLayoutParams();
                    params.height=view_pager_height-(increment_height*(50-percent_scrolled));
                    // params.height=viewPager.getHeight()-increment_height;
                    viewPager.setLayoutParams(params);

                }
                Log.d("Window","last_percent_scrolled ="+last_percent_scrolled);
            }
        });


    }

    // ask previous selection to restore alert box
    public void ask_to_retrive(final Integer id,String from){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(from == "history"){
            builder.setTitle("Restore selection from history?");
            builder.setMessage("Note - Current selections are auto-saved");
        }
        else {
            builder.setTitle("Restore Last selections of channels?");
            builder.setMessage("Restore channels selected last time before closing app");
        }

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                // retrive last saved selection from shared prefrence
                retrive(id);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void retrive(Integer Selection_id){
        current_selection_uid_s=Selection_id;

        // get list from selection DB
        SelectionDbHelper sel = new SelectionDbHelper(this);

        // update recyclerview with list
       // sel.get_uidc_from_selection(Selection_id);
        Log.d("retrive","retriving uidcs = "+ sel.get_uidc_from_selection(Selection_id));
       // sel.get_uidc_from_selection(Selection_id);

        if(  sel.get_uidc_from_selection(Selection_id)!=null) {
            if (getFragmentSetSelectionListener() != null) {
                getFragmentSetSelectionListener().onRetrive(sel.get_uidc_from_selection(Selection_id));
            }
        }
        else {
            //todo uidc from selection is empty .. error improvement
        }

      //  new calculate().execute();
    }



//    get channel price from meFragment
    @Override
    public void onInput_channelFragment (Bundle input) {

        SelectionDbHelper sel = new SelectionDbHelper(this);
        current_uidc_add=input.getInt("channel_uidc_add");
        current_uidc_remove=input.getInt("channel_uidc_remove");
        channel_price_final=input.getDouble("total_channel_price");
        paid_chn=input.getInt("paid_channels");
        free_chn=input.getInt("free_channels");

        Log.d("onInput_channelFragment","current_uidc_add = "+current_uidc_add+" current_uidc_remove = "+current_uidc_remove+" channel_price_final = "+channel_price_final);
        Log.d("onInput_channelFragment","paid_chn = "+paid_chn +" free_chn = "+free_chn);

        // calcualate total price and show to user
        update_price(channel_price_final);

        // ask to restore data
        // current selection
        if(current_selection_uid_s==2000){

            // create new selections
            create_new_selection();
            Log.d(SELECTION_TAG,"current_selection id "+current_selection_uid_s);

            // selection id , add , remove , channel price
            sel.update_selection(current_selection_uid_s,current_uidc_add,current_uidc_remove,current_total_price);
            Log.d(SELECTION_TAG,"current selection entry = "+ pref.getInt("current_selection_uid_s",0));
        }
        else {
            sel.update_selection(current_selection_uid_s,current_uidc_add,current_uidc_remove,current_total_price);
        }
    }


    // create a new selection entry in database and set current_selection_uid_s
    public void create_new_selection(){
        Log.d(SELECTION_TAG,"create_selection_Mainactivity started");
        SelectionDbHelper selectionDbHelper = new SelectionDbHelper(this);
        ContentValues cv = new ContentValues();

        long current_time = System.currentTimeMillis();
        cv.put(SelectionContract.SelectionEntry.COLUMN_ENTRY_UID,"");
        cv.put(SelectionContract.SelectionEntry.COLUMN_NAME, "Unnamed");
        cv.put(SelectionContract.SelectionEntry.COLUMN_TIME, current_time);
        cv.put(SelectionContract.SelectionEntry.COLUMN_TOTAL_PRICE, 0.0);
        cv.put(SelectionContract.SelectionEntry.COLUMN_MTN_CHARGE, "50");
        cv.put(SelectionContract.SelectionEntry.COLUMN_GST, "false");
        cv.put(SelectionContract.SelectionEntry.COLUMN_MONTHLY_REPORT, "00");

        selectionDbHelper.insert(cv);

        Integer selection_id = selectionDbHelper.current_uidc(current_time);

        current_selection_uid_s =selection_id;
        editor.putInt(current_selection_pref,selection_id);
        editor.apply();
        editor.commit();
    }

    // selection fragment restore selection
    @Override
    public void restore_this(Integer id) {
        tabLayout.setScrollPosition(0,0f,true);
        viewPager.setCurrentItem(0);
        ask_to_retrive(id,"history");
        current_selection_uid_s = id;
        Log.d( SELECTION_TAG,"restore this id = "+id);
        //viewPagerAdapter.notifyDataSetChanged();
    }


    // clear selection interface
    public interface clearSelectionListener{
        void onclear();
    }

    // set selection interface
    public interface SetSelectionListener{
        void onRetrive(ArrayList<String> uidc);
    }

    /* check list version */
    public class version_check extends AsyncTask<Void, Void, Void> {


        String newsline = "";
        String Data = "";
        String online_vc;
        String offline_c = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d(version_check," onPreExecute");

            progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setMessage("Updating...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.setCanceledOnTouchOutside(false);

            try {
                Drawable drawable = new ProgressBar(MainActivity.this).getIndeterminateDrawable().mutate();
                drawable.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.green_teal_600),
                        PorterDuff.Mode.SRC_IN);
                progDailog.setIndeterminateDrawable(drawable);

            } catch (Exception e) {
            }

            progDailog.show();

            Log.d("version_check", "");
            Log.d("version_check", " onPreExecuted");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //  http://mrp.narosys.com/version.txt

//            Log.d("version_update", "excecuted");
//            URL ul = null;
//            try {
//                ul = new URL("http://mrp.narosys.com/version.txt");
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//            HttpURLConnection httpURLConnection = null;
//            try {
//                httpURLConnection = (HttpURLConnection) ul.openConnection();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            InputStream inputStream = null;
//            try {
//                inputStream = httpURLConnection.getInputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//
//
//            while (newsline != null) {
//                Log.d(CHANNEL_FETCH, "online project.mrp.mrp.data is available ");
//
//                try {
//                    newsline = bufferedReader.readLine();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Data = Data + newsline;
//                try {
//                    Data = Data.replace("null", " ");
//                } catch (Exception e) {
//                    Log.e("version_update", "error  = " + e);
//                }
//                Data.trim();
//            }
//
//            Log.d("version_update", "Data = " + Data);
//
//            online_vc = Data;
//            Log.d("version_check", "online_vc =" + online_vc);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("version_check", "onPostExecute started");

            offline_c = pref.getString(version_code, "12");

            Log.d("version_check", "offline_c =" + offline_c);
            Log.d("version_check", "online_vc =" + online_vc);

            // update json and add to files

            JsonToSqlSingle Json_task = new JsonToSqlSingle(new JsonToSqlSingle.AsyncResponse() {
                    @Override
                    public void processFinish(Boolean output) {
                        Log.d("version_check", "processFinish started");
                        progDailog.dismiss();
                        add_pager();
                        Log.d("version_check", " output = "+output);
                        if (output && !online_vc.equals(offline_c)) {
                            Log.d("version_check", " online_vc != offline_c");
                            // add loading

                            editor.putString(version_code, online_vc);
                            editor.apply();
                            editor.commit(); // commit changes

                            Log.d("version_check", " editor applied ");
                        }
                        if(current_selection_uid_s!=2000) {
                            // pref.getInt(current_selection_pref,0);
                            ask_to_retrive(pref.getInt(current_selection_pref,0),"");
                        }
                        progDailog.dismiss();
                    }
                }, getBaseContext());

//                if (!online_vc.equals(offline_c)) {
                // json data saved
                Json_task.execute();
                //}
//                else {

//                }
                //new JsonToSqlSingle(getBaseContext()).execute();

                Log.d("version_check", "else started");
                // load pager
                add_pager();
            }
    }


    /**
     * function to add / get UIDC channels in select shared prefrence
     **/
    // debug mainActivity_pref_add_uidc_select
    public void add_UIDC_select(String uidc) {
        Boolean added = false;

        try {

            DBhelper db = new DBhelper(getBaseContext());
            db.add_UDIC(uidc);
            added = true;
        } catch (Exception e) {
            //todo databse not working
        }

        if (added) {
            SingleDbHelper sdb = new SingleDbHelper(this);
            double price = sdb.get_price(uidc);
            //add_to(price, uidc);
        }

    }


    public void remove_UIDC(String uidc) {
        DBhelper sdb = new DBhelper(this);
        sdb.remove(uidc);

    }

    public ArrayList get_select_c() {
        ArrayList main = null;

        DBhelper db = new DBhelper(getBaseContext());
        try {
            main = db.get_UDIC();

        } catch (Exception e) {
            //todo databse not working
        }

        return main;
    }


    /**
     * function to add / get UIDP packages in select shared prefrence
     **/

    public void add_UIDP_select(String uidp) {


        // return null;
    }

    public ArrayList get_select_p() {


        return null;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    // add maintance price , base price ,slot price & calculate GST
    public void update_price(Double total_channel_price) {

        current_total_price = base_price + maintanence_price
                + slot_price  + total_channel_price;

        // gst calculation
        gst_charges = (18*current_total_price) / 100 ;
        // adding gst charges
        current_total_price = current_total_price + gst_charges;
        DecimalFormat precision = new DecimalFormat("0.00");
        //total_channel_price;
        total_price.setText(precision.format(current_total_price) );

    }

    public class checknet extends AsyncTask<Void, Void, Boolean> {
        Boolean net = false;

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                InetAddress ipAddr = InetAddress.getByName("google.com");

                //You can replace it with your name
                net = !ipAddr.equals("");
                Log.d(version_check,"checking internet");

            } catch (Exception e) {
                e.printStackTrace();
                net = false;
            }

            return net;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            Log.d(version_check," onPostExecute(aBoolean); "+aBoolean.toString());
            if (aBoolean) {
                new version_check().execute();
            } else {
                add_pager();
            }

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // checks whether permissions asked are given or not
    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public class calculate extends AsyncTask<Void,Void,Boolean> {

        Integer free,paid ,no_of_channels,total_available_channel,available_channel;
        Double channel_price =0.00,base_price_main = 0.00,maintanence_price_main =0.00;
        Double total_price_main = 0.00;
        @Override
        protected Boolean doInBackground(Void... voids) {

            // calculating data from selection DB
            SelectionDbHelper sel = new SelectionDbHelper(context);
            //SingleDbHelper single_DB = new SingleDbHelper(getContext());

            ArrayList uidc_list = sel.get_uidc_from_selection(current_selection_uid_s);

            Log.d("PDGGenerator","uidc_list.size()"+uidc_list.size());
            // todo get data into bottom sheet
            for(int i=0;i<uidc_list.size();i++){
                SingleDbHelper single_DB = new SingleDbHelper(context);
                Double price = single_DB.get_price (uidc_list.get(i).toString().trim());
                Log.d("PDGGenerator","uidc = "+uidc_list.get(i).toString().trim()+" price = "+single_DB.get_price (uidc_list.get(i).toString().trim()));
                if (price<=0.00){
                    free+=1;
                }else {
                    paid +=1;
                }
                channel_price = channel_price + price;

                no_of_channels = free+paid;
                Log.d("PDGGenerator","no_of_channels = "+no_of_channels);
                //no_of_channels=101;

                if(no_of_channels>total_available_channel){
                    total_available_channel+=25;
                    slot_price+=20;
                }
            }

            // todo calculate slots

            available_channel= total_available_channel - no_of_channels;
            total_price_main = base_price_main + maintanence_price_main + slot_price  + channel_price;

            // gst calculation
            gst_charges = (18*total_price_main) / 100 ;
            // adding gst charges
            total_price_main = total_price_main + gst_charges;

            DecimalFormat precision = new DecimalFormat("0.00");
            total_price.setText( precision.format(total_price));

            return true;
        }


//                @Override
//        protected void onPostExecute(Boolean aBoolean) {
//            super.onPostExecute(aBoolean);
//        }
    }

}

//    public boolean checknet() {
//        try {
//            InetAddress ipAddr = InetAddress.getByName("google.com");
//            //You can replace it with your name
//            return !ipAddr.equals("");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

