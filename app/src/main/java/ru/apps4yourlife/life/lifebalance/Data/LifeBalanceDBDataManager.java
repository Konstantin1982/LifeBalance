package ru.apps4yourlife.life.lifebalance.Data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;

import ru.apps4yourlife.life.lifebalance.R;
import ru.apps4yourlife.life.lifebalance.Utilities.GeneralHelper;


public class LifeBalanceDBDataManager {
    private LifeBalanceDBHelper mDBHelper;
    private Context mContext;






    public LifeBalanceDBDataManager(Context context) {
        mDBHelper = new LifeBalanceDBHelper(context);
        mContext = context;
        //mDBHelper.getWritableDatabase(); // just to fix crash
    }

    public void deleteDatabase() {
        mContext.deleteDatabase(mDBHelper.getDatabaseName());
    }


    /*
        *******************************************
                EVENTS SECTION
        *******************************************
    */

    public Cursor GetEvents(int limit) {

        Cursor events = mDBHelper.getReadableDatabase().query(
                LifeBalanceContract.EventsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                LifeBalanceContract.EventsEntry.COLUMN_DATE + " DESC",
                String.valueOf(limit)
        );
        return events;
    }

    public static long InsertOrUpdateEvent(SQLiteDatabase db, String idEntry, long eventDate, String eventDescription) {
        long result = 0;
        ContentValues values = new ContentValues();
        values.put(LifeBalanceContract.EventsEntry.COLUMN_DESCRIPTION, eventDescription);
        values.put(LifeBalanceContract.EventsEntry.COLUMN_DATE, String.valueOf(GeneralHelper.GetCurrentDate().getTime()));
        if (idEntry == null) {
            result = db.insert(LifeBalanceContract.EventsEntry.TABLE_NAME, null, values);
        } else {
            result = db.update(LifeBalanceContract.EventsEntry.TABLE_NAME, values, LifeBalanceContract.EventsEntry._ID + " = ? ", new String[]{idEntry});
            if (result > 0) result = Long.getLong(idEntry);
        }
        return result;
    }

    public long InsertOrUpdateEvent(String idEntry, long eventDate, String eventDescription) {
        return  this.InsertOrUpdateEvent(mDBHelper.getWritableDatabase(), idEntry, eventDate, eventDescription);
    }


    /*
        *******************************************
                MESSAGES SECTION
        *******************************************
    */

    public Cursor GetMessages(int limit) {
        Cursor messages = mDBHelper.getReadableDatabase().query(
                LifeBalanceContract.MessagesEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                LifeBalanceContract.MessagesEntry.COLUMN_SENT_DATE + " DESC",
                String.valueOf(limit)
        );
        return messages;
    }

    public static long InsertOrUpdateMessage(SQLiteDatabase db, String idEntry, String from, String to, String subject, String body, int isnew, long messageDate) {
        long result = 0;

        ContentValues values = new ContentValues();
        values.put(LifeBalanceContract.MessagesEntry.COLUMN_FROM, from);
        values.put(LifeBalanceContract.MessagesEntry.COLUMN_TO, to);
        values.put(LifeBalanceContract.MessagesEntry.COLUMN_SUBJECT, subject);
        values.put(LifeBalanceContract.MessagesEntry.COLUMN_BODY, body);
        values.put(LifeBalanceContract.MessagesEntry.COLUMN_IS_NEW, isnew);
        values.put(LifeBalanceContract.MessagesEntry.COLUMN_SENT_DATE, messageDate);
        if (idEntry == null) {
            result = db.insert(LifeBalanceContract.MessagesEntry.TABLE_NAME, null, values);
        } else {
            result = db.update(LifeBalanceContract.MessagesEntry.TABLE_NAME, values, LifeBalanceContract.MessagesEntry._ID + " = ? ", new String[]{idEntry});
            if (result > 0) result = Long.getLong(idEntry);
        }
        return result;
    }

    public long InsertOrUpdateMessage(String idEntry, String from, String to, String subject, String body, int isnew, long messageDate) {
        return this.InsertOrUpdateMessage(mDBHelper.getWritableDatabase(), idEntry, from, to, subject, body, isnew, messageDate);
    }

    /*
        *******************************************
                WISHES SECTION
        *******************************************
    */
    public Cursor GetOpenedWishes() {
        Cursor wishes = mDBHelper.getReadableDatabase().query(
                LifeBalanceContract.WishesEntry.TABLE_NAME,
                null,
                LifeBalanceContract.WishesEntry.COLUMN_STATUS + " < ? ",
                new String[]{String.valueOf(GeneralHelper.WishStatusesClass.WISH_STATUS_COMPLETE)},
                null,
                null,
                LifeBalanceContract.WishesEntry.COLUMN_START + " DESC",
                null
        );
        return wishes;
    }

    public int GetFearWishStatus(long wishId) {
        int status = 0;
        Cursor fear = mDBHelper.getReadableDatabase().query(
                LifeBalanceContract.FearsEntry.TABLE_NAME,
                null,
                LifeBalanceContract.FearsEntry.COLUMN_WISH_ID + " = ? ",
                new String[]{String.valueOf(wishId)},
                null,
                null,
                null,
                null
        );

        if (fear.getCount() > 0) {
            fear.moveToFirst();
            status = fear.getInt(fear.getColumnIndex(LifeBalanceContract.FearsEntry.COLUMN_STATUS));
        }

        return status;
    }

    public long GetFearWishId(long wishId) {
        long id = -1;
        Cursor fear = mDBHelper.getReadableDatabase().query(
                LifeBalanceContract.FearsEntry.TABLE_NAME,
                null,
                LifeBalanceContract.FearsEntry.COLUMN_WISH_ID + " = ? ",
                new String[]{String.valueOf(wishId)},
                null,
                null,
                null,
                null
        );

        if (fear.getCount() > 0) {
            fear.moveToFirst();
            id = fear.getLong(fear.getColumnIndex(LifeBalanceContract.FearsEntry._ID));
        }
        return id;
    }

    public long InsertOrUpdateFearsStatus(long wishId, int fearsStatus) {
        long result = 0;

        long id = GetFearWishId(wishId);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LifeBalanceContract.FearsEntry.COLUMN_STATUS, fearsStatus);
        values.put(LifeBalanceContract.FearsEntry.COLUMN_WISH_ID, wishId);
        if (id <= 0) {
            result = db.insert(LifeBalanceContract.FearsEntry.TABLE_NAME, null, values);
        } else {
            result = db.update(LifeBalanceContract.FearsEntry.TABLE_NAME, values, LifeBalanceContract.FearsEntry._ID + " = ? ", new String[]{String.valueOf(id)});
            if (result > 0) result = id;
        }
        return result;
    }

    public String GetReviewForWish(long wishId, int mode) {
        String result = "";
        // mode == 0 - review
        // mode == 1 - sit review


        return result;
    }

    public Cursor GetWishesTypesWithChecked(ArrayList<Integer> selectedItems) {

        /*
        Cursor types = mDBHelper.getReadableDatabase().query(
                LifeBalanceContract.WishesTypesEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                LifeBalanceContract.WishesTypesEntry._ID,
                null
        );
        */
        String itemString = "(";
        for (int item : selectedItems) {
            itemString += item + ",";
        }
        itemString += "-1)";

        Cursor types = mDBHelper.getReadableDatabase().rawQuery(
                "select description, _id, CASE WHEN _id IN " + itemString + "THEN 1 ELSE 0 END AS CHECKED from " + LifeBalanceContract.WishesTypesEntry.TABLE_NAME,
                null);
        types.moveToFirst();
        return types;
    }

    public Cursor GetWishById(String id) {
        Cursor wish = mDBHelper.getReadableDatabase().query(
                LifeBalanceContract.WishesEntry.TABLE_NAME,
                null,
                LifeBalanceContract.WishesEntry._ID + " = ? ",
                new String[]{id},
                null,
                null,
                null,
                null
        );
        if (wish.getCount() > 0) {
            wish.moveToPosition(0);
        }
        return wish;
    }

    public long GetCountOpenedWishes() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        long result = DatabaseUtils.queryNumEntries(
                db,
                LifeBalanceContract.WishesEntry.TABLE_NAME,
                LifeBalanceContract.WishesEntry.COLUMN_STATUS + " < ? ",
                new String[]{String.valueOf(GeneralHelper.WishStatusesClass.WISH_STATUS_COMPLETE)});
        return result;
    }
    public static long InsertOrUpdateWish(SQLiteDatabase db,
                                          String idEntry,
                                          String types,
                                          long start,
                                          long planend,
                                          long factend,
                                          int status,
                                          String description,
                                          String situation) {
        long result = 0;
        ContentValues values = new ContentValues();
        values.put(LifeBalanceContract.WishesEntry.COLUMN_TYPE, types);
        values.put(LifeBalanceContract.WishesEntry.COLUMN_START, start);
        values.put(LifeBalanceContract.WishesEntry.COLUMN_PLAN_END, planend);
        values.put(LifeBalanceContract.WishesEntry.COLUMN_FACT_END, factend);
        values.put(LifeBalanceContract.WishesEntry.COLUMN_STATUS, status);
        values.put(LifeBalanceContract.WishesEntry.COLUMN_DESCRIPTION, description);
        values.put(LifeBalanceContract.WishesEntry.COLUMN_SITUATION, situation);
        if (idEntry == null) idEntry = "0";
        if (idEntry.equalsIgnoreCase("0")  || idEntry.equalsIgnoreCase("-1")) {
            result = db.insert(LifeBalanceContract.WishesEntry.TABLE_NAME, null, values);
        } else {
            result = db.update(LifeBalanceContract.WishesEntry.TABLE_NAME, values, LifeBalanceContract.WishesEntry._ID + " = ? ", new String[]{idEntry});
            //if (result > 0) result = Long.getLong(idEntry);
        }
        return result;
    }

    public static long InsertOrUpdateWishType(SQLiteDatabase db,
                                          String idEntry,
                                          String type) {
        long result = 0;
        ContentValues values = new ContentValues();
        values.put(LifeBalanceContract.WishesEntry.COLUMN_DESCRIPTION, type);
        if (idEntry == null) {
            result = db.insert(LifeBalanceContract.WishesTypesEntry.TABLE_NAME, null, values);
        } else {
            result = db.update(LifeBalanceContract.WishesTypesEntry.TABLE_NAME, values, LifeBalanceContract.WishesEntry._ID + " = ? ", new String[]{idEntry});
        }
        return result;
    }

    public long InsertOrUpdateWish(String idEntry, String types, long start, long planend, long factend, int status, String description, String situation) {
        return this.InsertOrUpdateWish(mDBHelper.getWritableDatabase(), idEntry, types, start, planend, factend, status, description, situation);
    }

/*
    public Cursor GetAllSizesTypes() {

        Cursor sizeTypes = mDBHelper.getReadableDatabase().query(
                LifeBalanceContract.SizesTypes.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                LifeBalanceContract.SizesTypes.COLUMN_ID);
        return sizeTypes;
    }

    public long GetCountItemsInCategory(long catId) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        long result = DatabaseUtils.queryNumEntries(
                db,
                LifeBalanceContract.ClothesItem.TABLE_NAME,
                LifeBalanceContract.ClothesItem.COLUMN_CAT_ID + " = ? ",
                new String[]{String.valueOf(catId)});
        return result;
    }

    public int GetSizeIdByFilter(int type, double value, int condition) {
        int result = -1;
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String sql  = "";
        sql = "select * from sizes where real_value <= ? and size_type = ? order by real_value desc limit 1;";
        String[] selectionArgs = new String[] {String.valueOf(value), String.valueOf(type)};
        Cursor cursor = db.rawQuery(sql,selectionArgs);
        if (cursor.getCount() > 0) {
            cursor.moveToPosition(0);
            result = cursor.getInt(cursor.getColumnIndex("_id"));
        }
        return result;
    }
*/
}
