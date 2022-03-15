/*******************************************************************************
 * Copyright (C) Rohan Arora - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Rohan Arora <rohanarora1313@gmail.com> on 17/1/19 10:02 PM
 *
 * Last modified 17/1/19 10:01 PM
 *
 ******************************************************************************/

package channel.price.calculator.meratv;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;

import channel.price.calculator.meratv.json_single.db.SingleDbHelper;
import channel.price.calculator.meratv.selection.SelectionDbHelper;

public class Bottomsheet_fragment extends BottomSheetDialogFragment {

    //private BottomSheetListener mListener;
//    public Double base_price_main = 130.00;  // base price
//    public Double maintanence_price_main = 0.00; // maintanence price
//    public Double slot_price = 0.00; // slot extenstion price
//    public Double gst_charges = 0.00;
//    public Double channel_price = 0.00; // slot extenstion price
//    public Double total_price = 0.00;
//    public Integer no_of_channels = 0,available_channel = 100;

    // calculation section
    private TextView base_price,slot_extenstion,total_channel_price,maintanence_price,gst_charged,final_price;

   // private String s_base_price,s_slot_extenstion,s_total_channel_price,s_maintanence_price,s_gst_charged,s_final_price;
    // channel section

    private TextView free_chn,paid_chn,total_selected_channel,avaiable_channel,total_channel_slot,total_packages;
    //private String s_free_chn,s_paid_chn,s_total_selected_channel,s_avaiable_channel,s_total_channel_slot,s_total_packages;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet, container, false);

        // calculation section
        base_price = v.findViewById(R.id.base_price_cost);
        slot_extenstion = v.findViewById(R.id.slot_ext_cost);
        total_channel_price = v.findViewById(R.id.total_channel_price_cost);
        maintanence_price = v.findViewById(R.id.maintenance_price_cost);
        gst_charged = v.findViewById(R.id.GST_Price_cost);

        // channel section
        free_chn = v.findViewById(R.id.chn_free_channel_no);
        paid_chn = v.findViewById(R.id.chn_paid_channel_no);
        total_selected_channel = v.findViewById(R.id.chn_selected_no);
        avaiable_channel = v.findViewById(R.id.chn_available_channels_no);
        total_channel_slot = v.findViewById(R.id.chn_total_channel_slot_no);
        total_packages = v.findViewById(R.id.chn_packages_no);


        // totp price heading
        final_price =v.findViewById(R.id.bottomsh_total_price);


//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            current_selection= bundle.getInt("current_selection",2000);
//        }

        new calculate().execute();
        return v;
    }

    public class calculate extends AsyncTask<Void,Void,Void>{

         Double base_price_main = 130.00;  // base price
         Double maintanence_price_main = 0.00; // maintanence price
         Integer slot_price = 0; // slot extenstion price
         Double gst_charges = 0.00;
         Double channel_price = 0.00; // slot extenstion price
         Double total_price = 0.00;
         Integer no_of_channels = 0,available_channel = 100;
         Integer  total_available_channel = 100;

         Integer free = 0,paid = 0 ;
         Double total = 0.00;

         private Integer current_selection;

        @Override
        protected Void doInBackground(Void... voids) {

            Bundle bundle = getArguments();
            if (bundle != null) {
                current_selection= bundle.getInt("current_selection",2000);
            }

            // calculating data from selection DB
            SelectionDbHelper sel = new SelectionDbHelper(getContext());
            //SingleDbHelper single_DB = new SingleDbHelper(getContext());

            ArrayList uidc_list = sel.get_uidc_from_selection(current_selection);

            Log.d("Bottom_sheet","uidc_list.size()"+uidc_list.size());
            // todo get data into bottom sheet
            for(int i=0;i<uidc_list.size();i++){
                SingleDbHelper single_DB = new SingleDbHelper(getContext());
                Double price = single_DB.get_price (uidc_list.get(i).toString().trim());
                Log.d("Bottom_sheet","uidc = "+uidc_list.get(i).toString().trim()+" price = "+single_DB.get_price (uidc_list.get(i).toString().trim()));
                if (price<=0.00){
                    free+=1;
                }else {
                    paid +=1;
                }
                channel_price = channel_price + price;

                no_of_channels = free+paid;
                Log.d("Bottom_sheet","no_of_channels = "+no_of_channels);
                //no_of_channels=101;

                if(no_of_channels>total_available_channel){
                    total_available_channel+=25;
                    slot_price+=20;
                }
            }

            // todo calculate slots

            available_channel= total_available_channel - no_of_channels;
            total_price = base_price_main + maintanence_price_main + slot_price  + channel_price;

            // gst calculation
            gst_charges = (18*total_price) / 100 ;
            // adding gst charges
            total_price = total_price + gst_charges;



            Log.d("Bottom_sheet","free = "+free);
            Log.d("Bottom_sheet","paid = "+paid);
            Log.d("Bottom_sheet","total = "+total);



            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            DecimalFormat precision = new DecimalFormat("0.00");
            // calculation section
            base_price.setText(precision.format(base_price_main));
            slot_extenstion.setText(Integer.toString(slot_price));
            maintanence_price.setText(precision.format(maintanence_price_main));
            gst_charged.setText(precision.format(gst_charges));
            total_channel_price.setText(precision.format(channel_price));


            free_chn.setText(Integer.toString(free));
            paid_chn.setText(Integer.toString(paid));
            total_selected_channel.setText(Integer.toString(no_of_channels));
            avaiable_channel.setText(Integer.toString(available_channel));
            total_channel_slot.setText(Integer.toString(total_available_channel));
            total_packages.setText("0");

            final_price.setText(precision.format(total_price));
        }
    }

}