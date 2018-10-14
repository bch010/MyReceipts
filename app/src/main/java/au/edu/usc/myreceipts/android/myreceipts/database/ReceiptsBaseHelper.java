package au.edu.usc.myreceipts.android.myreceipts.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReceiptsBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 5;
    private static final String DATABASE_NAME = "myReceiptsBase.db";

    public ReceiptsBaseHelper(Context context) {

        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ReceiptsDbSchema.ReceiptsTable.NAME + "(" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReceiptsDbSchema.ReceiptsTable.Cols.UUID + " INTEGER, " +
                ReceiptsDbSchema.ReceiptsTable.Cols.TITLE + " TEXT, " +
                ReceiptsDbSchema.ReceiptsTable.Cols.DATE + " INTEGER, " +
                ReceiptsDbSchema.ReceiptsTable.Cols.SHOPNAME + " TEXT, " +
                ReceiptsDbSchema.ReceiptsTable.Cols.COMMENTS + " TEXT, " +
                ReceiptsDbSchema.ReceiptsTable.Cols.RECEIPTSENT + " INTEGER, " +
                ReceiptsDbSchema.ReceiptsTable.Cols.LOCATION +
                ")"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
