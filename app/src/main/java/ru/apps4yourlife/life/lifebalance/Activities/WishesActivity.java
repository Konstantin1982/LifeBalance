package ru.apps4yourlife.life.lifebalance.Activities;

import android.animation.AnimatorSet;
import android.app.ActivityOptions;
import android.app.Instrumentation;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ru.apps4yourlife.life.lifebalance.Adapters.EventsListAdapter;
import ru.apps4yourlife.life.lifebalance.Adapters.WishListAdapter;
import ru.apps4yourlife.life.lifebalance.R;

public class WishesActivity extends AppCompatActivity {


    private WishListAdapter mWishListAdapter;
    private RecyclerView mListWishes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Explode());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishes);
        wishListInit();
    }
    public void wishListInit(){
        mListWishes = (RecyclerView) findViewById(R.id.wish_list_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setMeasurementCacheEnabled(false);

        mListWishes.setLayoutManager(layoutManager);
        mListWishes.setHasFixedSize(true);
        mWishListAdapter = new WishListAdapter(this);
        mListWishes.setAdapter(mWishListAdapter);
    }

    public void wishEdit_click(View view) {
        // Check if we're running on Android 5.0 or higher
        Intent wishEditIntent = new Intent(this, WishEditActivity.class);
        wishEditIntent.putExtra("WISH_ID", "1");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Apply activity transition
            View wishDescriptionView = findViewById(R.id.wishDescription);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, wishDescriptionView, "description");
            startActivity(wishEditIntent, options.toBundle());
        } else {
            startActivityForResult(wishEditIntent,0);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 ) {
            // from Wish Edit
            int position = resultCode;
            mWishListAdapter.updateListValues(position);
        }
    }


}
