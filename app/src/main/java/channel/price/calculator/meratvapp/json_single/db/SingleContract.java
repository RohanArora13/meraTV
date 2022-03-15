
package channel.price.calculator.meratvapp.json_single.db;

public final class SingleContract {
    // To prevent someone from accidentally instantiating the contract class
    private SingleContract() {
    }

    /**
     * Inner class that defines constant values for the chn database table.
     */
    public static final class SingleEntry {

        // TODO: change table name here for chn.db database
        public final static String TABLE_NAME = "chnmain";

        // Column Names
        public final static String UIDC = "UIDC";

        public final static String COLUMN_NAME = "name";

        public final static String COLUMN_PRICE = "price";

        public final static String COLUMN_SDHD = "SDHD";

        public final static String COLUMN_TIME = "time";

    }
}
