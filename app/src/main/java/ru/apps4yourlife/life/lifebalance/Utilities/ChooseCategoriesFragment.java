package ru.apps4yourlife.life.lifebalance.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

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
        builder.setTitle(R.string.title_choose_categories)
                .setMultiChoiceItems(
                        mItems,
                        "CHECKED",
                        LifeBalanceContract.WishesTypesEntry.COLUMN_DESCRIPTION,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(mContext, mSelectedItems.toString(), Toast.LENGTH_SHORT).show();
                        mListener.OnClickWishesTypes(mSelectedItems);
                    }
                });
        return builder.create();
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
