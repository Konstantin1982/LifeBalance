package ru.apps4yourlife.life.lifebalance.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import ru.apps4yourlife.life.lifebalance.Adapters.EventsListAdapter;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBHelper;
import ru.apps4yourlife.life.lifebalance.R;

public class MainActivity extends AppCompatActivity {

    //Events
    RecyclerView mListEvents;
    EventsListAdapter mEventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventListInit();

    }


    public void wishButtonInit() {

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

    public void wishList_click(View view) {
        //TODO: remove
        Toast.makeText(this, "Wish List clicked", Toast.LENGTH_SHORT).show();
        LifeBalanceDBDataManager dataManager = new LifeBalanceDBDataManager(this);
        dataManager.deleteDatabase();
    }
}
