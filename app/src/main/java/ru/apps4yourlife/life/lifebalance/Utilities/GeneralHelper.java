package ru.apps4yourlife.life.lifebalance.Utilities;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBHelper;
import ru.apps4yourlife.life.lifebalance.R;

/**
 * Created by 123 on 27.03.2018.
 */

public class GeneralHelper {

    /* SETTINGS
        CLIENT_NAME

     */

    public static final String USER_ID_SETTING_NAME = "USERID";   // уникальный айди клиента
    public static final String USER_NAME_SETTING_NAME = "CLIENTNAME"; // имя клиента
    public static final String USER_STATE_SETTING_NAME = "USER_STATUS";  // 1 2 - покупка есть.
    public static final String USER_TEST_STATE_SETTING_NAME = "USER_TEST_STATUS";  // 1 2 - покупка есть.
    public static final String USER_GOT_TEST_WISH_NAME = "USER_GOT_TEST_WISH";  // 1 2 - покупка есть.

    public static String GetCurrentDateString() {
// set the format to sql date time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static ArrayList<Integer> extractTypesFromWish(String types) {
        String[] typesArray = types.split(",");
        ArrayList<Integer> resultArray = new ArrayList<>();
        for (String type : typesArray) {
            if (!type.equalsIgnoreCase(""))
            resultArray.add(Integer.valueOf(type));
        }
        return resultArray;
    }

    public static String ConvertTypesToString(ArrayList<Integer> types) {
        String result = "";
        boolean isFirst = true;
        for (Integer type : types) {
            if (isFirst) {
                isFirst = false;
            } else {
                result += ",";
            }
            result += String.valueOf(type);
        }
        return result;
    }

    public static Date GetCurrentDate() {
        GregorianCalendar tmpDate = new GregorianCalendar();
        GregorianCalendar normalDate = new GregorianCalendar(
                tmpDate.get(Calendar.YEAR),
                tmpDate.get(Calendar.MONTH),
                tmpDate.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0);
        return normalDate.getTime();
    }

    public static class WishStatusesClass {
        public static final int WISH_STATUS_NEW = 0;
        public static final int WISH_STATUS_IN_REVIEW = 1;
        public static final int WISH_STATUS_REJECTED = 2;
        public static final int WISH_STATUS_SITUATION = 3;
        public static final int WISH_STATUS_SITUATION_REVIEW = 4;
        public static final int WISH_STATUS_SITUATION_REJECTED = 5;
        public static final int WISH_STATUS_FEARS = 6;
        public static final int WISH_STATUS_STEPS = 7;
        public static final int WISH_STATUS_WAITING = 8;
        public static final int WISH_STATUS_COMPLETE = 999;
    }

    public interface SubscribeDialogInterface {
        void OnAgreedToSubscribe(Context context);
        void OnAgreedTestSubscribe(Context context);
        void OnRejectToSubscribe(Context context);
    }


    public static AbstractMap.SimpleEntry<String, String> GetNextStepDescriptionForList(Integer current_status) {

        String nextStepNumber = "-1";
        String nextStepDescription = "загружается ... ";

        switch (current_status) {
            case WishStatusesClass.WISH_STATUS_NEW:
                nextStepNumber = "1";
                nextStepDescription = "Формулируем желание";
                break;
            case WishStatusesClass.WISH_STATUS_IN_REVIEW:
                nextStepNumber = "1";
                nextStepDescription = "Ожидаем проверки";
                break;
            case WishStatusesClass.WISH_STATUS_REJECTED :
                nextStepNumber = "1";
                nextStepDescription = "Исправляем ошибки";
                break;
            case WishStatusesClass.WISH_STATUS_SITUATION:
                nextStepNumber = "2";
                nextStepDescription = "Представляем и мечтаем";
                break;
            case WishStatusesClass.WISH_STATUS_SITUATION_REVIEW:
                nextStepNumber = "2";
                nextStepDescription = "Ожидаем проверки";
                break;
            case WishStatusesClass.WISH_STATUS_SITUATION_REJECTED :
                nextStepNumber = "2";
                nextStepDescription = "Исправляем ошибки";
                break;
            case WishStatusesClass.WISH_STATUS_FEARS:
                nextStepNumber = "3";
                nextStepDescription = "Работа со страхами";
                break;
            case WishStatusesClass.WISH_STATUS_STEPS:
                nextStepNumber = "4";
                nextStepDescription = "Готовим шаги";
                break;
            case WishStatusesClass.WISH_STATUS_WAITING:
                nextStepNumber = "5";
                nextStepDescription = "Исполняется ...";
                break;
            case WishStatusesClass.WISH_STATUS_COMPLETE:
                nextStepNumber = "6";
                nextStepDescription = "Исполняется ...";
                break;
        }
        AbstractMap.SimpleEntry<String, String> res = new AbstractMap.SimpleEntry<String, String>(nextStepNumber, nextStepDescription);
        return res;
    }


    public static boolean isUserSubscribed(Context context) {
        LifeBalanceDBHelper dbHelper = new LifeBalanceDBHelper(context);
        String currentState = LifeBalanceDBDataManager.GetSettingValueByName(dbHelper.getReadableDatabase(),GeneralHelper.USER_STATE_SETTING_NAME);
        if (currentState.equalsIgnoreCase("1") || currentState.equalsIgnoreCase("2")) {
            return true;
        }
        return false;
    }

    public static int isUserSubscribeTestWish(Context context, String wishId) {
        LifeBalanceDBDataManager dbDataManager = new LifeBalanceDBDataManager(context);
        int status =dbDataManager.UserTestWishStatus(wishId);
        //Log.e("USER STATUS", "IS = " + status);
        return status;
    }


    public static void PushWishToServer(Context context, long wishId) {
        LifeBalanceDBHelper dbHelper = new LifeBalanceDBHelper(context);
        LifeBalanceDBDataManager.InsertQueue(dbHelper.getWritableDatabase(), String.valueOf(wishId),"0", "0");
        Toast.makeText(context, "Отправлено на рассмотрение", Toast.LENGTH_SHORT).show();
    }

    public static void ShowHelpInWishActivity(String stepName, String extraMessage, final Context context) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.help_view, null);
            WebView messageWebView = (WebView) dialogView.findViewById(R.id.helpShowWebView);
            String fileName = "file:///android_asset/" +  stepName;
            if (extraMessage.isEmpty()) {
                messageWebView.loadUrl(fileName);
            } else {
                InputStream is = context.getAssets().open(stepName);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String str = new String(buffer);
                String fullMessage = "<h2 align='center'>Комментарий от тренера</h2>" + extraMessage + "<BR><BR><HR>" + str;
                messageWebView.loadData(fullMessage, "text/html", "ru_RU");
            }

            builder.setView(dialogView);
            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return;

    }



    public static int GetImageResourceByType(int type) {
        int resourceId = 0;
        switch (type) {
            case 0:
                // love
                resourceId = R.drawable.ic_love;
                break;
            case 1:
                // friends
                resourceId = R.drawable.ic_balloons;
                break;
            case 2:
                //work
                resourceId = R.drawable.ic_hiring;
                break;
            case 3:
                //hobby
                resourceId = R.drawable.ic_open_box;
                break;
            case 4:
                // money
                resourceId = R.drawable.ic_debit_card;
                break;
            case 5:
                //health
                resourceId = R.drawable.ic_hospital;
                break;
        }
        return resourceId;
    }
}
