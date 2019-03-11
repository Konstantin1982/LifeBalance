package ru.apps4yourlife.life.lifebalance.Activities;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;
import ru.apps4yourlife.life.lifebalance.Utilities.ChooseCategoriesFragment;
import ru.apps4yourlife.life.lifebalance.Utilities.GeneralHelper;

public class WishEditActivity extends AppCompatActivity implements ChooseCategoriesFragment.ChooseCategoriesFragmentListener, GeneralHelper.SubscribeDialogInterface {

    private LifeBalanceDBDataManager mDataManager;
    private long mWishEntryId;
    private int mWishStatus;
    private int mNewWishStatus;
    private String mWishPositionInList;
    private String mWishSituation;
    private Cursor mWishEntry;
    private Date mChosenDate;
    private long mChosenDateLong;

    private ArrayList<Integer> mSelectedTypes;
    private boolean isHelpShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String wishIdString = getIntent().getStringExtra("WISH_ID");
        mWishPositionInList = getIntent().getStringExtra("POSITION_ID");
        mDataManager = new LifeBalanceDBDataManager(this);

        // get all data from Database
        initWish(wishIdString);

        // init layout
        int layout_type;
        switch (mWishStatus) {
            case GeneralHelper.WishStatusesClass.WISH_STATUS_NEW:
                layout_type = R.layout.activity_wish_edit_status_new;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_REJECTED:
                layout_type = R.layout.activity_wish_edit_status_new;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_IN_REVIEW:
                layout_type = R.layout.activity_wish_edit_status_new;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION:
                layout_type = R.layout.activity_wish_edit_status_situation;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_FEARS:
                layout_type = R.layout.activity_wish_edit_status_fears;
                break;
            default:
                layout_type = R.layout.activity_wish_edit_status_new;
        }
        setContentView(layout_type);
        initLayout();
        updateUIWish(mWishEntry);
    }

    public void initLayout() {
        switch (mWishStatus) {
            case GeneralHelper.WishStatusesClass.WISH_STATUS_NEW:
                EditText wishDescriptionEditText = (EditText) findViewById(R.id.wishDescriptionEditText);
                wishDescriptionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                wishDescriptionEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
                android.support.v7.app.ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
                actionBar.setElevation(0.0f);
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_REJECTED:
                wishDescriptionEditText = (EditText) findViewById(R.id.wishDescriptionEditText);
                wishDescriptionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                wishDescriptionEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
                actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
                actionBar.setElevation(0.0f);
                StartHelpAnimation();
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_IN_REVIEW:
                wishDescriptionEditText = (EditText) findViewById(R.id.wishDescriptionEditText);
                wishDescriptionEditText.setFocusable(false);
                wishDescriptionEditText.setClickable(false);
                ImageButton buttonTypes = findViewById(R.id.imageView4);
                buttonTypes.setVisibility(View.INVISIBLE);
                actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
                actionBar.setElevation(0.0f);
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION:
                EditText situationDescriptionEditText = (EditText) findViewById(R.id.wishSituationEditText);
                situationDescriptionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                situationDescriptionEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
                actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
                actionBar.setElevation(0.0f);
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_FEARS:
                actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
                actionBar.setElevation(0.0f);
                break;
        }
    }

    public void StartHelpAnimation() {
        Button helpButton = (Button) findViewById(R.id.helpButton);
        ObjectAnimator animY = ObjectAnimator.ofFloat(helpButton, "translationY", 0f, -100f, 0f);
        animY.setDuration(600);//1sec
        animY.setInterpolator(new BounceInterpolator());
        animY.setRepeatCount(5);
        animY.start();
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
            mWishEntry = mDataManager.GetWishById(wishId);
            mSelectedTypes = GeneralHelper.extractTypesFromWish(mWishEntry.getString(mWishEntry.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_TYPE)));
            mWishStatus = mWishEntry.getInt(mWishEntry.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_STATUS));
            mChosenDateLong = mWishEntry.getLong(mWishEntry.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_PLAN_END));
        } else {
            // new wish
            mWishEntry = null;
            mSelectedTypes = new ArrayList<Integer>();
            mWishStatus = GeneralHelper.WishStatusesClass.WISH_STATUS_NEW;
            mChosenDateLong = 0;
        }
        mNewWishStatus = mWishStatus;
        mWishSituation = "";
    }


    public void wishAddCategoryClick(View view) {
        ChooseCategoriesFragment mApplicationDialogFragment = new ChooseCategoriesFragment();
        mApplicationDialogFragment.setmListener(this);
        mApplicationDialogFragment.show(getSupportFragmentManager(), "ChoosePlaceDialogFragment");
    }

    public void updateUIWish(Cursor wishEntry) {
        String description = "";
        String situation = "";

        mChosenDate = new Date(mChosenDateLong);
        if (wishEntry != null) {
            description = wishEntry.getString(wishEntry.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_DESCRIPTION));
            situation = wishEntry.getString(wishEntry.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_SITUATION));
        }
        switch (mWishStatus) {
            case GeneralHelper.WishStatusesClass.WISH_STATUS_NEW:
                EditText wishDescriptionEditText = (EditText) findViewById(R.id.wishDescriptionEditText);
                wishDescriptionEditText.setText(description);
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_REJECTED:
                wishDescriptionEditText = (EditText) findViewById(R.id.wishDescriptionEditText);
                wishDescriptionEditText.setText(description);
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_IN_REVIEW:
                wishDescriptionEditText = (EditText) findViewById(R.id.wishDescriptionEditText);
                wishDescriptionEditText.setText(description);
                CheckBox review = findViewById(R.id.ready_to_check);
                review.setVisibility(View.INVISIBLE);
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_FEARS:
                TextView wishDescriptionTextView = (TextView) findViewById(R.id.wishDescriptionTextView);
                wishDescriptionTextView.setText(description);
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION:
                EditText wishSituationEditText = (EditText) findViewById(R.id.wishSituationEditText);
                wishSituationEditText.setText(situation);
                wishDescriptionTextView = (TextView) findViewById(R.id.wishDescriptionTextView);
                wishDescriptionTextView.setText(description);
                if (mChosenDateLong > 0) {
                    Button mBirthDateButton = (Button) findViewById(R.id.planDateButton);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd");
                    mBirthDateButton.setText(dateFormat.format(mChosenDate.getTime()));
                }
                break;
        }


        UpdateUITypes();
    }

    public boolean isWishCorrect() {
        return true;
    }

    public void wishSave_routine() {
        String wishDescriptionString = "", wishSituationString = "";
        switch (mWishStatus) {
            case GeneralHelper.WishStatusesClass.WISH_STATUS_NEW:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_IN_REVIEW:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_REJECTED:
                EditText wishDescriptionEditText = (EditText) findViewById(R.id.wishDescriptionEditText);
                wishDescriptionString = wishDescriptionEditText.getText().toString();
            break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REJECTED:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REVIEW:
                EditText wishSituationEditText = (EditText) findViewById(R.id.wishSituationEditText);
                wishSituationString = wishSituationEditText.getText().toString();
                TextView wishDescriptionTextView = (TextView) findViewById(R.id.wishDescriptionTextView);
                wishDescriptionString = wishDescriptionTextView.getText().toString();
            break;
        }

        Toast.makeText(this, "Save clicked", Toast.LENGTH_SHORT).show();
        Log.e("wId", "WISH ID" + mWishEntryId);
        long res = mDataManager.InsertOrUpdateWish(
                String.valueOf(mWishEntryId),
                GeneralHelper.ConvertTypesToString(mSelectedTypes),
                0,
                mChosenDate.getTime(),
                0,
                mNewWishStatus,
                wishDescriptionString,
                wishSituationString);
        if (mWishEntryId == 0) mWishEntryId = res;

    }


    public void helpShowHide(View view) {
        String header = "", message = "", extraMessage = "";
        switch (mWishStatus) {
            case GeneralHelper.WishStatusesClass.WISH_STATUS_NEW:
                header = getString(R.string.next_step_0_header);
                message = getString(R.string.next_step_0_html);
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_REJECTED:
                header = getString(R.string.next_step_0_header);
                message = getString(R.string.next_step_0_html);
                extraMessage = mDataManager.GetReviewForWish(mWishEntryId, 0);
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_IN_REVIEW:
                header = getString(R.string.next_step_1_header);
                message = getString(R.string.next_step_1_html);
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION:
                header = getString(R.string.next_step_2_header);
                message = getString(R.string.next_step_2_html);
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REJECTED:
                header = getString(R.string.next_step_2_header);
                message = getString(R.string.next_step_2_html);
                extraMessage = mDataManager.GetReviewForWish(mWishEntryId, 1);
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REVIEW:
                header = getString(R.string.next_step_1_header);
                message = getString(R.string.next_step_1_html);
                break;
        }
        GeneralHelper.ShowHelpInWishActivity(header, message, extraMessage, this);
    }
/*
    public static class WishStatusesClass {
        ++ public static final int WISH_STATUS_NEW = 0;
        ++ public static final int WISH_STATUS_IN_REVIEW = 1;
        ++ public static final int WISH_STATUS_REJECTED = 2;
        ++public static final int WISH_STATUS_SITUATION = 3;
        public static final int WISH_STATUS_SITUATION_REVIEW = 4;
        public static final int WISH_STATUS_SITUATION_REJECTED = 5;
        public static final int WISH_STATUS_FEARS = 6;
        public static final int WISH_STATUS_STEPS = 7;
        public static final int WISH_STATUS_WAITING = 888;
        public static final int WISH_STATUS_COMPLETE = 999;
    }

*/

    public boolean wishSave_click() {
        boolean needToBeSent = false;
        boolean needToBeSave = true;
        boolean needToCloseActivity = false;
        // Save wish
        if (isWishCorrect()) {
            needToCloseActivity = true;
            if (mWishStatus == GeneralHelper.WishStatusesClass.WISH_STATUS_NEW ||
                    mWishStatus == GeneralHelper.WishStatusesClass.WISH_STATUS_REJECTED) {
                CheckBox submitCheckBox = findViewById(R.id.ready_to_check);
                if (submitCheckBox.isChecked()) {
                    if (GeneralHelper.isUserSubscribed()) {
                        mNewWishStatus = GeneralHelper.WishStatusesClass.WISH_STATUS_IN_REVIEW;
                        needToBeSent = true;
                    } else {
                        wishSave_routine();
                        needToBeSave = false;
                        GeneralHelper.ShowRecommendToSubscribe(this, this).show();
                        needToCloseActivity = false;
                    }
                }
            }
            if (mWishStatus == GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION     ||
                    mWishStatus == GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REJECTED) {
                CheckBox submitCheckBox = findViewById(R.id.ready_to_check);
                if (submitCheckBox.isChecked()) {
                    if (GeneralHelper.isUserSubscribed()) {
                        mNewWishStatus = GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REVIEW;
                        needToBeSent = true;
                    } else {
                        wishSave_routine();
                        needToBeSave = false;
                        GeneralHelper.ShowRecommendToSubscribe(this, this).show();
                        needToCloseActivity = false;
                    }
                }
            }
            if (needToBeSave) wishSave_routine();
            if (needToBeSent) GeneralHelper.PushWishToServer(this, mWishEntryId);
        }
        return needToCloseActivity;
    }


    @Override
    public void OnAgreedToSubscribe(Context context) {
        GeneralHelper.StartSubscriptionProcess(this);
    }

    @Override
    public void OnRejectToSubscribe(Context context) {
        switch (mWishStatus) {
            case GeneralHelper.WishStatusesClass.WISH_STATUS_NEW:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_IN_REVIEW:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_REJECTED:
                mNewWishStatus = GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REJECTED:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REVIEW:
                mNewWishStatus = GeneralHelper.WishStatusesClass.WISH_STATUS_FEARS;
                break;
        }
        wishSave_routine();
        FinishActivity(Integer.valueOf(mWishPositionInList));
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
        if (type1 != null) {
            type1.setImageBitmap(null);
            type2.setImageBitmap(null);
            type3.setImageBitmap(null);
            int count = 1;
            for (Integer type : mSelectedTypes) {
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
    }

    @Override
    public ArrayList<Integer> getSelectedItems() {
        return mSelectedTypes;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        int menu_id;
        switch (mWishStatus) {
            case GeneralHelper.WishStatusesClass.WISH_STATUS_NEW:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_REJECTED:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REJECTED:
                menu_id = R.menu.menu_save_item;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_IN_REVIEW:
                menu_id = 0;
                break;
            default:
                menu_id = R.menu.menu_save_item;
                break;
        }
        if (menu_id > 0) inflater.inflate(menu_id, menu);
        return true;
    }


    public void FinishActivity(int result) {
        setResult(result);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save:
                if (wishSave_click()) {
                    FinishActivity(Integer.valueOf(mWishPositionInList));
                }
                return true;
            case android.R.id.home:
                FinishActivity(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void btnSetBirthDate_click (View view) {
        Calendar currentCalendar = new GregorianCalendar();
        new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        Button mBirthDateButton = (Button) findViewById(R.id.planDateButton);
                        Calendar calendar = new GregorianCalendar(year,monthOfYear,dayOfMonth);
                        mChosenDate = calendar.getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd");
                        mBirthDateButton.setText(dateFormat.format(mChosenDate.getTime()));
                    }
                },
                currentCalendar.get(currentCalendar.YEAR),
                currentCalendar.get(currentCalendar.MONTH),
                currentCalendar.get(currentCalendar.DAY_OF_MONTH)).show();
    }

}