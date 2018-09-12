package au.edu.usc.myreceipts.android.myreceipts.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReceiptsBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "myReceiptsBase2.db";

    public ReceiptsBaseHelper(Context context) {


        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + ReceiptsDbSchema.ReceiptsTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                ReceiptsDbSchema.ReceiptsTable.Cols.UUID + ", " +
                ReceiptsDbSchema.ReceiptsTable.Cols.TITLE + ", " +
                ReceiptsDbSchema.ReceiptsTable.Cols.DATE + ", " +
                ReceiptsDbSchema.ReceiptsTable.Cols.SHOPNAME + ", " +
               ReceiptsDbSchema.ReceiptsTable.Cols.SOLVED +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
