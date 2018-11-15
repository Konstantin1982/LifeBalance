package ru.apps4yourlife.life.lifebalance.Data;
import android.provider.BaseColumns;



public class LifeBalanceContract {

    // Events - события, которые происходят в приложении.
    public static final class EventsEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_DESCRIPTION = "description";
    }

    // Сообщения - общение с автором
    public static final class MessagesEntry implements BaseColumns {
        public static final String TABLE_NAME = "messages";
        public static final String COLUMN_FROM = "from";
        public static final String COLUMN_TO = "to";
        public static final String COLUMN_SUBJECT = "subject";
        public static final String COLUMN_BODY = "body";
        public static final String COLUMN_IS_NEW = "isnew";
        public static final String COLUMN_SENT_DATE = "date";
    }

    // желания
    // страхи

    // шаги
    // фотки

    // покупки
    public static final class PurchasesEntry implements BaseColumns {
        public static final String TABLE_NAME = "purchases";
        public static final String COLUMN_CODE = "code";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_START_STATUS = "start_status";
        public static final String COLUMN_END_STATUS = "end_status";
    }


}
