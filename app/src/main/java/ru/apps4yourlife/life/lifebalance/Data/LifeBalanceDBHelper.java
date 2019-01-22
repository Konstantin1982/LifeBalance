package ru.apps4yourlife.life.lifebalance.Data;

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
        final String SQL_CREATE_MESSAGE_TABLE =
                "CREATE TABLE " +
                        LifeBalanceContract.MessagesEntry.TABLE_NAME + "(" +
                        LifeBalanceContract.MessagesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        LifeBalanceContract.MessagesEntry.COLUMN_SENT_DATE + " INTEGER, " +
                        LifeBalanceContract.MessagesEntry.COLUMN_FROM + " VARCHAR(255), " +
                        LifeBalanceContract.MessagesEntry.COLUMN_TO + " VARCHAR(255), " +
                        LifeBalanceContract.MessagesEntry.COLUMN_SUBJECT + " VARCHAR(255), " +
                        LifeBalanceContract.MessagesEntry.COLUMN_BODY + " BLOB, " +
                        LifeBalanceContract.MessagesEntry.COLUMN_IS_NEW + " INTEGER DEFAULT 1 " +
                        ")";

        final String SQL_CREATE_WISHES_TABLE =
        "CREATE TABLE " +
                        LifeBalanceContract.WishesEntry.TABLE_NAME + "(" +
                        LifeBalanceContract.WishesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        LifeBalanceContract.WishesEntry.COLUMN_TYPE + " VARCHAR(255), " +
                        LifeBalanceContract.WishesEntry.COLUMN_START + " INTEGER, " +
                        LifeBalanceContract.WishesEntry.COLUMN_PLAN_END + " INTEGER, " +
                        LifeBalanceContract.WishesEntry.COLUMN_FACT_END + " INTEGER, " +
                        LifeBalanceContract.WishesEntry.COLUMN_STATUS + " INTEGER, " +
                        LifeBalanceContract.WishesEntry.COLUMN_STATUS_HINT + " VARCHAR(255), " +
                        LifeBalanceContract.WishesEntry.COLUMN_DESCRIPTION + " VARCHAR(1020), " +
                        LifeBalanceContract.WishesEntry.COLUMN_SITUATION + " VARCHAR(1020), " +
                        LifeBalanceContract.WishesEntry.COLUMN_SITUATION_STATUS + " INTEGER " +
                        ")";

        final String SQL_CREATE_WISHES_TYPES_TABLE =
        "CREATE TABLE " +
                        LifeBalanceContract.WishesTypesEntry.TABLE_NAME + "(" +
                        LifeBalanceContract.WishesTypesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        LifeBalanceContract.WishesTypesEntry.COLUMN_DESCRIPTION + " VARCHAR(1020) " +
                        ")";

        sqLiteDatabase.execSQL(SQL_CREATE_EVENTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MESSAGE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WISHES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WISHES_TYPES_TABLE);
        Log.d("DB", "TABLES WERE CREATED");
    }

    public void InsertInitialEvent(SQLiteDatabase db) {
        String[] happenedEvents = mContext.getResources().getStringArray(R.array.events);
            for (String eventDescription : happenedEvents) {
                LifeBalanceDBDataManager.InsertOrUpdateEvent(db,
                        null,
                        GeneralHelper.GetCurrentDate().getTime(),
                        eventDescription);
            }
    }

    public void InsertInitialMessages(SQLiteDatabase db) {
        String[] messages = mContext.getResources().getStringArray(R.array.messages);
        for (String message : messages) {
            String[] parcedMessage = message.split("\\|");
            LifeBalanceDBDataManager.InsertOrUpdateMessage(db,null,parcedMessage[0],parcedMessage[1],parcedMessage[2],parcedMessage[3],1, GeneralHelper.GetCurrentDate().getTime());
        }
    }

    public void InsertInitialWishesTypes(SQLiteDatabase db) {
        String[] types = mContext.getResources().getStringArray(R.array.wishes_types);
        for (String type : types) {
            LifeBalanceDBDataManager.InsertOrUpdateWishType(db,null,type);
        }
    }

    public void insertFakeWishes(SQLiteDatabase db) {
        LifeBalanceDBDataManager.InsertOrUpdateWish(
                db,
                null,
                "0,1,2",
                0,
                0,
                0,
                GeneralHelper.WishStatusesClass.WISH_STATUS_NEW,
                "Черновик",
                "Я хочу морковный сок из 20-ти морковных грядок. Сок холодный и свежий.",
                "Морозное утро, выхожу из-за стола, пью ледяной сок.",
                0
                );
        LifeBalanceDBDataManager.InsertOrUpdateWish(
                db,
                null,
                "1,2",
                0,
                0,
                0,
                GeneralHelper.WishStatusesClass.WISH_STATUS_UNDER_REVIEW,
                "на проверке",
                "Устроиться на работу мечты - стать дворников на багамских островах.",
                "Мету кокосовые листья, выбрасываю крабовые шкурки в океан.",
                0
                );
        LifeBalanceDBDataManager.InsertOrUpdateWish(
                db,
                null,
                "0,1",
                0,
                0,
                0,
                GeneralHelper.WishStatusesClass.WISH_STATUS_UNDER_REVIEW,
                "на проверке",
                "Устроиться на работу мечты - стать дворников на багамских островах.",
                "Мету кокосовые листья, выбрасываю крабовые шкурки в океан.",
                0
                );
        LifeBalanceDBDataManager.InsertOrUpdateWish(
                db,
                null,
                "0,2",
                0,
                0,
                0,
                GeneralHelper.WishStatusesClass.WISH_STATUS_UNDER_REVIEW,
                "на проверке",
                "Устроиться на работу мечты - стать дворников на багамских островах.",
                "Мету кокосовые листья, выбрасываю крабовые шкурки в океан.",
                0
                );
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        CreateTables(sqLiteDatabase);

        InsertInitialEvent(sqLiteDatabase);
        InsertInitialMessages(sqLiteDatabase);
        insertFakeWishes(sqLiteDatabase);
        InsertInitialWishesTypes(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
