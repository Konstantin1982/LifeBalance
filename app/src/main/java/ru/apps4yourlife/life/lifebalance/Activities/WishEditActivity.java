package ru.apps4yourlife.life.lifebalance.Activities;

import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.CompoundButton;
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

import ru.apps4yourlife.life.lifebalance.Adapters.StepsListAdapter;
import ru.apps4yourlife.life.lifebalance.Adapters.WishListAdapter;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;
import ru.apps4yourlife.life.lifebalance.Utilities.ChooseCategoriesFragment;
import ru.apps4yourlife.life.lifebalance.Utilities.GeneralHelper;

public class WishEditActivity extends AppCompatActivity implements ChooseCategoriesFragment.ChooseCategoriesFragmentListener, GeneralHelper.SubscribeDialogInterface, StepsListAdapter.StepsListAdapterClickHandler{

    private LifeBalanceDBDataManager mDataManager;
    private long mWishEntryId;
    private int mWishStatus;
    private int mNewWishStatus;
    private String mWishPositionInList;
    private String mWishSituation;
    private Cursor mWishEntry;
    private Date mChosenDate;
    private long mChosenDateLong;
    private int mWishFearStatus;
    private StepsListAdapter mStepsListAdapter;

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

        Toast.makeText(this,"STATUS = " + mWishStatus, Toast.LENGTH_SHORT).show();

        // init layout
        int layout_type;
        switch (mWishStatus) {
            case GeneralHelper.WishStatusesClass.WISH_STATUS_NEW:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_REJECTED:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_IN_REVIEW:
                layout_type = R.layout.activity_wish_edit_status_new;
            break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REVIEW:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REJECTED:
                layout_type = R.layout.activity_wish_edit_status_situation;
            break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_FEARS:
                layout_type = R.layout.activity_wish_edit_status_fears;
            break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_STEPS:
                layout_type = R.layout.activity_wish_status_steps;
            break;
            default:
                layout_type = R.layout.activity_wish_edit_status_new;
        }
        setContentView(layout_type);
        initLayout();
        updateUIWish(mWishEntry);
    }

    public void initLayout() {
        int backButtonImage = R.drawable.ic_clear_white_24dp;
        switch (mWishStatus) {
            case GeneralHelper.WishStatusesClass.WISH_STATUS_NEW:
                EditText wishDescriptionEditText = (EditText) findViewById(R.id.wishDescriptionEditText);
                wishDescriptionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                wishDescriptionEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
                backButtonImage = R.drawable.ic_clear_white_24dp;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_REJECTED:
                wishDescriptionEditText = (EditText) findViewById(R.id.wishDescriptionEditText);
                wishDescriptionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                wishDescriptionEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
                backButtonImage = R.drawable.ic_clear_white_24dp;
                StartHelpAnimation();
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_IN_REVIEW:
                wishDescriptionEditText = (EditText) findViewById(R.id.wishDescriptionEditText);
                wishDescriptionEditText.setFocusable(false);
                wishDescriptionEditText.setClickable(false);
                ImageButton buttonTypes = findViewById(R.id.imageView4);
                buttonTypes.setVisibility(View.INVISIBLE);
                backButtonImage = R.drawable.ic_arrow_back_white_24dp;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION:
                EditText situationDescriptionEditText = (EditText) findViewById(R.id.wishSituationEditText);
                situationDescriptionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                situationDescriptionEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
                backButtonImage = R.drawable.ic_clear_white_24dp;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REVIEW:
                situationDescriptionEditText = (EditText) findViewById(R.id.wishSituationEditText);
                situationDescriptionEditText.setFocusable(false);
                situationDescriptionEditText.setClickable(false);
                backButtonImage = R.drawable.ic_arrow_back_white_24dp;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REJECTED:
                situationDescriptionEditText = (EditText) findViewById(R.id.wishSituationEditText);
                situationDescriptionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                situationDescriptionEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
                backButtonImage = R.drawable.ic_clear_white_24dp;
                StartHelpAnimation();
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_FEARS:
                backButtonImage = R.drawable.ic_clear_white_24dp;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_STEPS:
                RecyclerView mStepsRecyclerView = (RecyclerView) findViewById(R.id.stepsRecyclerView);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                layoutManager.setMeasurementCacheEnabled(false);

                mStepsRecyclerView.setLayoutManager(layoutManager);
                mStepsRecyclerView.setHasFixedSize(true);
                mStepsListAdapter = new StepsListAdapter(this, this, (int)mWishEntryId);
                mStepsRecyclerView.setAdapter(mStepsListAdapter);
                backButtonImage = R.drawable.ic_arrow_back_white_24dp;
            break;
        }
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(backButtonImage);
        actionBar.setElevation(0.0f);

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
        mWishFearStatus = mDataManager.GetFearWishStatus(mWishEntryId);

        //Toast.makeText(this,"FEARS = " + mWishFearStatus, Toast.LENGTH_SHORT).show();
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
            case GeneralHelper.WishStatusesClass.WISH_STATUS_REJECTED:
                EditText wishDescriptionEditText = (EditText) findViewById(R.id.wishDescriptionEditText);
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
                SetFearsStatus();
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REJECTED:
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
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REVIEW:
                wishSituationEditText = (EditText) findViewById(R.id.wishSituationEditText);
                wishSituationEditText.setText(situation);
                wishDescriptionTextView = (TextView) findViewById(R.id.wishDescriptionTextView);
                wishDescriptionTextView.setText(description);
                review = findViewById(R.id.ready_to_check);
                review.setVisibility(View.INVISIBLE);
                if (mChosenDateLong > 0) {
                    Button mBirthDateButton = (Button) findViewById(R.id.planDateButton);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd");
                    mBirthDateButton.setText(dateFormat.format(mChosenDate.getTime()));
                    mBirthDateButton.setEnabled(false);
                }
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_STEPS:
                wishDescriptionTextView = (TextView) findViewById(R.id.wishDescriptionTextView);
                wishDescriptionTextView.setText(description);
                updateFinishButton();
                break;

        }
        UpdateUITypes();
    }
    public void updateFinishButton() {
        Cursor tmp = mDataManager.GetStepsByWishId(String.valueOf(mWishEntryId));
        Button finishButton = (Button) findViewById(R.id.buttonFinishWish);
        if (tmp.getCount() < 10) {
            finishButton.setEnabled(false);
            finishButton.setText("Исполнить желание (" + tmp.getCount() + "/10)");
        } else {
            finishButton.setEnabled(true);
            finishButton.setText("Исполнить желание !!!");
        }

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
        Log.e("wId", "WISH ID" + mWishEntryId + ";   NEW STATUS = " + mNewWishStatus);
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

    public void PutWishToNextStatus() {
        mDataManager.MoveWishToNextStatus(String.valueOf(mWishEntryId),mNewWishStatus);
    }


    public void helpShowHide(View view) {
        String header = "", fileName = "", extraMessage = "";
        switch (mWishStatus) {
            case GeneralHelper.WishStatusesClass.WISH_STATUS_NEW:
                fileName = "step_0.html";
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_REJECTED:
                fileName = "step_0.html";
                extraMessage = mDataManager.GetReviewForWish(mWishEntryId, 0);
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_IN_REVIEW:
                fileName = "step_1.html";
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION:
                fileName = "step_2.html";
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REJECTED:
                fileName = "step_2.html";
                extraMessage = mDataManager.GetReviewForWish(mWishEntryId, 1);
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REVIEW:
                fileName = "step_1.html";
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_FEARS:
                fileName = "step_3.html";
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_STEPS:
                fileName = "step_4.html";
                break;
        }
        GeneralHelper.ShowHelpInWishActivity(fileName, extraMessage, this);
    }
/*
    public static class WishStatusesClass {
        ++ public static final int WISH_STATUS_NEW = 0;
        ++ public static final int WISH_STATUS_IN_REVIEW = 1;
        ++ public static final int WISH_STATUS_REJECTED = 2;
        ++public static final int WISH_STATUS_SITUATION = 3;
        ++public static final int WISH_STATUS_SITUATION_REVIEW = 4;
        ++public static final int WISH_STATUS_SITUATION_REJECTED = 5;
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
            if (mWishStatus == GeneralHelper.WishStatusesClass.WISH_STATUS_FEARS) {
                needToBeSave = false;
                if (mWishFearStatus == 4) {
                    mNewWishStatus = GeneralHelper.WishStatusesClass.WISH_STATUS_STEPS;
                    PutWishToNextStatus();

                }
                mDataManager.InsertOrUpdateFearsStatus(mWishEntryId, mWishFearStatus);
                Toast.makeText(this,"FEARS = " + mWishFearStatus, Toast.LENGTH_SHORT).show();
                needToCloseActivity = true;
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

    public void fearsClickListener(int step, boolean isChecked) {
        CheckBox step_1 = (CheckBox) findViewById(R.id.step1checkBox);
        CheckBox step_2 = (CheckBox) findViewById(R.id.step2checkBox);
        CheckBox step_3 = (CheckBox) findViewById(R.id.step3checkBox);
        CheckBox step_4 = (CheckBox) findViewById(R.id.step4checkBox);
        if (step == 1) {
            if (isChecked) {
                step_2.setEnabled(true);
                mWishFearStatus = 1;
            } else {
                mWishFearStatus = 0;
                step_2.setEnabled(false);
                step_2.setChecked(false);
            }
        }
        if (step == 2) {
            if (isChecked) {
                mWishFearStatus = 2;
                step_3.setEnabled(true);
                step_1.setEnabled(false);
            } else {
                mWishFearStatus = 1;
                step_3.setEnabled(false);
                step_3.setChecked(false);
                step_1.setEnabled(true);
            }
        }
        if (step == 3) {
            if (isChecked) {
                mWishFearStatus = 3;
                step_4.setEnabled(true);
                step_2.setEnabled(false);
            } else {
                mWishFearStatus = 2;
                step_4.setEnabled(false);
                step_4.setChecked(false);
                step_2.setEnabled(true);
            }
        }
        if (step == 4) {
            if (isChecked) {
                step_3.setEnabled(false);
                mWishFearStatus = 4;
            } else {
                step_3.setEnabled(true);
                mWishFearStatus = 3;
            }
        }
    }

    public void SetFearsStatus() {

        CheckBox step_1 = (CheckBox) findViewById(R.id.step1checkBox);
        CheckBox step_2 = (CheckBox) findViewById(R.id.step2checkBox);
        CheckBox step_3 = (CheckBox) findViewById(R.id.step3checkBox);
        CheckBox step_4 = (CheckBox) findViewById(R.id.step4checkBox);

        step_1.setEnabled(false);
        step_2.setEnabled(false);
        step_3.setEnabled(false);
        step_4.setEnabled(false);
        // 0 1 2 3 4
        Toast.makeText(this, "FEARS!!! = " + mWishFearStatus, Toast.LENGTH_SHORT).show();
        if (mWishFearStatus >= 3) {
            step_1.setChecked(true);
            step_2.setChecked(true);
            step_3.setChecked(true);
            step_3.setEnabled(true);
            step_4.setEnabled(true);
        } else if (mWishFearStatus >= 2) {
            step_1.setChecked(true);
            step_2.setChecked(true);
            step_2.setEnabled(true);
            step_3.setEnabled(true);
        } else if (mWishFearStatus >= 1) {
            step_1.setChecked(true);
            step_1.setEnabled(true);
            step_2.setEnabled(true);
        } else {
            step_1.setEnabled(true);
        }

        step_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                fearsClickListener(1, b);
            }
        });
        step_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                fearsClickListener(2, b);
            }
        });
        step_3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                fearsClickListener(3, b);
            }
        });
        step_4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                fearsClickListener(4, b);
            }
        });
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
            case GeneralHelper.WishStatusesClass.WISH_STATUS_FEARS:
                menu_id = R.menu.menu_save_item;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_IN_REVIEW:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REVIEW:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_STEPS:
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

    @Override
    public void onStepClick(String stepId, String itemPositionInList) {
        Intent stepEditIntent = new Intent(this, StepEditActivity.class);
        stepEditIntent.putExtra("STEP_ID",  stepId);
        stepEditIntent.putExtra("POSITION_ID",  itemPositionInList);
        stepEditIntent.putExtra("WISH_ID",  String.valueOf(mWishEntryId));
        startActivityForResult(stepEditIntent,0);
    }

    @Override
    public void onArrowClick(String stepId, int direction) {
        Toast.makeText(this, "ID = " + stepId + "; direction =   " + direction, Toast.LENGTH_SHORT ).show();
        mDataManager.ReorderStep(Long.valueOf(stepId), direction);
        mStepsListAdapter.updateListValues(-1);
    }

    public void stepAdd_click(View view) {
        Intent stepEditIntent = new Intent(this, StepEditActivity.class);
        stepEditIntent.putExtra("WISH_ID",  String.valueOf(mWishEntryId));
        stepEditIntent.putExtra("STEP_ID",  "-1");
        stepEditIntent.putExtra("POSITION_ID",  "-1");
        startActivityForResult(stepEditIntent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 ) {
            // from Step Edit
            int position = resultCode;
            mStepsListAdapter.updateListValues(position);
            updateFinishButton();
        }
    }


    public void buttonFinishClick(View view) {
        mNewWishStatus = GeneralHelper.WishStatusesClass.WISH_STATUS_COMPLETE;
        mDataManager.MoveWishToNextStatus(String.valueOf(mWishEntryId),mNewWishStatus);
        Toast.makeText(this,"Ваше желание отправлено во Вселенную на исполнение. Хорошего дня.", Toast.LENGTH_LONG).show();
        FinishActivity(-1);
    }
}