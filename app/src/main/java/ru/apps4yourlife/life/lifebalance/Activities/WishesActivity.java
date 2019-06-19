
package ru.apps4yourlife.life.lifebalance.Activities;

import android.app.ActivityOptions;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import ru.apps4yourlife.life.lifebalance.Adapters.StepsListAdapter;
import ru.apps4yourlife.life.lifebalance.Adapters.WishListAdapter;
import ru.apps4yourlife.life.lifebalance.R;
import ru.apps4yourlife.life.lifebalance.Utilities.GeneralHelper;
import ru.apps4yourlife.life.lifebalance.Utilities.SyncTask;

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


        /*
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishes);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setElevation(0.0f);
        */

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
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

}
