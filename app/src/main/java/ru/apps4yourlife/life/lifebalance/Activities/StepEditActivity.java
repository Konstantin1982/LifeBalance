package ru.apps4yourlife.life.lifebalance.Activities;

import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;

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
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
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