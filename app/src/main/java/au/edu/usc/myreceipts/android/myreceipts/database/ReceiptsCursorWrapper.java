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
        String comments = getString(getColumnIndex(ReceiptsDbSchema.ReceiptsTable.Cols.COMMENTS));
        int isReceiptSent = getInt(getColumnIndex(ReceiptsDbSchema.ReceiptsTable.Cols.RECEIPTSENT));
        String receipt = getString(getColumnIndex(ReceiptsDbSchema.ReceiptsTable.Cols.RECEIPT));


        MyReceipts myReceipts = new MyReceipts(UUID.fromString(uuidString));
        myReceipts.setTitle(title);
        myReceipts.setDate(new Date(date));
        myReceipts.setShopName(shopname);
        myReceipts.setComments(comments);
        myReceipts.setReceiptSent(isReceiptSent != 0);
        myReceipts.setReceipt(receipt);
        return myReceipts;
    }

}

