package ru.apps4yourlife.life.lifebalance.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ru.apps4yourlife.life.lifebalance.Adapters.EventsListAdapter;
import ru.apps4yourlife.life.lifebalance.Adapters.MessagesListAdapter;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceContract;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;

public class MainActivity extends AppCompatActivity {

    public static final int MAX_COUNT_WISHES = 50;

    //Events
    RecyclerView mListEvents;
    EventsListAdapter mEventsAdapter;

    // messages
    RecyclerView mListMessages;
    MessagesListAdapter mMessagesListAdapter;

    // dataManaged
    LifeBalanceDBDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataManager =  new LifeBalanceDBDataManager(this);

        dataManager.deleteDatabase();

        dataManager =  new LifeBalanceDBDataManager(this);

        wishButtonInit();
        eventListInit();
        messagesListInit();
    }


    public void wishButtonInit() {
        // label
        long uncompleteWishes = dataManager.GetCountUncompleteWishes();
        TextView countDescription = (TextView) findViewById(R.id.count_wishes_label);
        countDescription.setText(String.valueOf(uncompleteWishes) + "/" + MAX_COUNT_WISHES);
        //  progress
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.wishes_progress_bar);
        progressBar.setProgress( (int) uncompleteWishes / MAX_COUNT_WISHES);
    }

    public void eventListInit(){
        mListEvents = (RecyclerView) findViewById(R.id.eventsList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setMeasurementCacheEnabled(false);

        mListEvents.setLayoutManager(layoutManager);
        mListEvents.setHasFixedSize(true);
        mEventsAdapter = new EventsListAdapter(this);
        mListEvents.setAdapter(mEventsAdapter);
    }

    public void messagesListInit() {
        mListMessages = (RecyclerView) findViewById(R.id.messagesList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setMeasurementCacheEnabled(false);
        mListMessages.setLayoutManager(layoutManager);
        mListMessages.setHasFixedSize(true);
        mMessagesListAdapter = new MessagesListAdapter(this );
        mListMessages.setAdapter(mMessagesListAdapter);

    }

    public void wishList_click(View view) {
        Intent wishList = new Intent(this, WishesActivity.class);
        startActivity(wishList);
    }
}
