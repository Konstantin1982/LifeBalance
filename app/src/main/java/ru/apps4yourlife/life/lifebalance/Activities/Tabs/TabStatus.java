package ru.apps4yourlife.life.lifebalance.Activities.Tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.apps4yourlife.life.lifebalance.R;

/**
 * Created by ksharafutdinov on 28-Nov-18.
 */

public class TabStatus  extends Fragment{
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_layout_wish_status_action, container, false);
        return view;
    }
}
