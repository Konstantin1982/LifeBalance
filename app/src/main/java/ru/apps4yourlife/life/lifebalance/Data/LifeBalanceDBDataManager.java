package ru.apps4yourlife.life.lifebalance.Data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import ru.apps4yourlife.life.lifebalance.R;
import ru.apps4yourlife.life.lifebalance.Utilities.GeneralHelper;


public class LifeBalanceDBDataManager {
    private LifeBalanceDBHelper mDBHelper;
    private Context mContext;


    public static final int WISH_STATUS_NEW = 0;
    public static final int WISH_STATUS_IN_REVIEW = 1;
    public static final int WISH_STATUS_REJECTED = 2;
    public static final int WISH_STATUS_APPROVED = 3;
    public static final int WISH_STATUS_COMPLETE = 999;


    public static final int WISH_SITUATION_STATUS_DRAFT = 0;
    public static final int WISH_SITUATION_STATUS_SUBMITTED = 1;
    public static final int WISH_SITUATION_STATUS_REJECTED = 1;


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
                new String[]{String.valueOf(WISH_STATUS_COMPLETE)},
                null,
                null,
                LifeBalanceContract.WishesEntry.COLUMN_START + " DESC",
                null
        );
        return wishes;
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
                new String[]{String.valueOf(WISH_STATUS_COMPLETE)});
        return result;
    }
    public static long InsertOrUpdateWish(SQLiteDatabase db,
                                          String idEntry,
                                          String types,
                                          long start,
                                          long planend,
                                          long factend,
                                          int status,
                                          String statusHint,
                                          String description,
                                          String situation,
                                          int situation_status) {
        long result = 0;
        ContentValues values = new ContentValues();
        values.put(LifeBalanceContract.WishesEntry.COLUMN_TYPE, types);
        values.put(LifeBalanceContract.WishesEntry.COLUMN_START, start);
        values.put(LifeBalanceContract.WishesEntry.COLUMN_PLAN_END, planend);
        values.put(LifeBalanceContract.WishesEntry.COLUMN_FACT_END, factend);
        values.put(LifeBalanceContract.WishesEntry.COLUMN_STATUS, status);
        values.put(LifeBalanceContract.WishesEntry.COLUMN_STATUS_HINT, statusHint);
        values.put(LifeBalanceContract.WishesEntry.COLUMN_DESCRIPTION, description);
        values.put(LifeBalanceContract.WishesEntry.COLUMN_SITUATION, situation);
        if (idEntry == null) {
            result = db.insert(LifeBalanceContract.WishesEntry.TABLE_NAME, null, values);
        } else {
            result = db.update(LifeBalanceContract.WishesEntry.TABLE_NAME, values, LifeBalanceContract.WishesEntry._ID + " = ? ", new String[]{idEntry});
            //if (result > 0) result = Long.getLong(idEntry);
        }
        return result;
    }

    public long InsertOrUpdateWish(String idEntry, String types, long start, long planend, long factend, int status, String statusHint, String description, String situation, int situation_status) {
        return this.InsertOrUpdateWish(mDBHelper.getWritableDatabase(), idEntry, types, start, planend, factend, status, statusHint, description, situation, situation_status);
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
