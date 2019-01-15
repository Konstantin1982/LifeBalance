package ru.apps4yourlife.life.lifebalance.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;

/**
 * Created by ksharafutdinov on 14-Nov-18.
 */

public class WishListAdapter extends RecyclerView.Adapter <WishListAdapter.WishListAdapterViewHolder> {
    private Context mContext;
    private Cursor mWishListCursor;

    public interface WishListAdapterClickHandler {
        void onWishClick(String wishId, String itemPositionInList);
    }

    private final WishListAdapterClickHandler mWishListAdapterClickHandler;

    class WishListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView wishDescriptionTextView;
        private ConstraintLayout statusLayout;

        WishListAdapterViewHolder(View view) {
            super(view);
            wishDescriptionTextView = (TextView) view.findViewById(R.id.wishDescription);
            //statusLayout = (ConstraintLayout) view.findViewById(R.id.wish_status_card);
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
            //mChildrenListAdapterClickHandler.onChildClick(
                    //mListChildrenCursor.getString(mListChildrenCursor.getColumnIndex(WardrobeContract.ChildEntry._ID)),
                    //String.valueOf(position)
            //);
        }
    }

    public WishListAdapter(Context context, WishListAdapterClickHandler clickHandler) {
        mWishListAdapterClickHandler = clickHandler;
        mContext = context;
        LifeBalanceDBDataManager mDataManager = new LifeBalanceDBDataManager(mContext);
        mWishListCursor = mDataManager.GetOpenedWishes();
    }

    @Override
    public WishListAdapter.WishListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.wish_item_in_list, parent, false);
        return new WishListAdapter.WishListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WishListAdapter.WishListAdapterViewHolder holder, final int position) {
        mWishListCursor.moveToPosition(position);
        holder.wishDescriptionTextView.setText(mWishListCursor.getString(mWishListCursor.getColumnIndex(LifeBalanceContract.WishesEntry.COLUMN_DESCRIPTION)));


        return;
    }

    @Override
    public int getItemCount() {
        return  mWishListCursor.getCount();
    }

    public void updateListValues(int position) {
        LifeBalanceDBDataManager mDataManager = new LifeBalanceDBDataManager(mContext);
        mWishListCursor = mDataManager.GetOpenedWishes();
        //Log.e("CURSOR","Count of new cursor = " + mListChildrenCursor.getCount() + "; Position = " + position);
        if (position >= 0) {
            notifyItemChanged(position);
        } else {
            notifyItemInserted(mWishListCursor.getCount());
        }
    }

}

