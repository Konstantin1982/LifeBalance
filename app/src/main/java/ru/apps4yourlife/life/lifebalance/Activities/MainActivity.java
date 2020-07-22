package ru.apps4yourlife.life.lifebalance.Activities;

import android.content.Intent;

import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.apps4yourlife.life.lifebalance.Adapters.MessagesListAdapter;
import ru.apps4yourlife.life.lifebalance.Data.LifeBalanceDBDataManager;
import ru.apps4yourlife.life.lifebalance.R;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MessagesListAdapter.MessagesListAdapterClickHandler {

    // 1 - формулирование желания
    // 2 - работает над страхами
    // 3 - Представляем ситуацию в реальности, в которой желание сбылось, проставляем дату (не менее 3-х месяцев)
    // 4 - Планируем 10 шагов. Первые три можем сделать сейчас.
    // 5 - Отправляем в сбыточный цех.


    //Любовь - Дружба
    //Работа - Хобби
    //Здоровье - Деньги
    public static final int MAX_COUNT_WISHES = 50;

    // messages
    RecyclerView mListMessages;
    MessagesListAdapter mMessagesListAdapter;

    // dataManaged
    LifeBalanceDBDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

    public void messagesListInit() {
        mListMessages = (RecyclerView) findViewById(R.id.messagesList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setMeasurementCacheEnabled(false);
        mListMessages.setLayoutManager(layoutManager);
        mListMessages.setHasFixedSize(true);
        mMessagesListAdapter = new MessagesListAdapter(this, this, 0 );
        mListMessages.setAdapter(mMessagesListAdapter);

    }

    public void wishList_click(View view) {
        Intent wishList = new Intent(this, WishesActivity.class);
        startActivity(wishList);
    }

    public void messagesList_click(View view) {
        onMessageClick("", "");
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mentor) {
            //Toast.makeText(this,"Mentor buying", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_delete_wishes) {
            //Toast.makeText(this,"Delete WISHES", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_help2) {
            //Toast.makeText(this,"GENERAL HELP", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMessageClick(String itemId, String itemPositionInList) {
        Intent messagesIntent = new Intent(this, MessagesActivity.class);
        startActivity(messagesIntent);
    }
}

/*

=========================================================
1.2.0 Features
=========================================================


// DONE 1.2. 1 Валидация желания в форме
// DONE 1.2. 2 Покупка с кодом test_wish...  100 рублей + промокоды бесплатные
// DONE 1.2. 2.1 Внутри желания - ТЕСТОВАЯ ПОКУПКА!!!!
// DONE 1.2. 3 Если есть test_wish, я могу отправить одно желание на проверку.
// DONE 1.2. 4 Дата должна быть обязательной при втором статусе, должна также отправляться на сервер


=========================================================
1.3.0 Features
=========================================================

// ATODO 1.2. 4 Редизайн справочных окошек.
// ATODO 1.2  5 Нотификации от webchat
// ATODO 2 QUEUE for WISHES ON SERVER




 */