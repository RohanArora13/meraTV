/*******************************************************************************
 * Copyright (C) Rohan Arora - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Rohan Arora <rohanarora1313@gmail.com> on 12/1/19 11:24 AM
 *
 * Last modified 12/1/19 11:24 AM
 *
 ******************************************************************************/

package channel.price.calculator.meratvapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class selection_data {

    //    for MSO_ selection
    private Boolean dtn_select; // 1
    private Boolean cable_select; // 2
    private Boolean both_selection; // 3




    public static String readFromFile(String path) {
        String ret = "";
        try {
            InputStream inputStream = new FileInputStream(new File(path));

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("FileToJson", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("FileToJson", "Can not read file: " + e.toString());
        }
        return ret;
    }




    //    for MSO_ selection
    public selection_data(Boolean dtn_select, Boolean cable_select, Boolean both_selection) {
        this.dtn_select = dtn_select;
        this.cable_select = cable_select;
        this.both_selection = both_selection;

    }

//  for MSO_ selection update json
    public void setDtn_select(Boolean dtn_select) {
        this.dtn_select = dtn_select;

    }
    public void setCable_select(Boolean cable_select) {
        this.cable_select = cable_select;

    }
    public void setBoth_selection(Boolean both_selection) {
        this.both_selection = both_selection;

    }



//  for MSO_ selection from  json
    public Boolean getDtn_select() {
        return dtn_select;
    }
    public Boolean getCable_select() {
        return cable_select;
    }
    public Boolean getBoth_selection() {
        return both_selection;
    }




}
