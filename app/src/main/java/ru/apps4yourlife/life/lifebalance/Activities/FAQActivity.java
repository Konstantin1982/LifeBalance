package ru.apps4yourlife.life.lifebalance.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import ru.apps4yourlife.life.lifebalance.R;
import ru.apps4yourlife.life.lifebalance.Utilities.GeneralHelper;

public class FAQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setElevation(0.0f);

    }

    public void clickOnGeneralFaq(View view) {
        GeneralHelper.ShowHelpInWishActivity("why.html", "", this);

    }

    public void clickOnMentorFaq(View view) {
        GeneralHelper.ShowHelpInWishActivity("why2.html", "", this);
    }

    public void clickOnApplicationFAQ(View view) {
        GeneralHelper.ShowHelpInWishActivity("why3.html", "", this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(0);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
