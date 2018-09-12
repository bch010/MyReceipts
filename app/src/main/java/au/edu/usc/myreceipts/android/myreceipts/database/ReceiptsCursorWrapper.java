package au.edu.usc.myreceipts.android.myreceipts.database;


import android.database.Cursor;
import android.database.CursorWrapper;



import java.util.Date;
import java.util.UUID;

import au.edu.usc.myreceipts.android.myreceipts.MyReceipts;

public class ReceiptsCursorWrapper extends CursorWrapper {

    public ReceiptsCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public MyReceipts getMyReceipts() {

        String uuidString = getString(getColumnIndex(ReceiptsDbSchema.ReceiptsTable.Cols.UUID));
        String title = getString(getColumnIndex(ReceiptsDbSchema.ReceiptsTable.Cols.TITLE));
        long date = getLong(getColumnIndex(ReceiptsDbSchema.ReceiptsTable.Cols.DATE));
        String shopname = getString(getColumnIndex(ReceiptsDbSchema.ReceiptsTable.Cols.SHOPNAME));
        int isSolved = getInt(getColumnIndex(ReceiptsDbSchema.ReceiptsTable.Cols.SOLVED));


        MyReceipts myReceipts = new MyReceipts(UUID.fromString(uuidString));
        myReceipts.setTitle(title);
        myReceipts.setDate(new Date(date));
        myReceipts.setShopName(shopname);
        myReceipts.setSolved(isSolved != 0);
        return myReceipts;
    }

}

