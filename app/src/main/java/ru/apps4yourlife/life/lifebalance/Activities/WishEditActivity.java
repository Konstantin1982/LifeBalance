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

import java.util.ArrayList;

import ru.apps4yourlife.life.lifebalance.Activities.Tabs.TabSituation;
import ru.apps4yourlife.life.lifebalance.Adapters.PagerAdapter;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;
import ru.apps4yourlife.life.lifebalance.Utilities.ChooseCategoriesFragment;

public class WishEditActivity extends AppCompatActivity implements ChooseCategoriesFragment.ChooseCategoriesFragmentListener {
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

    public void wishAddCategoryClick(View view){
        ChooseCategoriesFragment mApplicationDialogFragment = new ChooseCategoriesFragment();
        mApplicationDialogFragment.setmListener(this);
        mApplicationDialogFragment.show(getSupportFragmentManager(), "ChoosePlaceDialogFragment");

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

    @Override
    public void OnClickWishesTypes(ArrayList<Integer> selectedItems) {

    }
}