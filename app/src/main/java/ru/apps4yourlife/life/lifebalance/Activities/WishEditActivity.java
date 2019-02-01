package ru.apps4yourlife.life.lifebalance.Activities;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;
import ru.apps4yourlife.life.lifebalance.Utilities.ChooseCategoriesFragment;
import ru.apps4yourlife.life.lifebalance.Utilities.GeneralHelper;

public class WishEditActivity extends AppCompatActivity implements ChooseCategoriesFragment.ChooseCategoriesFragmentListener {

    private LifeBalanceDBDataManager mDataManager;
    private long mWishEntryId;
    private Cursor mWishEntry;
    private ArrayList<Integer> mSelectedTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_edit_status_new);
        String wishIdString = getIntent().getStringExtra("WISH_ID");

        mDataManager = new LifeBalanceDBDataManager(this);
        initWish(wishIdString);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
    }
    // TODO: 1) Implement styles checker
    // TODO: 2) Implment add / edit wish on step 0 (or after rejecting)
    // TODO: 3) Implement Step 1 - Fears....

    public void initWish(String wishId) {
        if (wishId != null) {
            mWishEntryId = Long.valueOf(wishId);
        } else {
            mWishEntryId = 0;
        }
        Toast.makeText(this, "WISH ID = " + mWishEntryId, Toast.LENGTH_SHORT).show();
        if (mWishEntryId > 0) {
            // edit wish
            mWishEntry = mDataManager.GetWishById(wishId);
            mSelectedTypes = GeneralHelper.extractTypesFromWish(
                    mWishEntry.getString(mWishEntry.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_TYPE)));
        } else {
            // new wish
            mWishEntry = null;
            mSelectedTypes = new ArrayList<Integer>();
        }
        updateUIWish(mWishEntry);
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
            updateUIWishStatus(GeneralHelper.WishStatusesClass.WISH_STATUS_NEW);
            //wishSituationEditText.setText("");
        } else {
            wishDescriptionEditText.setText(wishEntry.getString(wishEntry.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_DESCRIPTION)));
            //wishSituationEditText.setText(wishEntry.getString(wishEntry.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_SITUATION)));
        }
    }

    public boolean isWishCorrect() {
        return true;
    }

    public void wishSave_routine() {
        EditText wishDescriptionEditText = (EditText) findViewById(R.id.wishDescriptionEditText);
        String wishDescriptionString = wishDescriptionEditText.getText().toString();

        Toast.makeText(this, "Save clicked", Toast.LENGTH_SHORT).show();
        Log.e("wId", "WISH ID" + mWishEntryId);

        mDataManager.InsertOrUpdateWish(String.valueOf(mWishEntryId), "", 0, 0, 0, 0, "", wishDescriptionString, "");
        finish();
    }


    public void wishSave_click() {
        // Save wish
        if (isWishCorrect()) {
            wishSave_routine();
            finish();
        } else {

        }


    }

    public void  wishSendtoReview (View view) {
        // check to subscribe
        if (GeneralHelper.isUserSubscribed()) {

        } else {
            // рекомендация подписаться!
            GeneralHelper.ShowRecommendToSubscribe(this);
        }
    }

    @Override
    public void OnClickWishesTypes(ArrayList<Integer> selectedItems) {
        mSelectedTypes = selectedItems;
    }

    @Override
    public ArrayList<Integer> getSelectedItems() {
        return mSelectedTypes;
    }

    public void updateUIWishStatus(int status) {
        TextView nextStepTextView = findViewById(R.id.theNextStepDescription);
        String theNextStepMessage = "";
        TextView nextStepHeaderTextView = findViewById(R.id.theNextStepDescriptionHeader);
        String theNextStepHeaderMessage = "";
        switch (status) {
            case GeneralHelper.WishStatusesClass.WISH_STATUS_NEW:
                theNextStepMessage = getString(R.string.next_step_0);
                theNextStepHeaderMessage = getString(R.string.next_step_0_header);
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_UNDER_REVIEW:
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_ACCEPTED:
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_ACCEPTED_ITSELF:
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_REJECTED:
                break;
        }
        Spanned sp = Html.fromHtml(theNextStepMessage);
        nextStepTextView.setText(sp);
        sp = Html.fromHtml(theNextStepHeaderMessage);
        nextStepHeaderTextView.setText(sp);
    }

    /*
    <string-array name="wishes_types">
        <item>Любовь</item> love.svg
        <item>Работа</item> hiring.svg
        <item>Здоровье</item> hospital.svg
        <item>Дружба</item> balloons.svg
        <item>Хобби</item> open-box.svg
        <item>Деньги</item> debit-card.svg
    </string-array>


     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save_item, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save :
                wishSave_click();
                return true;
            case android.R.id.home:
                setResult(0);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}