package ru.apps4yourlife.life.lifebalance.Adapters;

/**
 * Created by ksharafutdinov on 28-Nov-18.
 */

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.AdapterView;

import ru.apps4yourlife.life.lifebalance.Activities.Tabs.TabFears;
import ru.apps4yourlife.life.lifebalance.Activities.Tabs.TabSituation;
import ru.apps4yourlife.life.lifebalance.Activities.Tabs.TabStatus;
import ru.apps4yourlife.life.lifebalance.Activities.Tabs.TabSteps;

public class PagerAdapter extends FragmentPagerAdapter {

    private int mTabCount;

    public PagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        mTabCount = tabCount;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TabStatus ();
            case 1:
                return new TabSituation();
            case 2:
                return new TabFears();
            case 3:
                return new TabSteps();
        }
        return null;
    }



    @Override
    public int getCount() {
        return mTabCount;
    }

}

