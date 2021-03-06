
package ru.apps4yourlife.life.lifebalance.Activities;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.freshchat.consumer.sdk.Freshchat;
import com.freshchat.consumer.sdk.FreshchatConfig;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

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
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ru.apps4yourlife.life.lifebalance.Adapters.WishListAdapter;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBHelper;
import ru.apps4yourlife.life.lifebalance.R;
import ru.apps4yourlife.life.lifebalance.Utilities.BillingHelper;
import ru.apps4yourlife.life.lifebalance.Utilities.GeneralHelper;
//import ru.apps4yourlife.life.lifebalance.Utilities.SyncTask;

public class WishesActivity extends AppCompatActivity implements WishListAdapter.WishListAdapterClickHandler, NavigationView.OnNavigationItemSelectedListener, PurchasesUpdatedListener, BillingHelper.LastPurchaseListener  {


    private BillingHelper mBillingHelper;
    private WishListAdapter mWishListAdapter;
    private RecyclerView mListWishes;
    private SyncTask task;
    private int taskState = 0;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Explode());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        wishListInit();

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        FreshchatConfig freshchatConfig=new FreshchatConfig("ffb3d5fd-cd52-4f1c-b466-911ccda50fb2","2ad2945a-6045-4dcc-8efb-cd786672316d");
        Freshchat.getInstance(getApplicationContext()).init(freshchatConfig);

        RunSyncTask();
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        LifeBalanceDBDataManager dbDataManager = new LifeBalanceDBDataManager(this);
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null){
            dbDataManager.InsertOrUpdateSettings(GeneralHelper.USER_STATE_SETTING_NAME, "1");
            RunSyncTask();
            //Toast.makeText(this,"Покупка прошла успешно!!! Ваши желания обязательно сбудутся", Toast.LENGTH_LONG).show();
        }
        if (responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED) {
            dbDataManager.InsertOrUpdateSettings(GeneralHelper.USER_STATE_SETTING_NAME, "2");
            //Toast.makeText(this,"Покупка прошла успешно!!! Ваши желания обязательно сбудутся", Toast.LENGTH_LONG).show();
            RunSyncTask();
        }
    }

    @Override
    public void setLastPurchase(String code) {

    }

    public void RunSyncTask() {
        if (GeneralHelper.isUserSubscribed(this)) {
            taskState = 0;
            task = new SyncTask(this);
            task.execute();
        }
        if (GeneralHelper.isUserSubscribeTestWish(this,"-1") > 0) {
            taskState = 0;
            task = new SyncTask(this);
            task.execute();
        }

    }

    public void wishListInit(){
        mListWishes = (RecyclerView) findViewById(R.id.wish_list_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setMeasurementCacheEnabled(false);

        mListWishes.setLayoutManager(layoutManager);
        mListWishes.setHasFixedSize(true);
        mWishListAdapter = new WishListAdapter(this, this, 0);
        mListWishes.setAdapter(mWishListAdapter);

        CheckBox isCompleteShow = (CheckBox) findViewById(R.id.isShowCompleted);
        isCompleteShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mWishListAdapter.changeListMode(1);
                } else {
                    mWishListAdapter.changeListMode(0);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 ) {
            // from Wish Edit
            int position = resultCode;
            mWishListAdapter.updateListValues(position);
            invalidateOptionsMenu();
        } else {
            mWishListAdapter.updateListValues(-1);
        }
        RunSyncTask();
    }

    public void wishAdd_click(View view) {
        onWishClick("-1", "-1");
    }

    @Override
    public void onWishClick(String wishId, String itemPositionInList) {
        // Check if we're running on Android 5.0 or higher
        Intent wishEditIntent = new Intent(this, WishEditActivity.class);
        wishEditIntent.putExtra("WISH_ID",  wishId);
        wishEditIntent.putExtra("POSITION_ID",  itemPositionInList);
        View wishDescriptionView = findViewById(R.id.wishDescription);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && wishDescriptionView != null) {
            // Apply activity transition
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, wishDescriptionView, "description");
            startActivityForResult(wishEditIntent,0, options.toBundle());
        } else {
            startActivityForResult(wishEditIntent,0);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (GeneralHelper.isUserSubscribed(this) || GeneralHelper.isUserSubscribeTestWish(this,"-1") > 0) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_refresh_list, menu);
        }
        return true;
    }


    public void FinishActivity(int result) {
        setResult(result);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh_list:
                RunSyncTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mentor) {
            Intent mentorSubmitIntent = new Intent(this, MentorBuyingActivity.class);
            startActivity(mentorSubmitIntent);
        }else if (id == R.id.nav_mentor_test) {
            Intent mentorSubmitIntent = new Intent(this, MentorBuyingSubmitActivity.class);
            mentorSubmitIntent.putExtra("MODE", "TEST");
            startActivity(mentorSubmitIntent);
        } else if (id == R.id.nav_delete_wishes) {
            Intent deleteWishesIntent = new Intent(this, DeleteWishesActivity.class);
            startActivityForResult(deleteWishesIntent,1);
        } else if (id == R.id.nav_support) {
            Freshchat.showConversations(getApplicationContext());
        } else if (id == R.id.nav_help2) {
            Intent faqIntent = new Intent(this, FAQActivity.class);
            startActivity(faqIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isActivityStarted(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    public void btnRateThisApp_click(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=ru.apps4yourlife.life.lifebalance"));
        if (!isActivityStarted(intent)) {
            intent.setData(Uri
                    .parse("https://play.google.com/store/apps/details?id=id=ru.apps4yourlife.life.lifebalance"));
            if (!isActivityStarted(intent)) {
                Toast.makeText(
                        this,
                        "Не удалось открыть Google Play.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void debugBtn_click(View view) {
        LifeBalanceDBDataManager manager = new LifeBalanceDBDataManager(this);
        Cursor tmp = manager.mDBHelper.getReadableDatabase().rawQuery("Select _ID from Wishes", null);
        if (tmp.getCount()>0) {
            for (int i = 0; i< tmp.getCount(); i++) {
                tmp.moveToPosition(i);
                //Log.e("DB WISH", "ID = " + tmp.getLong(0));
            }
        }
    }


    public class SyncTask extends AsyncTask<Void,Void,Void> {

        public static final String SYNC_TASK_ACTIVITY = "SYNC_TASK_ACTIVITY";
        private static final String KEY = "KEY";
        //private static final String URL_ADDRESS_TO_SEND = "http://localhost/request.php";
        private static final String URL_ADDRESS_TO_SEND = "https://apps4yourlife.ru/lifebalance/request.php";
        private static final String URL_ADDRESS_TO_RECEIVE = "https://apps4yourlife.ru/lifebalance/receivedata.php";

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
            Toast.makeText(mContext,"Refreshing...",Toast.LENGTH_SHORT).show();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //Log.e("TASK"," Started");
                //boolean isActive = true;
                //while (isActive) {
                    // 1 - RECEIVE DATA FROM SERVER // once in minute
                    getNewsFromServer();

                    // 2 - GET LIST for SEND
                    JSONObject data = PrepareDataToSend();
                    // 3 - SEND
                    if (!data.isNull("wishes"))  {
                        sendDataToServer(data);
                    } else {
                        taskState++;
                    }

                    //TimeUnit.SECONDS.sleep(60);
                    //if (!LifeBalanceDBDataManager.GetSettingValueByName(dbHelper.getReadableDatabase(), SYNC_TASK_ACTIVITY).equalsIgnoreCase(String.valueOf(currentActivity))) {
                    //    isActive = false;
                    //}
                //}
            } catch (Exception ex) {
                //Toast.makeText(mContext,"ERROR WHILE REFRESHING", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... voids){
            super.onProgressUpdate(voids);
            // UPDATE LIST OF WISHES, MESSAGES
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mWishListAdapter.updateListValues(-1);
            if (taskState == 2) {
                //Toast.makeText(mContext, "Обновление прошло успешно.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Ошибка соединения с сервером, проверьте интернет-соединение и попробуйте еще раз.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        public JSONObject PrepareDataToSend(){
            JSONObject data = new JSONObject();
            try {
                // UserID + KEY
                String userId = LifeBalanceDBDataManager.GetSettingValueByName(dbHelper.getReadableDatabase(),GeneralHelper.USER_ID_SETTING_NAME);
                String userName = LifeBalanceDBDataManager.GetSettingValueByName(dbHelper.getReadableDatabase(),GeneralHelper.USER_NAME_SETTING_NAME);
                data.put(GeneralHelper.USER_ID_SETTING_NAME, userId);
                data.put(GeneralHelper.USER_NAME_SETTING_NAME, userName);
                data.put(KEY,GenerateKey(userId));

                // wishes
                Cursor wishesToSend = LifeBalanceDBDataManager.GetWishesToServer(dbHelper.getReadableDatabase());
                int count = wishesToSend.getCount();
                //Log.e("JSON_PREPARE", " Count of Wishes to Server: " + String.valueOf(count));
                if (count > 0) {
                    JSONArray wishesArray = new JSONArray();
                    wishesToSend.moveToFirst();
                    for (int i = 0; i < wishesToSend.getCount(); i++) {
                        JSONObject wish = new JSONObject();
                        wishesToSend.moveToPosition(i);
                        wish.put("ID", wishesToSend.getString(wishesToSend.getColumnIndex(LifeBalanceContract.WishesEntry._ID)));
                        wish.put("START", wishesToSend.getString(wishesToSend.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_START)));

                        long date = wishesToSend.getLong(wishesToSend.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_PLAN_END));
                        Date mChosenDate = new Date(date);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd");
                        wish.put("END1", dateFormat.format(mChosenDate.getTime()));
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
            //Log.e("JSON", data.toString());

            return data;
        }


        public JSONObject PrepareDataToReceive(String commit){
            JSONObject data = new JSONObject();
            try {
                // UserID + KEY
                String userId = LifeBalanceDBDataManager.GetSettingValueByName(dbHelper.getReadableDatabase(),GeneralHelper.USER_ID_SETTING_NAME);
                data.put(GeneralHelper.USER_ID_SETTING_NAME, userId);
                data.put(KEY,GenerateKey(userId));
                data.put("COMMIT",commit);
            } catch (Exception ex){
                Toast.makeText(mContext,"ERROR WHILE REFRESHING", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
            return data;
        }


        public boolean getNewsFromServer() {
            try {

                String wishesIds = "";

                URL url = new URL(URL_ADDRESS_TO_RECEIVE);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                byte[] bData = PrepareDataToReceive("0").toString().getBytes(StandardCharsets.UTF_8);
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.write(bData);
                os.flush();
                os.close();
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                String responseData = convertStreamToString(inputStream);
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    JSONObject responseJSON = new JSONObject(responseData);
                    try {
                        // wishes
                        JSONArray updatedWishes = responseJSON.getJSONArray("wishes");
                        int sizeWishes = updatedWishes.length();
                        for (int i = 0; i < sizeWishes; i++) {
                            JSONObject updatedWish = updatedWishes.getJSONObject(i);
                            String idToUpdate = updatedWish.getString("WISHID");
                            String statusToUpdate = updatedWish.getString("NEWSTATUS");
                            String commentToUpdate = updatedWish.getString("COMMENT");
                            LifeBalanceDBDataManager.UpdateWishFromServer(dbHelper.getWritableDatabase(), idToUpdate, statusToUpdate, commentToUpdate);
                            wishesIds += idToUpdate + ", ";
                        }
                        if (sizeWishes > 0) {
                            publishProgress();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(mContext,"ERROR WHILE REFRESHING", Toast.LENGTH_SHORT).show();
                        ex.printStackTrace();
                    }
                }
                conn.disconnect();
                try {
                    //commit
                    if (!wishesIds.isEmpty()) {
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        //Log.e("COMMIT1", "WISHESIDS = " + wishesIds);
                        JSONObject fullResponse = PrepareDataToReceive("1");
                        fullResponse.put("WISHES", wishesIds + "-1");
                        //Log.e("COMMIT2", "DATA = " + fullResponse.toString());
                        bData = fullResponse.toString().getBytes(StandardCharsets.UTF_8);
                        os = new DataOutputStream(conn.getOutputStream());
                        os.write(bData);
                        os.flush();
                        os.close();
                        InputStream inputStream2 = new BufferedInputStream(conn.getInputStream());
                        conn.disconnect();
                    }
                    taskState++;
                }
                catch (Exception e) {
                    Toast.makeText(mContext,"ERROR WHILE REFRESHING", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            } catch (Exception e) {
                Toast.makeText(mContext,"ERROR WHILE REFRESHING", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return false;

        }

        public boolean sendDataToServer(JSONObject data) {
            try {
                URL url = new URL(URL_ADDRESS_TO_SEND);
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
                    //Log.e("JSON_RESPONSE", responseJSON.toString());
                    try {
                        String updatedWishes = responseJSON.getString("wishes");
                        //Log.e("JSON.COMMIT", updatedWishes);
                        if (!updatedWishes.isEmpty()) {
                            LifeBalanceDBDataManager.CommitWishesFromServer (dbHelper.getWritableDatabase(),updatedWishes);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
                conn.disconnect();
                taskState++;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext,"ERROR WHILE REFRESHING", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        private String GenerateKey(String userId) {
            String key =  "OK";
            if (!GeneralHelper.isUserSubscribed(mContext) && GeneralHelper.isUserSubscribeTestWish(mContext,"-1") == 0) {
                key =  "ERROR";
            }
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



}



