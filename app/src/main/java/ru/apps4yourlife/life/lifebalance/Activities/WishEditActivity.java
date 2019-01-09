package ru.apps4yourlife.life.lifebalance.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ru.apps4yourlife.life.lifebalance.Activities.Tabs.TabSituation;
import ru.apps4yourlife.life.lifebalance.Adapters.PagerAdapter;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;

public class WishEditActivity extends AppCompatActivity {
    //This is our tablayout
    private TabLayout mTabLayout;
    //This is our viewPager
    private ViewPager mViewPager;
    // pager Adapter
    private PagerAdapter mpagerAdapter;
    private LifeBalanceDBDataManager mDataManager;
    private long mWishEntryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_edit);
        String wishIdString = getIntent().getStringExtra("WISH_ID");

        mDataManager = new LifeBalanceDBDataManager(this);
        initTabs();
        initWish(wishIdString);
    }

    public void initWish(String wishId) {
        if (wishId != null) {
            mWishEntryId = Long.valueOf(wishId);
        } else {
            mWishEntryId = 0;
        }
        Toast.makeText(this, "WISH ID = " + mWishEntryId, Toast.LENGTH_SHORT).show();
        if (mWishEntryId > 0) {
            // edit wish
            Cursor wishEntry = mDataManager.GetWishById(wishId);
            updateUIWish(wishEntry);
        } else {
            // new wish
            updateUIWish(null);
        }
    }

    public void updateUIWish(Cursor wishEntry) {
        EditText wishDescriptionEditText = (EditText) findViewById(R.id.wishDescriptionEditText);
        //EditText    wishSituationEditText = (EditText) mTabLayout.findViewById(R.id.wishSituationEditText);
        if (wishEntry == null) {
            wishDescriptionEditText.setText("");
            //wishSituationEditText.setText("");
        } else {
            wishDescriptionEditText.setText(wishEntry.getString(wishEntry.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_DESCRIPTION)));
            //wishSituationEditText.setText(wishEntry.getString(wishEntry.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_SITUATION)));
        }
    }

    public void initTabs() {
        mTabLayout = (TabLayout) findViewById(R.id.wish_edit_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_wish_edit);
        mpagerAdapter = new PagerAdapter(getSupportFragmentManager(), 4);
        mViewPager.setAdapter(mpagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_wish_status_accepted);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_wish_situation);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_wish_fears);
        mTabLayout.getTabAt(3).setIcon(R.drawable.ic_escalator);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(getApplicationContext(), "text" + tab.getPosition(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void wishSave_click(View view) {
        // Save wish
        EditText wishDescriptionEditText = (EditText) findViewById(R.id.wishDescriptionEditText);
        EditText wishSituationEditText = (EditText) findViewById(R.id.wishSituationEditText);
        String wishDescriptionString = wishDescriptionEditText.getText().toString();
        String wishSituationString = wishSituationEditText.getText().toString();
        Toast.makeText(this, "Save clicked", Toast.LENGTH_SHORT).show();
        Log.e("wId", "WISH ID" + mWishEntryId);
        mDataManager.InsertOrUpdateWish(String.valueOf(mWishEntryId), "", 0, 0, 0, 0, "", wishDescriptionString, wishSituationString, 0);
        finish();
    }

}