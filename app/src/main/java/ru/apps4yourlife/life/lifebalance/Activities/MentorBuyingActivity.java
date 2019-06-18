package ru.apps4yourlife.life.lifebalance.Activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import ru.apps4yourlife.life.lifebalance.R;

public class MentorBuyingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_buying);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setElevation(0.0f);

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

    public void onBuyingMentorClick(View view) {
        // Check if we're running on Android 5.0 or higher
        Intent mentorSubmitIntent = new Intent(this, MentorBuyingSubmitActivity.class);
        startActivityForResult(mentorSubmitIntent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 ) {
            if (resultCode == 0) {
                // отказался или нет мест.
                Toast.makeText(this,"Ментор НЕ подключен. Рекомендую попробовать позже.", Toast.LENGTH_SHORT ).show();
                return;
            }
        }
    }


}
