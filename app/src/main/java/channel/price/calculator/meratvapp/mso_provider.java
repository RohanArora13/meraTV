/*******************************************************************************
 * Copyright (C) Rohan Arora - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Rohan Arora <rohanarora1313@gmail.com> on 12/1/19 11:33 AM
 *
 * Last modified 12/1/19 11:33 AM
 *
 ******************************************************************************/

package channel.price.calculator.meratvapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class mso_provider extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_mso_options);

        Intent intent = getIntent();
        String selected_mso = intent.getStringExtra("selected_mso");


        // dth
        if(selected_mso.equals("1")){



        }

        //cable
        else if(selected_mso.equals("2")){



        }


        // both
        else if(selected_mso.equals("3")){


        }
    }
}
