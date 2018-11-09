package ru.apps4yourlife.life.lifebalance.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import ru.apps4yourlife.life.lifebalance.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void wishList_click(View view) {
        Toast.makeText(this, "Wish List clicked", Toast.LENGTH_SHORT).show();
    }
}
