package ru.apps4yourlife.life.lifebalance.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;

import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;

/**
 * Created by ksharafutdinov on 18-May-18.
 */

public class ChooseCategoriesFragment extends DialogFragment  {

    private ArrayList<Integer> mSelectedItems;
    private Context mContext;
    private View mDialogView;
    private LifeBalanceDBDataManager mDataManager;

    public interface ChooseCategoriesFragmentListener {
        void OnClickWishesTypes(ArrayList<Integer> selectedItems);
        ArrayList<Integer> getSelectedItems();
    }

    private ChooseCategoriesFragmentListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mContext = this.getContext();
        mSelectedItems = mListener.getSelectedItems();  // Where we track the selected items
        mDataManager = new LifeBalanceDBDataManager(mContext);
        Cursor mItems = mDataManager.GetWishesTypesWithChecked(mSelectedItems);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mDialogView = View.inflate(mContext, R.layout.wish_types_dialog, null );
        CheckBox checkBox = mDialogView.findViewById(R.id.checkBox_0);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCheckBox(0);
            }
        });
        checkBox = mDialogView.findViewById(R.id.checkBox_1);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCheckBox(1);
            }
        });
        checkBox = mDialogView.findViewById(R.id.checkBox_2);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCheckBox(2);
            }
        });
        checkBox = mDialogView.findViewById(R.id.checkBox_3);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCheckBox(3);
            }
        });
        checkBox = mDialogView.findViewById(R.id.checkBox_4);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCheckBox(4);
            }
        });
        checkBox = mDialogView.findViewById(R.id.checkBox_5);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCheckBox(5);
            }
        });
        updateCheckBoxes();

        builder.setView(mDialogView);
        builder.setTitle(R.string.title_choose_categories)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(mContext, mSelectedItems.toString(), Toast.LENGTH_SHORT).show();
                        mListener.OnClickWishesTypes(mSelectedItems);
                    }
                });
        return builder.create();
    }

    public void updateCheckBoxes() {
        int id;
        CheckBox checkBox = mDialogView.findViewById(R.id.checkBox_0);
        checkBox.setChecked(mSelectedItems.contains(0));
        checkBox = mDialogView.findViewById(R.id.checkBox_1);
        checkBox.setChecked(mSelectedItems.contains(1));
        checkBox = mDialogView.findViewById(R.id.checkBox_2);
        checkBox.setChecked(mSelectedItems.contains(2));
        checkBox = mDialogView.findViewById(R.id.checkBox_3);
        checkBox.setChecked(mSelectedItems.contains(3));
        checkBox = mDialogView.findViewById(R.id.checkBox_4);
        checkBox.setChecked(mSelectedItems.contains(4));
        checkBox = mDialogView.findViewById(R.id.checkBox_5);
        checkBox.setChecked(mSelectedItems.contains(5));
    }


    public void onClickCheckBox(int position) {
        CheckBox checkBox = null, checkBoxMain = null;
        int position1 = -1;
        if (position == 0) {
            checkBoxMain = mDialogView.findViewById(R.id.checkBox_0);
            checkBox = mDialogView.findViewById(R.id.checkBox_1);
            position1 = 1;
        }
        if (position == 1) {
            checkBoxMain = mDialogView.findViewById(R.id.checkBox_1);
            checkBox = mDialogView.findViewById(R.id.checkBox_0);
            position1 = 0;
        }
        if (position == 2) {
            checkBoxMain = mDialogView.findViewById(R.id.checkBox_2);
            checkBox = mDialogView.findViewById(R.id.checkBox_3);
            position1 = 3;
        }
        if (position == 3) {
            checkBoxMain = mDialogView.findViewById(R.id.checkBox_3);
            checkBox = mDialogView.findViewById(R.id.checkBox_2);
            position1 = 2;
        }
        if (position == 4) {
            checkBoxMain = mDialogView.findViewById(R.id.checkBox_4);
            checkBox = mDialogView.findViewById(R.id.checkBox_5);
            position1 = 5;
        }
        if (position == 5) {
            checkBoxMain = mDialogView.findViewById(R.id.checkBox_5);
            checkBox = mDialogView.findViewById(R.id.checkBox_4);
            position1 = 4;
        }

        if (checkBoxMain.isChecked()) {
            mSelectedItems.add(position);
        } else  if (mSelectedItems.contains(Integer.valueOf(position))) {
            mSelectedItems.remove((Object)position);
        }
        if (mSelectedItems.contains(Integer.valueOf(position1))) {
            mSelectedItems.remove((Object)position1);
        }
        checkBox.setChecked(false);
    }

    public void setmListener(Context context) {
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ChooseCategoriesFragmentListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ChooseCategoriesFragmentListener");
        }
    }

}
