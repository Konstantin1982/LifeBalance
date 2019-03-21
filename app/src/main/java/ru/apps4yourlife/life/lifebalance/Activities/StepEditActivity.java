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
    private long mWishEntryId;

    private ArrayList<Integer> mSelectedTypes;
    private boolean isHelpShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_edit);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setElevation(0.0f);

        /*
        String wishIdString = getIntent().getStringExtra("WISH_ID");
        mWishPositionInList = getIntent().getStringExtra("POSITION_ID");
        mDataManager = new LifeBalanceDBDataManager(this);

        // get all data from Database
        initWish(wishIdString);

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
        */
    }
}