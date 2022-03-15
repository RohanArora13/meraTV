package channel.price.calculator.meratv;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import androidx.core.app.Fragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import android.widget.SearchView;
import android.widget.Spinner;


import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.CustomIndicator;
import com.turingtechnologies.materialscrollbar.TouchScrollBar;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import channel.price.calculator.meratv.json_single.db.SingleContract.SingleEntry;
import channel.price.calculator.meratv.json_single.db.SingleDbHelper;

public class MeFragment extends Fragment {
    private static final String TAG = "MeFragment";

    private channel_fragment_listner listener;


    private RecyclerView recyclerView;
    private SearchView searchView;
    private Spinner sortButton;
    private TouchScrollBar scrollBar;
    private SingleDbHelper dbHelper;
    private ArrayList<SingleItemModel> data;
    private MeRecyclerAdapter adapter;
    private Bundle all_data =  new Bundle();
    private Integer free_chn = 0,paid_chn = 0;
    double totalPrice = 0.00;
    private ArrayList<Integer> uidc_list = new ArrayList<Integer>() ;

    public interface channel_fragment_listner {
        void onInput_channelFragment(Bundle input);
    }

    public interface uidc_list{
        void onUidc_list(ArrayList uidc_list);
    }


    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView started  ");

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.channel_fragment, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        searchView = view.findViewById(R.id.search_view);
        sortButton = view.findViewById(R.id.sort_btn);
        scrollBar = view.findViewById(R.id.me_scroll_bar);

        dbHelper = new SingleDbHelper(getContext());
        data = new ArrayList<>();

        // fetch data from database
        fetchFromDatabase();

        // to setup sorting spinner for data
        setupSort();

//        for (int i = 0; i < 50; i++) {
//
//            data.add(new SingleItemModel(123, "qwer", 20, "sf"));
//
//            data.add(new SingleItemModel(456, "asdf", 10, "sf"));
//
//            data.add(new SingleItemModel(789, "zxcv", 50, "sf"));
//
//        }


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // setting up adapter for recycler view
        adapter = new MeRecyclerAdapter(getContext(), data);
        recyclerView.setAdapter(adapter);

        // setting up search feature
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText, new Filter.FilterListener() {
                    // on search complete again sort them as selected
                    @Override
                    public void onFilterComplete(int count) {
                        if (sortButton.getSelectedItemPosition() == 0) {
                            sortByName();
                        } else if (sortButton.getSelectedItemPosition() == 1) {
                            sortByPriceLowToHigh();
                        } else if (sortButton.getSelectedItemPosition() == 2) {
                            sortByPriceHighToLow();
                        }
                    }
                });
                return false;
            }
        });

        // click listener for each item in recycler view
        adapter.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();

                // getting the position of clicked item
                int position = holder.getAdapterPosition();

                // getting data associated to that position
                SingleItemModel currentData = data.get(position);

                // setting selection status of current data (select that if not)
                currentData.setSelected(!currentData.isSelected());

                // TODO: change the color here for selected item
                holder.itemView.setBackgroundColor(currentData.isSelected() ?  getResources().getColor(R.color.green_teal_300) : Color.WHITE);
              //  currentData.isSelected() ? R.color.text_color : Color.WHITE


                totalPrice = 0.0;
                // getting total price of selected data
                for (SingleItemModel model : data) {
                    if (model.isSelected()) {
                        totalPrice += model.getPrice();
                        Log.d(TAG, "model.getUidc() " + model.getUidc());
                        //uidc_list.add(model.getUidc());
                        //todo add selection
                    }
                }
                // send data to main activity
                if(currentData.isSelected()){
                    Log.d(TAG, " if(currentData.isSelected()){ ");
                    //uidc_list.add(model.getUidc());
                    // add in listener
                    all_data.putInt("channel_uidc_add",currentData.getUidc());
                    all_data.putInt("channel_uidc_remove",0);
                    if(currentData.getPrice()>0.0){
                        paid_chn+=1;
                    }else if(currentData.getPrice()<=0.0){
                        free_chn+=1;
                    }
                }
                else {
                    // remove from listener
                    all_data.putInt("channel_uidc_add",0);
                    all_data.putInt("channel_uidc_remove",currentData.getUidc());
                    if(currentData.getPrice()>0.0){
                        paid_chn-=1;
                    }else if(currentData.getPrice()<=0.0){
                        free_chn-=1;
                    }
                }

                // addding total channel price
                all_data.putDouble("total_channel_price",totalPrice);

               //  adding no of paid free channel
                 all_data.putInt("free_channels",free_chn);
                all_data.putInt("paid_channels",paid_chn);

                // sending data
                listener.onInput_channelFragment(all_data);


                // TOO: getting totalPrice here can do anything with that
                Log.d(TAG, "onCreateView: Total Price: " + totalPrice);
            }


        });

       // clear all selections
        ((MainActivity)getActivity()).setFragmentRefreshListener(new MainActivity.clearSelectionListener() {
            @Override
            public void onclear() {
                Log.d("clear_selection","onclear started ");
                for (SingleItemModel model : data) {
                    if(model.isSelected()){
                        model.setSelected(false);
                        free_chn = 0;
                        paid_chn = 0;
                        uidc_list.clear();
                        adapter.notifyDataSetChanged();

                        all_data.putDouble("total_channel_price",0);
                        totalPrice=0.00;

                        // adding no of paid free channel
                        all_data.putInt("free_channels",free_chn);
                        all_data.putInt("paid_channels",paid_chn);

                        // sending data
                        listener.onInput_channelFragment(all_data);
                    }
                }
            }
        });


        // retrive set selection by taking uidcs from activites
        ((MainActivity)getActivity()).setFragmentSetSelectionListener(new MainActivity.SetSelectionListener() {
            @Override
            public void onRetrive (ArrayList<String> uidc_s) {

                for (SingleItemModel model : data) {
                    // if our uidc list contains uidc of current item select this item
                    Log.d(TAG, " for (SingleItemModel model : data) { " + model.getUidc());
                    try {
                        if (uidc_s.contains(Integer.toString(model.getUidc()))) {
                            Log.d(TAG, "selectRecyclerItem: uidc= " + model.getUidc());
                            model.setSelected(true);
                        }
                        // todo error improvement
                    }catch (Exception e){
                        Log.d(TAG, "selectRecyclerItem: error "+e);
                    }
                }
                adapter.notifyDataSetChanged();
                recyclerView.performClick();
//                all_data.putDouble("total_channel_price",totalPrice);
//                // adding no of paid free channel
//                all_data.putInt("free_channels",free_chn);
//                all_data.putInt("paid_channels",paid_chn);
//                // sending data
//                listener.onInput_channelFragment(all_data);

            }
        });

        Log.d(TAG, " view returned ");
        return view;
    }




    private void setupSort() {
        Log.d(TAG, " setupSort started  ");
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_types, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortButton.setAdapter(spinnerAdapter);

        sortButton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    sortByName();
                } else if (position == 1) {
                    sortByPriceLowToHigh();
                } else {
                    sortByPriceHighToLow();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void sortByPriceLowToHigh() {
        // sort the data by price low to high
        Collections.sort(data, new Comparator<SingleItemModel>() {
            @Override
            public int compare(SingleItemModel lhs, SingleItemModel rhs) {
                return Double.compare(lhs.getPrice(), rhs.getPrice());
            }
        });

        // setting price indicator on sort by price
        scrollBar.setIndicator(new CustomIndicator(getContext()), false);
    }

    private void sortByPriceHighToLow() {
        // sort the data by price high to low
        Collections.sort(data, new Comparator<SingleItemModel>() {
            @Override
            public int compare(SingleItemModel lhs, SingleItemModel rhs) {
                return Double.compare(rhs.getPrice(), lhs.getPrice());
            }
        });
        // setting price indicator on sort by price
        scrollBar.setIndicator(new CustomIndicator(getContext()), false);
    }

    private void sortByName() {
        // sort data by name
        Collections.sort(data, new Comparator<SingleItemModel>() {
            @Override
            public int compare(SingleItemModel lhs, SingleItemModel rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        // setting price indicator on sort by price
        scrollBar.setIndicator(new AlphabetIndicator(getContext()), false);
    }

    public void selectRecyclerItem(List<String> uidcs) {
        // to do check for each item in the recycler view
        for (SingleItemModel model : data) {
            // if our uidc list contains uidc of current item select this item
            try {
                if (uidcs.contains(model.getUidc())) {
                    Log.d(TAG, "selectRecyclerItem: uidc= " + model.getUidc());
                    model.setSelected(true);
                }
            }catch (Exception e){
                Log.d(TAG, "selectRecyclerItem: error "+e);
            }
        }
    }

    private void fetchFromDatabase() {
        Log.d(TAG, " fetchFromDatabase started  ");
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String projection[] = {
                SingleEntry.UIDC,
                SingleEntry.COLUMN_NAME,
                SingleEntry.COLUMN_PRICE,
                SingleEntry.COLUMN_SDHD
        };

        Cursor cursor = db.query(SingleEntry.TABLE_NAME, projection, null, null, null, null, null);

        int uidcColumnIndex = cursor.getColumnIndex(SingleEntry.UIDC);
        int nameColumnIndex = cursor.getColumnIndex(SingleEntry.COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(SingleEntry.COLUMN_PRICE);
        int sdhdColumnIndex = cursor.getColumnIndex(SingleEntry.COLUMN_SDHD);

        if (cursor.moveToFirst()) {
            do {
                int uidc = cursor.getInt(uidcColumnIndex);
                String name = cursor.getString(nameColumnIndex);
                double price = cursor.getDouble(priceColumnIndex);
                String sdhd = cursor.getString(sdhdColumnIndex);

                SingleItemModel currentData = new SingleItemModel(uidc, name, price, sdhd);
                data.add(currentData);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof channel_fragment_listner) {
            listener = (channel_fragment_listner) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentAListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }



}
