package channel.price.calculator.meratv;

import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class mso_selection extends AppCompatActivity {

    private LinearLayout dth_lay,cable_lay,both_lay;

    /**
     * 1 = dth
     *
     * 2 = cable
     *
     * 3 = both
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mso_selection);
//
//        dth_lay  = findViewById(R.id.button_dtn);
//
//        cable_lay = findViewById(R.id.button_cable);
//
//        both_lay = findViewById(R.id.button_both);

        dth_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goto_mso_provider("1");
            }
        });

        cable_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goto_mso_provider("2");

            }
        });

        both_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goto_mso_provider("3");

            }
        });

    }

    public void goto_mso_provider(String id){
        Intent mso_int;
        mso_int = new Intent(this,mso_provider.class);
        mso_int.putExtra("selected_mso",id);
        this.startActivity(mso_int);
        finish();
    }


}
