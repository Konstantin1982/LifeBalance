package ru.apps4yourlife.life.lifebalance.Adapters;

import android.content.Context;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;

/**
 * Created by ksharafutdinov on 14-Nov-18.
 */

public class MessagesListAdapter extends RecyclerView.Adapter <MessagesListAdapter.MessagesListAdapterViewHolder> {
    private Context mContext;
    private Cursor mListMessagesCursor;
    private int mMode;
    MessagesListAdapterClickHandler mClickHandler;

    public interface MessagesListAdapterClickHandler {
        void onMessageClick(String itemId, String itemPositionInList);
    }

    class MessagesListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView messageFrom;
        private TextView messageDate;
        private TextView messageSubject;
        private TextView messageBody;

        MessagesListAdapterViewHolder(View view) {
            super(view);
            messageFrom = (TextView) view.findViewById(R.id.messageFrom);
            messageDate = (TextView) view.findViewById(R.id.messageDate);
            messageSubject = (TextView) view.findViewById(R.id.messageSubject);
            messageBody = (TextView) view.findViewById(R.id.messageBody);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mListMessagesCursor.moveToPosition(position);
            mClickHandler.onMessageClick(
                    mListMessagesCursor.getString(mListMessagesCursor.getColumnIndex(LifeBalanceContract.MessagesEntry._ID)),
                    String.valueOf(position)
            );
        }
    }


    public MessagesListAdapter(Context context, MessagesListAdapterClickHandler clickHandler, int mode) {
        mClickHandler = clickHandler;
        mMode = mode;
        mContext = context;
        LifeBalanceDBDataManager mDataManager = new LifeBalanceDBDataManager(mContext);
        if (mMode == 0)
            mListMessagesCursor = mDataManager.GetMessages(5);
        else
            mListMessagesCursor = mDataManager.GetMessages(999);
    }

    @Override
    public MessagesListAdapter.MessagesListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mMode == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_item_in_list, parent, false);
            return new MessagesListAdapter.MessagesListAdapterViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_item_in_list, parent, false);
            return new MessagesListAdapter.MessagesListAdapterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(MessagesListAdapter.MessagesListAdapterViewHolder holder, int position) {
        mListMessagesCursor.moveToPosition(position);
        holder.messageDate.setText("08.11 12:00");
        holder.messageFrom.setText(mListMessagesCursor.getString(mListMessagesCursor.getColumnIndex(LifeBalanceContract.MessagesEntry.COLUMN_FROM)));
        holder.messageSubject.setText(mListMessagesCursor.getString(mListMessagesCursor.getColumnIndex(LifeBalanceContract.MessagesEntry.COLUMN_SUBJECT)));
        holder.messageBody.setText(mListMessagesCursor.getString(mListMessagesCursor.getColumnIndex(LifeBalanceContract.MessagesEntry.COLUMN_BODY)));
        return;
    }

    @Override
    public int getItemCount() {
        return  mListMessagesCursor.getCount();
    }
}

