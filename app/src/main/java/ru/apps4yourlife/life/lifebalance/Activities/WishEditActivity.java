package ru.apps4yourlife.life.lifebalance.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;
import ru.apps4yourlife.life.lifebalance.Utilities.ChooseCategoriesFragment;
import ru.apps4yourlife.life.lifebalance.Utilities.GeneralHelper;
import ru.apps4yourlife.life.lifebalance.Utilities.ShowHelpFragment;

public class WishEditActivity extends AppCompatActivity implements ChooseCategoriesFragment.ChooseCategoriesFragmentListener {

    private LifeBalanceDBDataManager mDataManager;
    private long mWishEntryId;
    private String mWishPositionInList;
    private Cursor mWishEntry;
    private ArrayList<Integer> mSelectedTypes;
    private boolean isHelpShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_edit_status_new);
        EditText wishDescriptionEditText = (EditText) findViewById(R.id.wishDescriptionEditText);
        wishDescriptionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        wishDescriptionEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        String wishIdString = getIntent().getStringExtra("WISH_ID");

        mWishPositionInList = getIntent().getStringExtra("POSITION_ID");

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
        String description = "";
        int status = GeneralHelper.WishStatusesClass.WISH_STATUS_NEW;
        if (wishEntry != null) {
            description = wishEntry.getString(wishEntry.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_DESCRIPTION));
            status = wishEntry.getInt(wishEntry.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_STATUS));
        }
        wishDescriptionEditText.setText(description);
        updateUIWishStatus(status);
        UpdateUITypes();
    }

    public boolean isWishCorrect() {
        return true;
    }

    public void wishSave_routine() {
        EditText wishDescriptionEditText = (EditText) findViewById(R.id.wishDescriptionEditText);
        String wishDescriptionString = wishDescriptionEditText.getText().toString();

        Toast.makeText(this, "Save clicked", Toast.LENGTH_SHORT).show();
        Log.e("wId", "WISH ID" + mWishEntryId);


        mDataManager.InsertOrUpdateWish(
                String.valueOf(mWishEntryId),
                GeneralHelper.ConvertTypesToString(mSelectedTypes),
                0,
                0,
                0,
                0,
                wishDescriptionString,
                "");
    }


    public void helpShowHide(View view) {
        Toast.makeText(this, "POPUP", Toast.LENGTH_SHORT).show();

        ShowHelpFragment mApplicationDialogFragment = new ShowHelpFragment();
        mApplicationDialogFragment.show(getFragmentManager(),"showHelp");
    }

    public void wishSave_click() {
        // Save wish
        if (isWishCorrect()) {
            wishSave_routine();
            setResult(Integer.valueOf(mWishPositionInList));
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
        UpdateUITypes();
    }

    public void UpdateUITypes() {
        ImageView type1 = findViewById(R.id.image_type_1);
        ImageView type2 = findViewById(R.id.image_type_2);
        ImageView type3 = findViewById(R.id.image_type_3);
        type1.setImageBitmap(null);
        type2.setImageBitmap(null);
        type3.setImageBitmap(null);
        int count = 1;
        for (Integer type :mSelectedTypes) {
            int imageId = GeneralHelper.GetImageResourceByType(type);
            if (imageId > 0) {
                if (count == 1) {
                    type1.setImageResource(imageId);
                }
                if (count == 2) {
                    type2.setImageResource(imageId);
                }
                if (count == 3) {
                    type3.setImageResource(imageId);
                }
                count++;
            }
        }
    }

    @Override
    public ArrayList<Integer> getSelectedItems() {
        return mSelectedTypes;
    }

    public void updateUIWishStatus(int status) {
        //TODO: NEXT STEP DESCRIPTION
        //TextView nextStepTextView = findViewById(R.id.theNextStepDescription);
        //TextView nextStepHeaderTextView = findViewById(R.id.theNextStepDescriptionHeader);


        String theNextStepHeaderMessage = "";
        String theNextStepMessage = "";
        switch (status) {
            case GeneralHelper.WishStatusesClass.WISH_STATUS_NEW:
                theNextStepMessage = getString(R.string.next_step_0);
                theNextStepHeaderMessage = getString(R.string.next_step_0_header);
                break;
        }
        Spanned sp = Html.fromHtml(theNextStepMessage);
        //nextStepTextView.setText(sp);
        sp = Html.fromHtml(theNextStepHeaderMessage);
        //nextStepHeaderTextView.setText(sp);
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