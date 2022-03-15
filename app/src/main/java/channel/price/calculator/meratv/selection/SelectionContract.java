package channel.price.calculator.meratv.selection;

import android.provider.BaseColumns;

public class SelectionContract {
    private SelectionContract() {
    }

    public class SelectionEntry implements BaseColumns {
        public static final String TABLE_NAME="select_table";

        // column names
        public static final String COLUMN_SELECTION_UID="selection_uid";
        public static final String COLUMN_ENTRY_UID="entry_uids";
        public static final String COLUMN_NAME="name";
        public static final String COLUMN_TOTAL_PRICE="total_price";
        public static final String COLUMN_MTN_CHARGE="mtn_charge";
        public static final String COLUMN_GST="GST";
        public static final String COLUMN_TIME="time";
        public static final String COLUMN_MONTHLY_REPORT="monthly_report";

    }
}
