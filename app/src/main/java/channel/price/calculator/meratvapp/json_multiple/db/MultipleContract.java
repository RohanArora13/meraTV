package channel.price.calculator.meratvapp.json_multiple.db;


public final class MultipleContract {
    // To prevent someone from accidentally instantiating the contract class
    private MultipleContract() {
    }

    /**
     * Inner class that defines constant values for the pkg database table.
     */
    public static final class MultipleEntry {

        // TODO: change table name here for pkg.db database
        public final static String TABLE_NAME = "pkgmain";

        // Column Names
        public final static String UIDP = "UIDP";

        public final static String COLUMN_NAME = "name";

        public final static String COLUMN_PRICE = "price";

        public final static String COLUMN_TIME = "time";

        public final static String COLUMN_DATAENTRY = "dataentry";

    }
}
