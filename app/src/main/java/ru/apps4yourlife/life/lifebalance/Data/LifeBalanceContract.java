package ru.apps4yourlife.life.lifebalance.Data;
import android.provider.BaseColumns;



public class LifeBalanceContract {

    // Settings - идентификационная запись участника
    public static final class SettingsEntry implements BaseColumns {
        public static final String TABLE_NAME = "settings";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_VALUE = "value";
    }




    // Events - события, которые происходят в приложении.
    public static final class EventsEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_DESCRIPTION = "description";
    }

    // Сообщения - общение с автором
    public static final class MessagesEntry implements BaseColumns {
        public static final String TABLE_NAME = "messages";
        public static final String COLUMN_FROM = "sender";
        public static final String COLUMN_TO = "receiver";
        public static final String COLUMN_SUBJECT = "subject";
        public static final String COLUMN_BODY = "body";
        public static final String COLUMN_IS_NEW = "isnew";
        public static final String COLUMN_SENT_DATE = "date";
    }

    // желания
    // страхи

    // шаги
    public static final class StepsEntry implements BaseColumns {
        public static final String TABLE_NAME = "steps";
        public static final String COLUMN_WISH_ID = "wish_id"; //
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ORDER = "sort_order"; // 0,1,2,3,4
        //
    }
    // фотки

    // покупки
    public static final class PurchasesEntry implements BaseColumns {
        public static final String TABLE_NAME = "purchases";
        public static final String COLUMN_CODE = "code";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_START_STATUS = "start_status"; // дата начала подписки
        public static final String COLUMN_END_STATUS = "end_status";   // дата окончания подписки
    }

    // желания
    public static final class WishesEntry implements BaseColumns {
        public static final String TABLE_NAME = "wishes";
        public static final String COLUMN_TYPE = "types"; // личное, семья, работа и т.п.
        public static final String COLUMN_START = "start"; // старт желания
        public static final String COLUMN_PLAN_END = "planend"; // желаемый конец желания
        public static final String COLUMN_FACT_END = "factend"; // реальный конец  желания
        public static final String COLUMN_STATUS = "status"; // создано, отправлено на проверку, проверено, нужно доработать, идет работа со страхами, идет реализация, готово"
        public static final String COLUMN_STATUS_HINT = "statushint"; // подсказка, что дальше или что не так
        public static final String COLUMN_DESCRIPTION = "description"; // собственно, описание
        public static final String COLUMN_SITUATION = "situation"; // ситуация реализации
    }

    public static final class FearsEntry implements BaseColumns {
        public static final String TABLE_NAME = "fears";
        public static final String COLUMN_WISH_ID = "wish_id"; //
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_STATUS = "status"; // 0,1,2,3,4
        //
    }


    // желания
    public static final class WishesTypesEntry implements BaseColumns {
        public static final String TABLE_NAME = "wishestypes";
        public static final String COLUMN_DESCRIPTION = "description"; // собственно, описание
    }


}
