package ru.apps4yourlife.life.lifebalance.Activities.Tabs;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import ru.apps4yourlife.life.lifebalance.R;

/**
 * Created by ksharafutdinov on 28-Nov-18.
 */

public class TabSituation extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_layout_wish_situation, container, false);
        return view;
    }

    public void updateUI(String situationText) {
        View view = this.getView();
        EditText situationTextView = (EditText) view.findViewById(R.id.wishSituationEditText);
        situationTextView.setText(situationText);
        return;
    }

}
