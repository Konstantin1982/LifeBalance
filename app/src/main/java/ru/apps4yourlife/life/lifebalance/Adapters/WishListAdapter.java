package ru.apps4yourlife.life.lifebalance.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.AbstractMap;
import java.util.ArrayList;

import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;
import ru.apps4yourlife.life.lifebalance.Utilities.GeneralHelper;

/**
 * Created by ksharafutdinov on 14-Nov-18.
 */

public class WishListAdapter extends RecyclerView.Adapter <WishListAdapter.WishListAdapterViewHolder> {
    private Context mContext;
    private Cursor mWishListCursor;
    private int mode;

    public interface WishListAdapterClickHandler {
        void onWishClick(String wishId, String itemPositionInList);
    }

    private final WishListAdapterClickHandler mWishListAdapterClickHandler;

    class WishListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView wishDescriptionTextView;
        private ImageView wishType1;
        private ImageView wishType2;
        private ImageView wishType3;
        private ConstraintLayout statusLayout;
        private TextView nextStepNumber;
        private TextView nextStepDescription;

        WishListAdapterViewHolder(View view) {
            super(view);
            wishDescriptionTextView = (TextView) view.findViewById(R.id.wishDescription);
            wishType1 = (ImageView) view.findViewById(R.id.list_wish_type_1);
            wishType2 = (ImageView) view.findViewById(R.id.list_wish_type_2);
            wishType3 = (ImageView) view.findViewById(R.id.list_wish_type_3);

            nextStepNumber = (TextView) view.findViewById(R.id.nextStepNumber);
            nextStepDescription = (TextView) view.findViewById(R.id.nextStepDescription);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mWishListCursor.moveToPosition(position);

            Toast.makeText(mContext, "CLICKED sds", Toast.LENGTH_SHORT).show();

            Log.d("ADAPTER", "CALL ACTIVITY with position = " + position);
            mWishListAdapterClickHandler.onWishClick(
                    mWishListCursor.getString(mWishListCursor.getColumnIndex(LifeBalanceContract.WishesEntry._ID)),
                     String.valueOf(position)
            );
        }
    }

    public WishListAdapter(Context context, WishListAdapterClickHandler clickHandler, int listMode) {
        mWishListAdapterClickHandler = clickHandler;
        mContext = context;
        LifeBalanceDBDataManager mDataManager = new LifeBalanceDBDataManager(mContext);
        mode = listMode;
        mWishListCursor = mDataManager.GetWishesList(mode);
    }

    @Override
    public WishListAdapter.WishListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.wish_item_in_list, parent, false);
        return new WishListAdapter.WishListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WishListAdapter.WishListAdapterViewHolder holder, final int position) {
        mWishListCursor.moveToPosition(position);
        ArrayList<Integer> types = GeneralHelper.extractTypesFromWish(mWishListCursor.getString(mWishListCursor.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_TYPE)));
        // public static Map<Integer, String> GetNextStepDescriptionForList(Integer current_status) {
        AbstractMap.SimpleEntry<String, String> nextStep =   GeneralHelper.GetNextStepDescriptionForList(mWishListCursor.getInt(mWishListCursor.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_STATUS)));

        holder.wishDescriptionTextView.setText(mWishListCursor.getString(mWishListCursor.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_DESCRIPTION)));
        holder.nextStepNumber.setText(nextStep.getKey());
        holder.nextStepDescription.setText(nextStep.getValue());
        holder.wishType1.setImageBitmap(null);
        holder.wishType2.setImageBitmap(null);
        holder.wishType3.setImageBitmap(null);
        holder.wishType1.setVisibility(View.INVISIBLE);
        holder.wishType2.setVisibility(View.INVISIBLE);
        holder.wishType3.setVisibility(View.INVISIBLE);

/*
            <TextView
        android:id="@+id/nextStepNumber"
        style="@style/WishListStepNumberText"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_marginBottom="24dp"
        android:layout_marginEnd="8dp"
        android:layout_marginTop="24dp"
        android:background="@drawable/dark_circle"
        android:paddingVertical="2dp"
        android:text="2"
        android:textAlignment="center"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toStartOf="@+id/nextStepDescription"
        app:layout_constraintTop_toTopOf="@+id/horizontal_divider_image" />

            <TextView
        android:id="@+id/nextStepDescription"
*/

        int count = 1;
        for (Integer type : types) {
            int imageId = GeneralHelper.GetImageResourceByType(type);
            if (imageId > 0) {
                if (count == 1) {
                    holder.wishType1.setImageResource(imageId);
                    holder.wishType1.setVisibility(View.VISIBLE);
                }
                if (count == 2) {
                    holder.wishType2.setImageResource(imageId);
                    holder.wishType2.setVisibility(View.VISIBLE);
                }
                if (count == 3) {
                    holder.wishType3.setImageResource(imageId);
                    holder.wishType3.setVisibility(View.VISIBLE);
                }
                count++;
            }
        }
        return;
    }

    @Override
    public int getItemCount() {
        return  mWishListCursor.getCount();
    }

    public void updateListValues(int position) {
        LifeBalanceDBDataManager mDataManager = new LifeBalanceDBDataManager(mContext);
        mWishListCursor = mDataManager.GetWishesList(mode);
        Log.e("CURSOR","Count of new cursor = " + mWishListCursor.getCount() + "; Position = " + position);
        if (position >= 0) {
            notifyItemChanged(position);
        } else {
            //notifyItemInserted(mWishListCursor.getCount());
            notifyDataSetChanged();
        }
    }

    public void changeListMode(int newMode) {
        mode = newMode;
        updateListValues(-1);
    }
}

