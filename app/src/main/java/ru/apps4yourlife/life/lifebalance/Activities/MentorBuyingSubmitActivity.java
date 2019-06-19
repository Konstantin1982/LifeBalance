package ru.apps4yourlife.life.lifebalance.Activities;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBHelper;
import ru.apps4yourlife.life.lifebalance.R;

import static ru.apps4yourlife.life.lifebalance.Utilities.SyncTask.SYNC_TASK_ACTIVITY;

public class MentorBuyingSubmitActivity extends AppCompatActivity {

    private static final String URL_ADDRESS_TO_CHECK = "http://apps4yourlife.ru/lifebalance/state.php";
    private int places = -1;
    private double price = 8000;
    private int isCampaign = 0;
    private String cHeader = "";
    private String cText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_buying_submit);
    }


    public void OnCheckStateClick(View view) {
        //getStateFromServer();
        //LifeBalanceDBHelper dbHelper = new LifeBalanceDBHelper(this);
        //LifeBalanceDBDataManager.InsertOrUpdateSettings(dbHelper.getWritableDatabase(), SYNC_TASK_ACTIVITY, String.valueOf(""));
        CheckStateTask task = new CheckStateTask();
        task.execute();
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
            priceView.setText("Итого цена: " + String.valueOf(price) + " рублей.");

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

    public void getStateFromServer() {
        try {

            String wishesIds = "";

            URL url = new URL(URL_ADDRESS_TO_CHECK);
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
                    UpdateStateView(places,price,isCampaign,cHeader, cText);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            UpdateStateView(-1,price,isCampaign,cHeader, cText);
            e.printStackTrace();
        }
        return;

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

    public class CheckStateTask extends AsyncTask<Void,Void,Void> {


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

                URL url = new URL(URL_ADDRESS_TO_CHECK);
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

}


// TODO: TASK WITH GETTING INFO FROM SERVER, BUYING TASK
// TODO: PURCHASE FLOW.

