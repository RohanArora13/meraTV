package channel.price.calculator.meratv.selection;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import androidx.core.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import channel.price.calculator.meratv.R;
import channel.price.calculator.meratv.selection.SelectionContract.SelectionEntry;

public class SelectionFragment extends Fragment {
    private static final String TAG = "SelectionFragment";

    private RecyclerView recyclerView;
    private Spinner filterSpinner;
    private ImageButton clearFilterBtn;

    private SelectionAdapter adapter;
    private SelectionDbHelper dbHelper;
    private ArrayList<Selection> data;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_selection, container, false);
        //setContentView(R.layout.activity_selection);

        recyclerView = view.findViewById(R.id.selection_recycler_view);
        filterSpinner = view.findViewById(R.id.selection_filter_spinner);
        clearFilterBtn = view.findViewById(R.id.selection_clear_filter);

        dbHelper = new SelectionDbHelper(getContext());
        data = new ArrayList<>();

        fetchFromDatabase();

        adapter = new SelectionAdapter(getContext(), data);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();

                // getting the position of clicked item
                int position = holder.getAdapterPosition();

                // getting data associated to that position
                Selection currentData = data.get(position);

                // getting selection uid
                int selectionUid = currentData.getSelectionUid();
                Log.d(TAG, "onClick: View clicked with selection id::" + selectionUid);
                // TODO: code for what would happen on clicking box
            }
        });

        // setup spinner adapter for the filter spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.month_names, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // if position != 0 then filter by the month name
                if (position != 0) {
                    adapter.getFilter().filter(getResources().getStringArray(R.array.month_names)[position]);
                } else {
                    // else show all dara
                    adapter.getFilter().filter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                adapter.getFilter().filter(null);
            }
        });

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(filterSpinner);

            // Set popupWindow height to 500px
            popupWindow.setHeight(700);
        }
        catch (Exception e) {
        }

        clearFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clearing filter
                filterSpinner.setSelection(0);
            }
        });

        return view;
    }

    private void fetchFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String projection[] = {
                SelectionEntry.COLUMN_SELECTION_UID,
                SelectionEntry.COLUMN_ENTRY_UID,
                SelectionEntry.COLUMN_NAME,
                SelectionEntry.COLUMN_TOTAL_PRICE,
                SelectionEntry.COLUMN_MTN_CHARGE,
                SelectionEntry.COLUMN_GST,
                SelectionEntry.COLUMN_TIME,
                SelectionEntry.COLUMN_MONTHLY_REPORT

        };

        Cursor cursor = db.query(SelectionEntry.TABLE_NAME, projection, null, null, null, null, null);

        // getting column index of all columns of selection db
        int selectionUidColumnIndex = cursor.getColumnIndex(SelectionEntry.COLUMN_SELECTION_UID);
        int entryUidColumnIndex = cursor.getColumnIndex(SelectionEntry.COLUMN_ENTRY_UID);
        int nameColumnIndex = cursor.getColumnIndex(SelectionEntry.COLUMN_NAME);
        int totalPriceColumnIndex = cursor.getColumnIndex(SelectionEntry.COLUMN_TOTAL_PRICE);
        int mtnChargeColumnIndex = cursor.getColumnIndex(SelectionEntry.COLUMN_MTN_CHARGE);
        int gstColumnIndex = cursor.getColumnIndex(SelectionEntry.COLUMN_GST);
        int timeColumnIndex = cursor.getColumnIndex(SelectionEntry.COLUMN_TIME);
        int monthlyReportColumnIndex = cursor.getColumnIndex(SelectionEntry.COLUMN_MONTHLY_REPORT);

        if (cursor.moveToFirst()) {
            do {
                // getting values from those columns
                int selectionUid = cursor.getInt(selectionUidColumnIndex);
                String entryUid = cursor.getString(entryUidColumnIndex);
                String name = cursor.getString(nameColumnIndex);
                double totalPrice = cursor.getDouble(totalPriceColumnIndex);
                double mtnCharge = cursor.getDouble(mtnChargeColumnIndex);
                boolean gst = cursor.getInt(gstColumnIndex) > 0;
                long time = cursor.getLong(timeColumnIndex);
                int monthlyReport = cursor.getInt(monthlyReportColumnIndex);

                Selection currentData = new Selection(
                        selectionUid,
                        new String[]{entryUid},
                        name,
                        totalPrice,
                        mtnCharge,
                        gst,
                        time,
                        monthlyReport
                );
                data.add(currentData);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
}
