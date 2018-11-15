package ru.apps4yourlife.life.lifebalance.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;

/**
 * Created by ksharafutdinov on 14-Nov-18.
 */

public class EventsListAdapter extends RecyclerView.Adapter <EventsListAdapter.EventsListAdapterViewHolder> {
    private Context mContext;
    private Cursor mListEventsCursor;


    class EventsListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView eventDate;
        private TextView eventDescription;

        EventsListAdapterViewHolder(View view) {
            super(view);
            eventDate = (TextView) view.findViewById(R.id.eventDate);
            eventDescription = (TextView) view.findViewById(R.id.eventDescription);
        }

        @Override
        public void onClick(View v) {
            //int position = getAdapterPosition();
            //mListChildrenCursor.moveToPosition(position);
            //Log.e("ADAPTER","CALL ACTIVITY with position = " + position);
            //mChildrenListAdapterClickHandler.onChildClick(
                    //mListChildrenCursor.getString(mListChildrenCursor.getColumnIndex(WardrobeContract.ChildEntry._ID)),
                    //String.valueOf(position)
            //);
        }
    }


    public EventsListAdapter(Context context) {
        mContext = context;
        LifeBalanceDBDataManager mDataManager = new LifeBalanceDBDataManager(mContext);
        mListEventsCursor = mDataManager.GetEvents(5);
    }


    @Override
    public EventsListAdapter.EventsListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.event_item, parent, false);
        return new EventsListAdapter.EventsListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventsListAdapter.EventsListAdapterViewHolder holder, int position) {
        mListEventsCursor.moveToPosition(position);
        holder.eventDate.setText("08.11");
        String eventDescription = mListEventsCursor.getString(mListEventsCursor.getColumnIndex(LifeBalanceContract.EventsEntry.COLUMN_DESCRIPTION));
        int sizeDescription = eventDescription.length();
        //int lines = 2;
        //if (sizeDescription > 60) lines++;
        //holder.eventDescription.lin


        holder.eventDescription.setText(eventDescription);
        return;
    }

    @Override
    public int getItemCount() {
        return mListEventsCursor.getCount();
    }
}

