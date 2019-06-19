
package ru.apps4yourlife.life.lifebalance.Activities;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.concurrent.TimeUnit;

import ru.apps4yourlife.life.lifebalance.Adapters.StepsListAdapter;
import ru.apps4yourlife.life.lifebalance.Adapters.WishListAdapter;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBHelper;
import ru.apps4yourlife.life.lifebalance.R;
import ru.apps4yourlife.life.lifebalance.Utilities.GeneralHelper;
//import ru.apps4yourlife.life.lifebalance.Utilities.SyncTask;

public class WishesActivity extends AppCompatActivity implements WishListAdapter.WishListAdapterClickHandler, NavigationView.OnNavigationItemSelectedListener {


    private WishListAdapter mWishListAdapter;
    private RecyclerView mListWishes;
    private SyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Explode());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        task = new SyncTask(this);
        task.execute();
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
        }
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_refresh_list, menu);
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
                task = new SyncTask(this);
                task.execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        task.cancel(true);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mentor) {
            Intent mentorSubmitIntent = new Intent(this, MentorBuyingActivity.class);
            startActivity(mentorSubmitIntent);

        } else if (id == R.id.nav_delete_wishes) {
            Toast.makeText(this,"Delete WISHES", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_campaigns) {
            Toast.makeText(this,"CAMAIGNS", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_help) {
            Toast.makeText(this,"Main HELP", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_help2) {
            Toast.makeText(this,"GENERAL HELP", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class SyncTask extends AsyncTask<Void,Void,Void> {

        public static final String SYNC_TASK_ACTIVITY = "SYNC_TASK_ACTIVITY";
        private static final String USER_ID = "USER_ID";
        private static final String KEY = "KEY";
        //private static final String URL_ADDRESS_TO_SEND = "http://localhost/request.php";
        private static final String URL_ADDRESS_TO_SEND = "http://apps4yourlife.ru/lifebalance/request.php";
        private static final String URL_ADDRESS_TO_RECEIVE = "http://apps4yourlife.ru/lifebalance/receivedata.php";

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
                Log.e("TASK"," Started");
                //boolean isActive = true;
                //while (isActive) {
                    // 1 - RECEIVE DATA FROM SERVER // once in minute
                    getNewsFromServer();

                    // 2 - GET LIST for SEND
                    JSONObject data = PrepareDataToSend();
                    // 3 - SEND
                    sendDataToServer(data);

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
            Toast.makeText(mContext,"Refresh has been finished.", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(mContext,"ERROR WHILE REFRESHING", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext,"ERROR WHILE REFRESHING", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
            return data;
        }


        public JSONObject PrepareDataToReceive(String commit){
            JSONObject data = new JSONObject();
            try {
                // UserID + KEY
                String userId = LifeBalanceDBDataManager.GetSettingValueByName(dbHelper.getReadableDatabase(),USER_ID);
                data.put(USER_ID, userId);
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
                        Log.e("COMMIT1", "WISHESIDS = " + wishesIds);
                        JSONObject fullResponse = PrepareDataToReceive("1");
                        fullResponse.put("WISHES", wishesIds + "-1");
                        Log.e("COMMIT2", "DATA = " + fullResponse.toString());
                        bData = fullResponse.toString().getBytes(StandardCharsets.UTF_8);
                        os = new DataOutputStream(conn.getOutputStream());
                        os.write(bData);
                        os.flush();
                        os.close();
                        InputStream inputStream2 = new BufferedInputStream(conn.getInputStream());
                        conn.disconnect();
                    }
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
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext,"ERROR WHILE REFRESHING", Toast.LENGTH_SHORT).show();
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



}
