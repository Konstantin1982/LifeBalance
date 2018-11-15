package ru.apps4yourlife.life.lifebalance.Data;
import android.content.Context;
import android.database.Cursor;


public class LifeBalanceDBDataManager {
    private LifeBalanceDBHelper mDBHelper;
    private Context mContext;


    public LifeBalanceDBDataManager(Context context) {
        mDBHelper = new LifeBalanceDBHelper(context);
        mContext = context;
        //mDBHelper.getWritableDatabase(); // just to fix crash
    }

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

    public void deleteDatabase() {
        mContext.deleteDatabase(mDBHelper.getDatabaseName());
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
