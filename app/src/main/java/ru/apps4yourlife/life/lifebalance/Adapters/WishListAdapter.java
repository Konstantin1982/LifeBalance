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


    class WishListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView wishDescriptionTextView;
        private ConstraintLayout statusLayout;

        WishListAdapterViewHolder(View view) {
            super(view);
            wishDescriptionTextView = (TextView) view.findViewById(R.id.wishDescription);
            statusLayout = (ConstraintLayout) view.findViewById(R.id.wish_status_card);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mWishListCursor.moveToPosition(position);
            Log.d("ADAPTER", "CALL ACTIVITY with position = " + position);
            //mChildrenListAdapterClickHandler.onChildClick(
                    //mListChildrenCursor.getString(mListChildrenCursor.getColumnIndex(WardrobeContract.ChildEntry._ID)),
                    //String.valueOf(position)
            //);
        }
    }

    public WishListAdapter(Context context) {
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
/*
        holder.statusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("CLICK LISTENER", "Clicked STatus on Position " +  position);
                Toast.makeText(view.getContext(),"CLICKED ON", Toast.LENGTH_SHORT).show();
                ConstraintLayout statusLayout = (ConstraintLayout) holder.itemView.findViewById(R.id.wishStatusActionLayout);
                Animation slideUp = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_up);
                statusLayout.setVisibility(View.VISIBLE);
                statusLayout.startAnimation(slideUp);
            }
        });
*/
        /*
        holder.messageDate.setText("08.11 12:00");
        holder.messageFrom.setText(mWishListCursor.getString(mWishListCursor.getColumnIndex(LifeBalanceContract.MessagesEntry.COLUMN_FROM)));
        holder.messageSubject.setText(mWishListCursor.getString(mWishListCursor.getColumnIndex(LifeBalanceContract.MessagesEntry.COLUMN_SUBJECT)));
        holder.messageBody.setText(mWishListCursor.getString(mWishListCursor.getColumnIndex(LifeBalanceContract.MessagesEntry.COLUMN_BODY)));
        */
        /*
    public void onWishStatusImageClick(View view) {


    }
        */
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

