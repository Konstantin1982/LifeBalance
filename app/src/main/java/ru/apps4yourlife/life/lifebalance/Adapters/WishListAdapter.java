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

public class WishListAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private Context mContext;
    private Cursor mWishListCursor;
    private int mode;

    public interface WishListAdapterClickHandler {
        void onWishClick(String wishId, String itemPositionInList);
    }

    private final WishListAdapterClickHandler mWishListAdapterClickHandler;

    private void GeneralOnClick(View v, int position) {
        mWishListCursor.moveToPosition(position);
        mWishListAdapterClickHandler.onWishClick(
                mWishListCursor.getString(mWishListCursor.getColumnIndex(LifeBalanceContract.WishesEntry._ID)),
                String.valueOf(position)
        );
    }

    class WishListAdapterViewHolderStatusNew extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView wishDescriptionTextView;
        private ImageView wishType1;
        private ImageView wishType2;
        private ImageView wishType3;
        WishListAdapterViewHolderStatusNew(View view) {
            super(view);
            wishDescriptionTextView = (TextView) view.findViewById(R.id.wishDescription);
            wishType1 = (ImageView) view.findViewById(R.id.list_wish_type_1);
            wishType2 = (ImageView) view.findViewById(R.id.list_wish_type_2);
            wishType3 = (ImageView) view.findViewById(R.id.list_wish_type_3);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            GeneralOnClick(v, getAdapterPosition());
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(mContext).inflate(R.layout.wish_item_in_list_step0, parent, false);
                return new WishListAdapterViewHolderStatusNew(view);
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.wish_item_in_list_step0, parent, false);
                return new WishListAdapterViewHolderStatusNew(view);
        }
    }
    @Override
    public int getItemViewType(int position) {
        int layout_type = -1;
        mWishListCursor.moveToPosition(position);
        int currentStatus = mWishListCursor.getInt(mWishListCursor.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_STATUS));
        switch (currentStatus) {
            case GeneralHelper.WishStatusesClass.WISH_STATUS_NEW:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_REJECTED:
                layout_type = 0;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_IN_REVIEW:
                layout_type = 1;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION:
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REJECTED:
                layout_type = 2;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_SITUATION_REVIEW:
                layout_type = 3;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_FEARS:
                layout_type = 4;
                break;
            case GeneralHelper.WishStatusesClass.WISH_STATUS_STEPS:
                layout_type = 5;
                break;
            default:
                layout_type = 0;
        }
        return layout_type;
    }

    public void  SetWishTypeImages(ArrayList<Integer> types, ImageView wishType1, ImageView wishType2, ImageView wishType3) {
        wishType1.setImageBitmap(null);
        wishType2.setImageBitmap(null);
        wishType3.setImageBitmap(null);
        wishType1.setVisibility(View.INVISIBLE);
        wishType2.setVisibility(View.INVISIBLE);
        wishType3.setVisibility(View.INVISIBLE);
        int count = 1;
        for (Integer type : types) {
            int imageId = GeneralHelper.GetImageResourceByType(type);
            if (imageId > 0) {
                if (count == 1) {
                    wishType1.setImageResource(imageId);
                    wishType1.setVisibility(View.VISIBLE);
                }
                if (count == 2) {
                    wishType2.setImageResource(imageId);
                    wishType2.setVisibility(View.VISIBLE);
                }
                if (count == 3) {
                    wishType3.setImageResource(imageId);
                    wishType3.setVisibility(View.VISIBLE);
                }
                count++;
            }
        }

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        mWishListCursor.moveToPosition(position);
        ArrayList<Integer> types = GeneralHelper.extractTypesFromWish(mWishListCursor.getString(mWishListCursor.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_TYPE)));
        String wishDescription = mWishListCursor.getString(mWishListCursor.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_DESCRIPTION));
        // public static Map<Integer, String> GetNextStepDescriptionForList(Integer current_status) {
        //AbstractMap.SimpleEntry<String, String> nextStep =   GeneralHelper.GetNextStepDescriptionForList(mWishListCursor.getInt(mWishListCursor.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_STATUS)));
        ImageView wishType1,wishType2, wishType3;
        switch (holder.getItemViewType()) {
            case 0:
                WishListAdapterViewHolderStatusNew holder0 = (WishListAdapterViewHolderStatusNew) holder;
                holder0.wishDescriptionTextView.setText(wishDescription);
                wishType1 = holder0.wishType1;
                wishType2 = holder0.wishType2;
                wishType3 = holder0.wishType3;
                SetWishTypeImages(types, wishType1, wishType2, wishType3);
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
        notifyDataSetChanged();
    }

    public void changeListMode(int newMode) {
        mode = newMode;
        updateListValues(-1);
    }
}

