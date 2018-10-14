package au.edu.usc.myreceipts.android.myreceipts;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import au.edu.usc.myreceipts.android.myreceipts.database.ReceiptsBaseHelper;
import au.edu.usc.myreceipts.android.myreceipts.database.ReceiptsCursorWrapper;
import au.edu.usc.myreceipts.android.myreceipts.database.ReceiptsDbSchema;

/**
 * Stores data as a singleton while application is in memory. Will be destroyed after
 * app is stopped and memory is released...
 */

public class MyReceiptsObjects {
    private static MyReceiptsObjects sMyReceiptsObjects;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    /**
     * @param context of the application state
     * @return existing or new MyReceiptsObjects (If one doesn't exist)
     */
    public static MyReceiptsObjects get(Context context) {
        if (sMyReceiptsObjects == null) {
            sMyReceiptsObjects = new MyReceiptsObjects(context);
        }

        return sMyReceiptsObjects;
    }

    // Adds a receipt to the list of receipts
    public void addMyReceipts(MyReceipts c) {
        ContentValues values = getContentValues(c);

        mDatabase.insert(ReceiptsDbSchema.ReceiptsTable.NAME, null, values);
    }

    public void updateMyReceipts(MyReceipts myReceipts) {
        String uuidString = myReceipts.getId().toString();
        ContentValues values = getContentValues(myReceipts);

        mDatabase.update(ReceiptsDbSchema.ReceiptsTable.NAME, values,
                ReceiptsDbSchema.ReceiptsTable.Cols.UUID + "=?",
                new String[]{uuidString});
    }

    //delete myReceipts method from db .. called from myReceiptsfragment
    public void deleteMyReceipts(MyReceipts myReceipts) {
        mDatabase.delete(
                ReceiptsDbSchema.ReceiptsTable.NAME,
                ReceiptsDbSchema.ReceiptsTable.Cols.UUID + "=?",
                new String[] {myReceipts.getId().toString()}
        );
    }

    private ReceiptsCursorWrapper queryReceipts(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ReceiptsDbSchema.ReceiptsTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );

        return new ReceiptsCursorWrapper(cursor);
    }

    public List<MyReceipts> getMyReceipts() {
        List<MyReceipts> myReceipts = new ArrayList<>();

        ReceiptsCursorWrapper cursor = queryReceipts(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                myReceipts.add(cursor.getMyReceipts());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return myReceipts;
    }

    public MyReceipts getMyReceipt(UUID id) {
        // otherwise no receipts by that id
        ReceiptsCursorWrapper cursor = queryReceipts(
                ReceiptsDbSchema.ReceiptsTable.Cols.UUID + "=?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getMyReceipts();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(MyReceipts myReceipts) {

        File filesDir = mContext.getFilesDir();
//        if (filesDir == null) {
//            return null;
//        }
        return new File(filesDir, myReceipts.getPhotoFilename());
    }

    private static ContentValues getContentValues(MyReceipts myReceipts) {
        ContentValues values = new ContentValues();
        values.put(ReceiptsDbSchema.ReceiptsTable.Cols.UUID, myReceipts.getId().toString());
        values.put(ReceiptsDbSchema.ReceiptsTable.Cols.TITLE, myReceipts.getTitle());
        values.put(ReceiptsDbSchema.ReceiptsTable.Cols.DATE, myReceipts.getDate().getTime());
        values.put(ReceiptsDbSchema.ReceiptsTable.Cols.SHOPNAME, myReceipts.getShopName());
        values.put(ReceiptsDbSchema.ReceiptsTable.Cols.COMMENTS, myReceipts.getComments());
        values.put(ReceiptsDbSchema.ReceiptsTable.Cols.RECEIPTSENT, myReceipts.isReceiptSent() ? 1 : 0);
        values.put(ReceiptsDbSchema.ReceiptsTable.Cols.LOCATION, myReceipts.getLocation());
        return values;
    }

    /**
     * Private constructor prevents more than one instance
     * and can only be created from this class
     */
    private MyReceiptsObjects(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ReceiptsBaseHelper(mContext).getWritableDatabase();
    }
}