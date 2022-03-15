/*******************************************************************************
 * Copyright (C) Rohan Arora - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Rohan Arora <rohanarora1313@gmail.com> on 30/12/18 3:37 PM
 *
 * Last modified 30/12/18 3:37 PM
 *
 ******************************************************************************/

package channel.price.calculator.meratvapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class CustomAdapter extends ArrayAdapter<listdata> {

    private ArrayList<listdata> dataSet = new ArrayList<>();
//    private String name;
//    private String price;
//    private String uidc;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView Pricetxt;
        TextView uidc;

    }

    public CustomAdapter(ArrayList<listdata> list, Context context) {
        super(context, R.layout.row_item, list);
        //this.dataSet = project.mrp.mrp.data;
//        this.name = data.;
//        this.price = price;
//        this.uidc = uidc;
        dataSet= list;
        mContext = context;

    }

//    @Nullable
//    @Override
//    public String getItem(int position) {
//       // return (String) dataSet.get(position);
//    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        final ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            try {

            // name = ld.getList_name();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);

            Log.d("dataset " , " " +dataSet.get(position));

            //if(dataSet.get(position) != "Item 1A")

                listdata ld = dataSet.get(position);

                viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
                viewHolder.txtName.setText(ld.getList_name());

                viewHolder.Pricetxt = (TextView) convertView.findViewById(R.id.price);
                viewHolder.Pricetxt.setText(ld.getList_price());

                //viewHolder.uidc = (TextView) convertView.findViewById(R.id.uidc);
                viewHolder.uidc.setText(ld.getList_uidc());

                Log.d("dataset ", " value added ");


                convertView.setTag(viewHolder);

                viewHolder.txtName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Toast.makeText(getContext(),"  "+viewHolder.Pricetxt.getText(),Toast.LENGTH_SHORT).show();
                        MainActivity ma = new MainActivity();
                        ArrayList list = ma.get_select_c();
                        String selected_uidc = viewHolder.uidc.getText().toString();
                        if(list!=null && list.contains(selected_uidc)){
                          //  viewHolder.txtName.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
                            Log.d("dataset","uidc already exsit removing it ");
                            ma.remove_UIDC(selected_uidc);
                        }
                        else {
                          //  viewHolder.txtName.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
                            // MainActivity ma = new MainActivity();
                            Log.d("dataset", "adding  uidc = " + selected_uidc);
                            ma.add_UIDC_select(selected_uidc);
                        }
                    }
                });

            }catch (Exception e){

            }
        }

        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //viewHolder.txtName.setText(getItem(position));
//        viewHolder.txtName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               // Toast.makeText(getContext(),"  "+viewHolder.Pricetxt.getText(),Toast.LENGTH_SHORT).show();
//                parent.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
//                MainActivity ma = new MainActivity();
//                ma.add_UIDC_select(viewHolder.uidc.getText().toString());
//            }
//        });
       // convertView.setTag(viewHolder);

        // Return the completed view to render on screen

        return convertView;

    }

}
