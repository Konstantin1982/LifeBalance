package ru.apps4yourlife.life.lifebalance.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;

/**
 * Created by ksharafutdinov on 20-Mar-19.
 */

public class StepsListAdapter  extends RecyclerView.Adapter <StepsListAdapter.StepsListAdapterViewHolder> {
    private Context mContext;
    private Cursor mStepListCursor;
    private String mWishId;

    public interface StepsListAdapterClickHandler {
        void onStepClick(String stepId, String itemPositionInList);
        void onArrowClick(String stepId, int direction);
    }

    private final StepsListAdapter.StepsListAdapterClickHandler mWishListAdapterClickHandler;

    class StepsListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nextStepDescription;
        private TextView nextStepOrder;

        private ImageButton buttonUp;
        private ImageButton buttonDn;

        StepsListAdapterViewHolder(View view) {
            super(view);
            nextStepDescription = (TextView) view.findViewById(R.id.stepDescriptionEditText);
            nextStepOrder = (TextView) view.findViewById(R.id.step_order);
            buttonUp = (ImageButton) view.findViewById(R.id.button_up);
            buttonDn = (ImageButton) view.findViewById(R.id.button_dn);
            buttonUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onArrowClick(-1);
                }
            });
            buttonDn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onArrowClick(1);
                }
            });
            view.setOnClickListener(this);
        }

        public void onArrowClick(int direction) {
            int position = getAdapterPosition();
            mStepListCursor.moveToPosition(position);
            mWishListAdapterClickHandler.onArrowClick(
                    mStepListCursor.getString(mStepListCursor.getColumnIndex(LifeBalanceContract.StepsEntry._ID)),
                    direction
            );

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mStepListCursor.moveToPosition(position);
            mWishListAdapterClickHandler.onStepClick(
                    mStepListCursor.getString(mStepListCursor.getColumnIndex(LifeBalanceContract.StepsEntry._ID)),
                    String.valueOf(position)
            );
        }
    }

    public StepsListAdapter(Context context, StepsListAdapter.StepsListAdapterClickHandler clickHandler, long wishId) {
        mWishListAdapterClickHandler = clickHandler;
        mContext = context;
        mWishId = String.valueOf(wishId);
        LifeBalanceDBDataManager mDataManager = new LifeBalanceDBDataManager(mContext);
        mStepListCursor = mDataManager.GetStepsByWishId(mWishId);
        //Toast.makeText(mContext,"COUNT OF STEPS = " + mStepListCursor.getCount(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public StepsListAdapter.StepsListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.step_item_in_list, parent, false);
        return new StepsListAdapter.StepsListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StepsListAdapter.StepsListAdapterViewHolder holder, final int position) {
        mStepListCursor.moveToPosition(position);
        String stepDescription = mStepListCursor.getString(mStepListCursor.getColumnIndex(LifeBalanceContract.StepsEntry.COLUMN_DESCRIPTION));
        String stepOrder = mStepListCursor.getString(mStepListCursor.getColumnIndex(LifeBalanceContract.StepsEntry.COLUMN_ORDER));
        holder.nextStepDescription.setText(stepDescription);
        holder.nextStepOrder.setText(stepOrder);
        return;
    }

    @Override
    public int getItemCount() {
        return  mStepListCursor.getCount();
    }

    public void updateListValues(int position) {
        LifeBalanceDBDataManager mDataManager = new LifeBalanceDBDataManager(mContext);
        mStepListCursor = mDataManager.GetStepsByWishId(mWishId);
        if (position >= 0) {
            notifyItemChanged(position);
        } else {
            notifyDataSetChanged();
        }
    }




}
