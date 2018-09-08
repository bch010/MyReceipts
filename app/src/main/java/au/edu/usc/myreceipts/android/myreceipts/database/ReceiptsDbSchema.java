package au.edu.usc.myreceipts.android.myreceipts.database;


public class ReceiptsDbSchema {
    public static final class ReceiptsTable {

        public static final String NAME = "receipts";

            // database schema
            public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
        }
    }
}
