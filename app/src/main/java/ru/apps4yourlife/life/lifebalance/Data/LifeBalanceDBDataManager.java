package ru.apps4yourlife.life.lifebalance.Data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

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

    public static long InsertOrUpdateSettings(SQLiteDatabase db, String name, String value) {
        long result = 0;
        ContentValues values = new ContentValues();
        values.put(LifeBalanceContract.SettingsEntry.COLUMN_VALUE, value);
        String currentValue = LifeBalanceDBDataManager.GetSettingValueByName(db, name);
        if (currentValue.isEmpty()) {
            values.put(LifeBalanceContract.SettingsEntry.COLUMN_NAME, name);
            result = db.insert(LifeBalanceContract.SettingsEntry.TABLE_NAME, null, values);
        } else {
            result = db.update(LifeBalanceContract.SettingsEntry.TABLE_NAME, values, LifeBalanceContract.SettingsEntry.COLUMN_NAME + " LIKE ? " , new String[]{name});
        }
        return result;
    }

    public long InsertOrUpdateMessage(String idEntry, String from, String to, String subject, String body, int isnew, long messageDate) {
        return this.InsertOrUpdateMessage(mDBHelper.getWritableDatabase(), idEntry, from, to, subject, body, isnew, messageDate);
    }

    public static long InsertQueue(SQLiteDatabase db, String idEntry, String type, String status) {
        long result = 0;
        ContentValues values = new ContentValues();
        values.put(LifeBalanceContract.ServerQueueEntry.COLUMN_ENTITY_ID, idEntry);
        values.put(LifeBalanceContract.ServerQueueEntry.COLUMN_TYPE, type);
        values.put(LifeBalanceContract.ServerQueueEntry.COLUMN_STATUS, status);
        result = db.insert(LifeBalanceContract.ServerQueueEntry.TABLE_NAME, null, values);
        return result;
    }

    public long InsertQueue(String idEntry, String type, String status) {
        return InsertQueue(mDBHelper.getWritableDatabase(), idEntry, type, status);
    }

    public static long SetQueueRecordStatus(SQLiteDatabase db, String queueId, String status) {
        long result = 0;
        ContentValues values = new ContentValues();
        values.put(LifeBalanceContract.ServerQueueEntry.COLUMN_STATUS, status);
        result = db.update(LifeBalanceContract.ServerQueueEntry.TABLE_NAME, values, LifeBalanceContract.ServerQueueEntry._ID + " = ? ", new String[]{queueId});
        return result;
    }

    public long SetQueueRecordStatus(String queueId, String status) {
        return SetQueueRecordStatus(mDBHelper.getWritableDatabase(), queueId, status);
    }

    public Cursor GetWishesList(int mode) {
        Cursor wishes;
        String selection = null;
        String[] args = null;
        if (mode == 0) {
            selection = LifeBalanceContract.WishesEntry.COLUMN_STATUS + " < ? ";
            args = new String[]{String.valueOf(GeneralHelper.WishStatusesClass.WISH_STATUS_COMPLETE)};
        }
        wishes = mDBHelper.getReadableDatabase().query(
                LifeBalanceContract.WishesEntry.TABLE_NAME,
                null,
                selection,
                args,
                null,
                null,
                LifeBalanceContract.WishesEntry.COLUMN_START + " DESC",
                null
        );
        return wishes;
    }

    public Cursor GetWishesToServer() {
        return GetWishesToServer(mDBHelper.getReadableDatabase());
    }

    public static Cursor GetWishesToServer(SQLiteDatabase db) {
        String sql = "select * from  " + LifeBalanceContract.WishesEntry.TABLE_NAME+ " WHERE " + LifeBalanceContract.WishesEntry._ID +
                " IN (Select " + LifeBalanceContract.ServerQueueEntry.COLUMN_ENTITY_ID + " FROM " + LifeBalanceContract.ServerQueueEntry.TABLE_NAME +
                " WHERE " + LifeBalanceContract.ServerQueueEntry.COLUMN_TYPE + " = 0 AND " +
                LifeBalanceContract.ServerQueueEntry.COLUMN_STATUS + " = 0)";
        Cursor wishes = db.rawQuery(
                sql,
                null);
        wishes.moveToFirst();
        return wishes;
    }

    public static void CommitWishesFromServer(SQLiteDatabase writableDb, String listId) {


        long result = 0;
        ContentValues values = new ContentValues();
        values.put(LifeBalanceContract.ServerQueueEntry.COLUMN_STATUS, 1);
        String whereClause = LifeBalanceContract.ServerQueueEntry.COLUMN_TYPE + " = 0 AND " + LifeBalanceContract.ServerQueueEntry.COLUMN_ENTITY_ID + " IN (" + listId + ")";
        result = writableDb.update(LifeBalanceContract.ServerQueueEntry.TABLE_NAME, values, whereClause, null);
        return;
    }

    public void CommitWishesFromServer(String listId) {
        CommitWishesFromServer(mDBHelper.getWritableDatabase(), listId);
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
        String result = "<B>Внимание к деталям</B> <BR> <UL><LI>Обрати внимание на ГГГ</LI></UL>";
        // mode == 0 - review
        // mode == 1 - sit review
        return result;
    }

    public Cursor GetWishesTypesWithChecked(ArrayList<Integer> selectedItems) {
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
            if (result > 0) result = Long.valueOf(idEntry);
        }
        return result;
    }

    public long MoveWishToNextStatus(String idEntry,
                                          int status)
    {
        long result = 0;
        ContentValues values = new ContentValues();
        values.put(LifeBalanceContract.WishesEntry.COLUMN_STATUS, status);
        result = mDBHelper.getWritableDatabase().update(LifeBalanceContract.WishesEntry.TABLE_NAME, values, LifeBalanceContract.WishesEntry._ID + " = ? ", new String[]{idEntry});
        if (result > 0 ) result = Long.valueOf(idEntry);
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

    public static String GetSettingValueByName(SQLiteDatabase  db, String name) {
        String result = "";
        Cursor settings = db.query(
                LifeBalanceContract.SettingsEntry.TABLE_NAME,
                null,
                LifeBalanceContract.SettingsEntry.COLUMN_NAME + " LIKE ?",
                new String[]{name},
                null,
                null,
                null
        );
        if (settings.getCount() > 0) {
            settings.moveToFirst();
            result = settings.getString( settings.getColumnIndex(LifeBalanceContract.SettingsEntry.COLUMN_VALUE));
        }
        return result;
    }


    // get steps by wish id
    public Cursor GetStepsByWishId(String wishId) {
        Cursor steps = mDBHelper.getReadableDatabase().query(
                LifeBalanceContract.StepsEntry.TABLE_NAME,
                null,
                LifeBalanceContract.StepsEntry.COLUMN_WISH_ID + " = ? ",
                new String[]{wishId},
                null,
                null,
                LifeBalanceContract.StepsEntry.COLUMN_ORDER,
                null
        );
        if (steps.getCount() > 0) {
            steps.moveToFirst();
        }
        return steps;
    }

    public static Cursor GetStepById(SQLiteDatabase db, long id) {
        Cursor steps = db.query(
                LifeBalanceContract.StepsEntry.TABLE_NAME,
                null,
                LifeBalanceContract.StepsEntry._ID + " = ? ",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null
        );
        if (steps.getCount() > 0) {
            steps.moveToFirst();
        }
        return steps;
    }
    public Cursor GetStepById(long id) {
        return LifeBalanceDBDataManager.GetStepById(mDBHelper.getReadableDatabase(), id);
    }

    public long InsertOrUpdateStep(long idEntry, int wishId, String description) {
        return LifeBalanceDBDataManager.InsertOrUpdateStep(mDBHelper.getWritableDatabase(),idEntry, wishId, description);
    }

    public static long InsertOrUpdateStep(SQLiteDatabase db, long idEntry, int wishId, String description) {
        long result = 0;
        ContentValues values = new ContentValues();
        values.put(LifeBalanceContract.StepsEntry.COLUMN_WISH_ID, wishId);
        values.put(LifeBalanceContract.StepsEntry.COLUMN_DESCRIPTION, description);
        if (idEntry > 0) {
            result = db.update(LifeBalanceContract.StepsEntry.TABLE_NAME, values, LifeBalanceContract.StepsEntry._ID + " = ? ", new String[]{String.valueOf(idEntry)});
            if (result > 0) result = idEntry;
        } else {
            int order = GetMaxOrderNumber(db, wishId) + 1;
            if (order <= 0) order = 1;
            values.put(LifeBalanceContract.StepsEntry.COLUMN_ORDER, order);
            result = db.insert(LifeBalanceContract.StepsEntry.TABLE_NAME, null, values);
        }
        return result;
    }

    public static int GetMaxOrderNumber(SQLiteDatabase db, int wishId) {
        int result = -1;
        String sign, aggr;
        Cursor value = db.rawQuery("SELECT MAX(" + LifeBalanceContract.StepsEntry.COLUMN_ORDER +") FROM " + LifeBalanceContract.StepsEntry.TABLE_NAME + " WHERE " +
                LifeBalanceContract.StepsEntry.COLUMN_WISH_ID + " = " + wishId, null) ;
        if (value.getCount() > 0) {
            value.moveToFirst();
            result = value.getInt(0);
        }
        return result;
    }

    public static int GetMinOrderNumber(SQLiteDatabase db, int wishId) {
        return 0;
    }

    public static int GetNextOrderNumber(SQLiteDatabase db, int wishId, int currentOrder) {
        int result;
        Cursor value = db.rawQuery(
                "SELECT MIN(" + LifeBalanceContract.StepsEntry.COLUMN_ORDER +") FROM " + LifeBalanceContract.StepsEntry.TABLE_NAME + " WHERE " +
                LifeBalanceContract.StepsEntry.COLUMN_WISH_ID + " = ? AND " + LifeBalanceContract.StepsEntry.COLUMN_ORDER + " > ?" , new String[]{String.valueOf(wishId), String.valueOf(currentOrder)}) ;
        if (value.getCount() > 0) {
            value.moveToFirst();
            result = value.getInt(0);
        } else {
            result = currentOrder; //
        }
        return result;
    }
    public static int GetPrevOrderNumber(SQLiteDatabase db, int wishId, int currentOrder) {
        int result;
        Cursor value = db.rawQuery(
                "SELECT MAX(" + LifeBalanceContract.StepsEntry.COLUMN_ORDER +") FROM " + LifeBalanceContract.StepsEntry.TABLE_NAME + " WHERE " +
                        LifeBalanceContract.StepsEntry.COLUMN_WISH_ID + " = ? AND " + LifeBalanceContract.StepsEntry.COLUMN_ORDER + " < ?" , new String[]{String.valueOf(wishId), String.valueOf(currentOrder)}) ;
        if (value.getCount() > 0) {
            value.moveToFirst();
            result = value.getInt(0);
        } else {
            result = currentOrder; //
        }
        return result;
    }





    public static int GetClosestOrderNumber(SQLiteDatabase db,  int currentOrder, int wishId, int direction) {
        if (direction == 1) return GetNextOrderNumber(db,wishId,currentOrder);
        return GetPrevOrderNumber(db,wishId,currentOrder);
    }

    public static boolean ReorderStep(SQLiteDatabase db, long stepId, int direction) {
        boolean result = false;
        Cursor currentStep = GetStepById(db,stepId);
        if (currentStep.getCount() > 0) {
            currentStep.moveToFirst();
            int wishId = currentStep.getInt(currentStep.getColumnIndex(LifeBalanceContract.StepsEntry.COLUMN_WISH_ID));
            int currentOrder = currentStep.getInt(currentStep.getColumnIndex(LifeBalanceContract.StepsEntry.COLUMN_ORDER));
            int newOrder = GetClosestOrderNumber(db,currentOrder,wishId, direction);
            if (currentOrder <= 1 && direction == -1) return false;
            if (newOrder == currentOrder || newOrder <= 0) return false;
            if (newOrder >= 0 && newOrder != currentOrder) {
                ContentValues values = new ContentValues();

                values.put(LifeBalanceContract.StepsEntry.COLUMN_ORDER, currentOrder);
                long res1 = db.update(LifeBalanceContract.StepsEntry.TABLE_NAME, values,
                        LifeBalanceContract.StepsEntry.COLUMN_ORDER + " = ? AND " + LifeBalanceContract.StepsEntry.COLUMN_WISH_ID + " = ? ",
                        new String[]{String.valueOf(newOrder), String.valueOf(wishId)});

                values = new ContentValues();
                values.put(LifeBalanceContract.StepsEntry.COLUMN_ORDER, newOrder);
                long res2 = db.update(LifeBalanceContract.StepsEntry.TABLE_NAME, values, LifeBalanceContract.StepsEntry._ID + " = ? ", new String[]{String.valueOf(stepId)});
            }
        }

        return result;
    }

    public boolean ReorderStep(long stepId, int direction) {
        return ReorderStep(mDBHelper.getWritableDatabase(), stepId, direction);
    }

}
