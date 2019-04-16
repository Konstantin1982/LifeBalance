package ru.apps4yourlife.life.lifebalance.Utilities;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBHelper;

public class SyncTask extends AsyncTask<Void,Void,Void> {

    private static final String SYNC_TASK_ACTIVITY = "SYNC_TASK_ACTIVITY";
    private static final String USER_ID = "USER_ID";
    private static final String KEY = "KEY";
    //private static final String URL_ADDRESS = "http://localhost/request.php";
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
            Log.e("TASK"," Started");
            boolean isActive = true;
            while (isActive) {
                // 1 - RECEIVE DATA FROM SERVER // once in minute
                // 2 - GET LIST for SEND
                JSONObject data = PrepareDataToSend();
                // 3 - SEND
                sendDataToServer(data);

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

    public JSONObject PrepareDataToSend(){
        JSONObject data = new JSONObject();
        try {
            // UserID + KEY
            String userId = LifeBalanceDBDataManager.GetSettingValueByName(dbHelper.getReadableDatabase(),USER_ID);
            data.put(USER_ID, userId);
            data.put(KEY,GenerateKey(userId));

            // wishes
            Cursor wishesToSend = LifeBalanceDBDataManager.GetWishesToServer(dbHelper.getReadableDatabase());
            int count = wishesToSend.getCount();
            Log.e("JSON_PREPARE", " Count of Wishes to Server: " + String.valueOf(count));
            if (count > 0) {
                JSONArray wishesArray = new JSONArray();
                wishesToSend.moveToFirst();
                for (int i = 0; i < wishesToSend.getCount(); i++) {
                    JSONObject wish = new JSONObject();
                    wishesToSend.moveToPosition(i);
                    wish.put("ID", wishesToSend.getString(wishesToSend.getColumnIndex(LifeBalanceContract.WishesEntry._ID)));
                    wish.put("START", wishesToSend.getString(wishesToSend.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_START)));
                    wish.put("END1", wishesToSend.getString(wishesToSend.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_PLAN_END)));
                    wish.put("END2", wishesToSend.getString(wishesToSend.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_FACT_END)));
                    wish.put("STATUS", wishesToSend.getString(wishesToSend.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_STATUS)));
                    wish.put("DESCRIPTION", wishesToSend.getString(wishesToSend.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_DESCRIPTION)));
                    wish.put("SITUATION", wishesToSend.getString(wishesToSend.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_SITUATION)));
                    wish.put("TYPE", wishesToSend.getString(wishesToSend.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_TYPE)));
                    wishesArray.put(wish);
                }
                data.put("wishes", wishesArray);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return data;
    }

    public void CommitWishesAnswer(String idList) {

    }

    public boolean sendDataToServer(JSONObject data) {
        try {
            URL url = new URL(URL_ADDRESS);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            byte[] bData = data.toString().getBytes(StandardCharsets.UTF_8);
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.write(bData);
            os.flush();
            os.close();
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            String responseData = convertStreamToString(inputStream);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                JSONObject responseJSON = new JSONObject(responseData);
                try {
                    String updatedWishes = responseJSON.getString("wishes");
                    Log.e("JSON.COMMIT", updatedWishes);
                    if (!updatedWishes.isEmpty()) {
                        LifeBalanceDBDataManager.CommitWishesFromServer (dbHelper.getWritableDatabase(),updatedWishes);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            Log.e("JSON. RESPONSE_CODE", String.valueOf(conn.getResponseCode()));
            Log.e("JSON. RESPONSE " , responseData);
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("JSON. ERROR", String.valueOf(e.getStackTrace().toString()));
            e.printStackTrace();
        }
        return false;
    }

    //TODO: generate key
    private String GenerateKey(String userId) {
        String key = "OK";
        return key;

    }

    private String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}

