package ru.apps4yourlife.life.lifebalance.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ru.apps4yourlife.life.lifebalance.R;
import ru.apps4yourlife.life.lifebalance.Utilities.GeneralHelper;

/**
 * Created by ksharafutdinov on 27-Mar-18.
 */

public class LifeBalanceDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "lifebalance.db";
    public static final int DATABASE_VERSION = 1;

    private Context mContext;

    public LifeBalanceDBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        mContext = context;
    }

    public void CreateTables(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_EVENTS_TABLE =
                "CREATE TABLE " +
                        LifeBalanceContract.EventsEntry.TABLE_NAME + "(" +
                        LifeBalanceContract.EventsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        LifeBalanceContract.EventsEntry.COLUMN_DATE + " INTEGER, " +
                        LifeBalanceContract.EventsEntry.COLUMN_DESCRIPTION + " VARCHAR(1024) " +
                        ")";


        sqLiteDatabase.execSQL(SQL_CREATE_EVENTS_TABLE);
        Log.d("DB", "TABLES WERE CREATED");
    }

    public void InsertInitialEvent(SQLiteDatabase db) {
        String[] happenedEvents = mContext.getResources().getStringArray(R.array.events);
        //db.beginTransaction();
        try {
            for (String eventDescription : happenedEvents) {
                ContentValues values = new ContentValues();
                values.put(LifeBalanceContract.EventsEntry.COLUMN_DESCRIPTION, eventDescription);
                values.put(LifeBalanceContract.EventsEntry.COLUMN_DATE, String.valueOf(GeneralHelper.GetCurrentDate().getTime()));
                db.insert(LifeBalanceContract.EventsEntry.TABLE_NAME, null, values);
            }
        } finally {
            //db.endTransaction();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        CreateTables(sqLiteDatabase);

        InsertInitialEvent(sqLiteDatabase);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
