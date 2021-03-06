package ru.apps4yourlife.life.lifebalance.Activities;

import android.os.AsyncTask;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Random;

import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBHelper;
import ru.apps4yourlife.life.lifebalance.R;
import ru.apps4yourlife.life.lifebalance.Utilities.BillingHelper;
import ru.apps4yourlife.life.lifebalance.Utilities.GeneralHelper;

//import static ru.apps4yourlife.life.lifebalance.Utilities.SyncTask.SYNC_TASK_ACTIVITY;

public class MentorBuyingSubmitActivity extends AppCompatActivity implements PurchasesUpdatedListener, BillingHelper.LastPurchaseListener  {

    private static final String URL_ADDRESS_TO_CHECK = "https://apps4yourlife.ru/lifebalance/state.php";
    private static final String URL_ADDRESS_TO_CALLBACK = "https://apps4yourlife.ru/lifebalance/callback.php";
    private BillingHelper mBillingHelper;
    private int places = -1;
    private double price = 8000;
    private int isCampaign = 0;
    private String cHeader = "";
    private String cText = "";
     //private String skuCodeMain = "android.test.purchased";
    private String skuCodeMain = "mainwish";
    private String skuCodeTest = "testwish";
    private String mMode = "";


    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        LifeBalanceDBDataManager dbDataManager = new LifeBalanceDBDataManager(this);
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null){

            for (Purchase purchase : purchases) {
                String skuCode = purchase.getSku();
                if (skuCode.equalsIgnoreCase(skuCodeMain)) {
                    dbDataManager.InsertOrUpdateSettings(GeneralHelper.USER_STATE_SETTING_NAME, "1");
                    Toast.makeText(this,"Покупка прошла успешно!!! Ваши желания обязательно сбудутся", Toast.LENGTH_LONG).show();
                    CallBackTask cb = new CallBackTask(0);
                    cb.execute();
                }
                if (skuCode.equalsIgnoreCase(skuCodeTest)) {
                    dbDataManager.InsertOrUpdateSettings(GeneralHelper.USER_TEST_STATE_SETTING_NAME, "1");
                    Toast.makeText(this,"Покупка прошла успешно!!! Ваши желания обязательно сбудутся", Toast.LENGTH_LONG).show();
                    CallBackTask cb = new CallBackTask(2);
                    cb.execute();
                }
            }
        }
        if (responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED) {
            if (purchases != null) {
                for (Purchase purchase : purchases) {
                    String skuCode = purchase.getSku();
                    if (skuCode.equalsIgnoreCase(skuCodeMain)) {
                        dbDataManager.InsertOrUpdateSettings(GeneralHelper.USER_STATE_SETTING_NAME, "2");
                        Toast.makeText(this, "Покупка прошла успешно!!! Ваши желания обязательно сбудутся", Toast.LENGTH_LONG).show();
                        CallBackTask cb = new CallBackTask(1);
                        cb.execute();
                    }
                    if (skuCode.equalsIgnoreCase(skuCodeTest)) {
                        dbDataManager.InsertOrUpdateSettings(GeneralHelper.USER_TEST_STATE_SETTING_NAME, "1");
                        Toast.makeText(this, "Покупка прошла успешно!!! Ваши желания обязательно сбудутся", Toast.LENGTH_LONG).show();
                        CallBackTask cb = new CallBackTask(3);
                        cb.execute();
                    }
                }
            }   else {
                if (mMode == "TEST") {
                    dbDataManager.InsertOrUpdateSettings(GeneralHelper.USER_TEST_STATE_SETTING_NAME, "1");
                    Toast.makeText(this, "Покупка прошла успешно!!! Ваши желания обязательно сбудутся", Toast.LENGTH_LONG).show();
                    CallBackTask cb = new CallBackTask(3);
                    cb.execute();

                } else {
                    dbDataManager.InsertOrUpdateSettings(GeneralHelper.USER_STATE_SETTING_NAME, "2");
                    Toast.makeText(this, "Покупка прошла успешно!!! Ваши желания обязательно сбудутся", Toast.LENGTH_LONG).show();
                    CallBackTask cb = new CallBackTask(1);
                    cb.execute();
                }
            }
        }
    }

    @Override
    public void setLastPurchase(String code) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_buying_submit);

        Random r = new Random();
        int i1 = r.nextInt(150000);
        EditText userNameEditText = findViewById(R.id.userNameEditText);
        userNameEditText.setText("Мечтатель_" + i1);

        String activityMode = getIntent().getStringExtra("MODE");
        Button helpButton = findViewById(R.id.helpSubmitButton);
        helpButton.setVisibility(View.GONE);
        if (activityMode != null) {
            if (activityMode.equalsIgnoreCase("TEST")) {
                mMode = "TEST";
                setTitle("Тестовое желание");
                helpButton.setVisibility(View.VISIBLE);
            }
        }
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

    }


    public void OnCheckStateClick(View view) {
        CheckStateTask task = new CheckStateTask(mMode);
        task.execute();
    }

    public void OnSubmitClick(View view) {

        /*
        // DONE: REMOVE IN PROD
        LifeBalanceDBDataManager dbDataManager = new LifeBalanceDBDataManager(this);
        dbDataManager.InsertOrUpdateSettings(GeneralHelper.USER_TEST_STATE_SETTING_NAME, "1");
        Toast.makeText(this,"Покупка прошла успешно!!! Ваши желания обязательно сбудутся", Toast.LENGTH_LONG).show();
        CallBackTask cb = new CallBackTask(2);
        cb.execute();
         */

        EditText userNameEditText = findViewById(R.id.userNameEditText);
            String userName = userNameEditText.getText().toString();
            LifeBalanceDBHelper dbHelper = new LifeBalanceDBHelper(this);
            LifeBalanceDBDataManager.InsertOrUpdateSettings(dbHelper.getWritableDatabase(),GeneralHelper.USER_NAME_SETTING_NAME,userName);
            if (mMode == "TEST") {
                mBillingHelper = new BillingHelper(this, this, this, skuCodeTest);
            } else {
                mBillingHelper = new BillingHelper(this, this, this, skuCodeMain);
            }
            mBillingHelper.StartOperationInStore();
    }

    public void UpdateStateView(int places, double price, int isAction, String header, String text) {
        Button startBuyButton = (Button) findViewById(R.id.startBuyButton);
        startBuyButton.setEnabled(places > 0);
        if (places < 0) {
            Toast.makeText(this,"Произошла ошибка. Попробуйте еще раз", Toast.LENGTH_SHORT).show();
        } else {
            TextView placesView = (TextView) findViewById(R.id.freePlacesTextView);
            if (places > 50) {
                placesView.setText("Свободных мест: достаточно");
            }
            if (places > 10 && places <= 50) {
                placesView.setText("Свободных мест: немного");
            }
            if (places <= 10 && places > 0) {
                placesView.setText("Свободных мест: крайне мало");
            }
            if (places == 0) {
                placesView.setText("Свободных мест: нет.");
            }

            TextView priceView = (TextView) findViewById(R.id.actualPrice);
            priceView.setText("Стоимость: " + String.valueOf(price) + " рублей.");

            TextView headerActionTextView = (TextView) findViewById(R.id.headerAction);
            TextView textActionTextView = (TextView) findViewById(R.id.textAction);

            if (isAction > 0) {
                headerActionTextView.setText(header);
                textActionTextView.setText(text);
            } else {
                headerActionTextView.setVisibility(View.INVISIBLE);
                textActionTextView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(1);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void HelpButtonClick(View view) {
        // DONE: SHOW HELP ABOUT TEST WISH
        GeneralHelper.ShowHelpInWishActivity("step_test.html", "", this);
        return;
    }

    public class CheckStateTask extends AsyncTask<Void,Void,Void> {

        private String mMode;

        public CheckStateTask(String mode) {
            mMode = mode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                getStateFromServer();
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
            UpdateStateView(places,price,isCampaign,cHeader, cText);
            super.onPostExecute(aVoid);
        }

        public boolean getStateFromServer() {
            try {

                String wishesIds = "";

                URL url = new URL(URL_ADDRESS_TO_CHECK + "?mode=" + mMode);
                //Log.e("URL", url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                String responseData = convertStreamToString(inputStream);
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    JSONObject responseJSON = new JSONObject(responseData);
                    try {
                        places = responseJSON.getInt("places");
                        price = responseJSON.getDouble("price");
                        isCampaign = responseJSON.getInt("is_campaign");
                        cHeader = responseJSON.getString("campaign_header");
                        cText = responseJSON.getString("campaign_text");
                        //skuCodeMain = responseJSON.getString("sku_code");

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                UpdateStateView(-1,price,isCampaign,cHeader, cText);
                e.printStackTrace();
            }
            return false;

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


    public class CallBackTask extends AsyncTask<Void,Void,Void> {

        private  int mMode;
        public CallBackTask(int mode) {
            mMode = mode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                callBackExecute();
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
            UpdateStateView(places,price,isCampaign,cHeader, cText);
            super.onPostExecute(aVoid);
        }

        public boolean callBackExecute() {
            try {
                URL url = new URL(URL_ADDRESS_TO_CALLBACK + "?mode=" + mMode);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.d("Callback", "Callback OK");
                } else {
                    Log.e("Callback","CallBack ERROR" + conn.getResponseMessage());
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }


}




