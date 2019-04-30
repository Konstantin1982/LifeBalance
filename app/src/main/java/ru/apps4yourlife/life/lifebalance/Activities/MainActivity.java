package ru.apps4yourlife.life.lifebalance.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.apps4yourlife.life.lifebalance.Adapters.EventsListAdapter;
import ru.apps4yourlife.life.lifebalance.Adapters.MessagesListAdapter;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    // 1 - формулирование желания
    // 2 - работает над страхами
    // 3 - Представляем ситуацию в реальности, в которой желание сбылось, проставляем дату (не менее 3-х месяцев)
    // 4 - Планируем 10 шагов. Первые три можем сделать сейчас.
    // 5 - Отправляем в сбыточный цех.


    //Любовь - Дружба
    //Работа - Хобби
    //Здоровье - Деньги
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

        //android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        //actionBar.setElevation(0.0f);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dataManager =  new LifeBalanceDBDataManager(this);

        dataManager.deleteDatabase();

        dataManager =  new LifeBalanceDBDataManager(this);

        wishButtonInit();
        eventListInit();
        messagesListInit();
    }


    public void wishButtonInit() {
        // label
        long uncompleteWishes = dataManager.GetCountOpenedWishes();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
