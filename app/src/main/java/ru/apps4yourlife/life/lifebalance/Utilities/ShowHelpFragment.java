package ru.apps4yourlife.life.lifebalance.Utilities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;

import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;

/**
 * Created by ksharafutdinov on 12-Feb-19.
 */

public class ShowHelpFragment extends DialogFragment {

    private Context mContext;
    private View mDialogView;

    public interface ShowHelpFragmentListener {
    }

    private ShowHelpFragment.ShowHelpFragmentListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mContext = this.getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mDialogView = View.inflate(mContext, R.layout.help_view, null );
        builder.setView(mDialogView);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    public void updateCheckBoxes() {
    }


    public void onClickCheckBox(int position) {
    }

    public void setmListener(Context context) {
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ShowHelpFragment.ShowHelpFragmentListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ChooseCategoriesFragmentListener");
        }
    }

}
