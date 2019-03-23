package ru.apps4yourlife.life.lifebalance.Activities;

import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import org.w3c.dom.Text;

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

public class StepEditActivity extends AppCompatActivity {

    private LifeBalanceDBDataManager mDataManager;
    private Cursor mCurrentStep;
    private String mStepPositionInList;
    private long mStepId;
    private String mWishId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_edit);

        int backButtonImage = R.drawable.ic_clear_white_24dp;
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(backButtonImage);
        actionBar.setElevation(0.0f);

        String stepId = getIntent().getStringExtra("STEP_ID");
        mStepPositionInList = getIntent().getStringExtra("POSITION_ID");
        mWishId = getIntent().getStringExtra("WISH_ID");
        mDataManager = new LifeBalanceDBDataManager(this);
        mStepId = 0;
        if (stepId != null) {
            mStepId = Long.valueOf(stepId);
            if (mStepId > 0) {
                mCurrentStep = mDataManager.GetStepById(mStepId);
                EditText mStepDescription = (EditText) findViewById(R.id.stepDescriptionEditText);
                String stepDescription = mCurrentStep.getString(mCurrentStep.getColumnIndex(LifeBalanceContract.StepsEntry.COLUMN_DESCRIPTION));
                mStepDescription.setText(stepDescription);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save_item, menu);
        return true;
    }

    public boolean isStepCorrect() {
        return true;
    }

    public boolean stepSave_click() {
        if (isStepCorrect()) {
            EditText stepDescriptionEditText = (EditText) findViewById(R.id.stepDescriptionEditText);
            String stepDescription = stepDescriptionEditText.getText().toString();
            mDataManager.InsertOrUpdateStep(mStepId, Integer.valueOf(mWishId), stepDescription);
            return true;
        } else {
            return false;
        }
    }

    public void FinishActivity(int result) {
        setResult(result);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save:
                if (stepSave_click()) {
                    FinishActivity(Integer.valueOf(mStepPositionInList));
                }
                return true;
            case android.R.id.home:
                FinishActivity(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}