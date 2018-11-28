package ru.apps4yourlife.life.lifebalance.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.apps4yourlife.life.lifebalance.Adapters.PagerAdapter;
import ru.apps4yourlife.life.lifebalance.R;

public class WishEditActivity extends AppCompatActivity {
    //This is our tablayout
    private TabLayout mTabLayout;
    //This is our viewPager
    private ViewPager mViewPager;

    // pager Adapter
    private PagerAdapter mpagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_edit);

        mTabLayout = (TabLayout) findViewById(R.id.wish_edit_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_wish_edit);
        mpagerAdapter = new PagerAdapter(getSupportFragmentManager(),4);
        mViewPager.setAdapter(mpagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }
}
