package ru.apps4yourlife.life.lifebalance.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBHelper;
import ru.apps4yourlife.life.lifebalance.R;

/**
 * Created by 123 on 27.03.2018.
 */

public class GeneralHelper {

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


    public static boolean isUserSubscribed() {
        return false;
    }


    public static void PushWishToServer(Context context, long wishId) {
        Toast.makeText(context, "Отправлено на рассмотрение", Toast.LENGTH_SHORT).show();
    }

    public static void StartSubscriptionProcess(Context context) {
        Toast.makeText(context, "Subscription process is started....", Toast.LENGTH_SHORT).show();
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
                String fullMessage = extraMessage + "<BR><HR>" + str;
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


    public static Dialog ShowRecommendToSubscribe(final Context context, final SubscribeDialogInterface listener ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.title_recommend_subscribe)
                .setMessage(R.string.text_recommend_subscribe)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(context,"УРА! Спасибо за подписку!",Toast.LENGTH_SHORT).show();
                        GeneralHelper.StartSubscriptionProcess(context);
                        listener.OnAgreedToSubscribe(context);
                    }
                })
                .setNegativeButton(R.string.myself, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context,"Успехов! При необходимости всегда можно подключить ментора",Toast.LENGTH_LONG).show();
                        listener.OnRejectToSubscribe(context);
                    }
                });
        return builder.create();
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

    public class SyncTask extends AsyncTask<Void,Void,Void> {

        private static final String SYNC_TASK_ACTIVITY = "SyncTaskActivity";
        private static final String URL_ADDRESS = "http://apps4yourlife.ru/lifebalance/request.php";

        private int res = 0;
        private LifeBalanceDBHelper dbHelper;
        private Context mContext;
        private int currentActivity;

        public SyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dbHelper = new LifeBalanceDBHelper(mContext);
            currentActivity = mContext.hashCode();
            LifeBalanceDBDataManager.InsertOrUpdateSettings(dbHelper.getWritableDatabase(), SYNC_TASK_ACTIVITY, String.valueOf(currentActivity));
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.d("TASK"," Started");
                boolean isActive = true;
                while (isActive) {
                    // 1 - RECEIVE DATA FROM SERVER // once in minute
                    // 2 - GET LIST for SEND
                    // 3 - SEND
                    TimeUnit.SECONDS.sleep(60);
                    if (!LifeBalanceDBDataManager.GetSettingValueByName(dbHelper.getReadableDatabase(), SYNC_TASK_ACTIVITY).equalsIgnoreCase(String.valueOf(currentActivity))) {
                        isActive = false;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... voids){
            super.onProgressUpdate(voids);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }


        public boolean sendDataToServer() {
            try {
                URL url = new URL(URL_ADDRESS);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("timestamp", 1488873360);
                jsonParam.put("latitude", 0D);
                jsonParam.put("longitude", 0D);

                Log.i("JSON", jsonParam.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG" , conn.getResponseMessage());

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }


}
