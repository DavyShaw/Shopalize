package com.example.jakek.ubicomp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.lightGreen));


        Button openGallery = (Button) findViewById(R.id.openGallery);
        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, Gallery.class);
                startActivity(i);

            }
        });

    }

    public void clickNew(View v) {
        switch (v.getId()){
            case R.id.ReceiptPicture:
                startActivity(new Intent(this, ReceiptHistory.class));
                break;
            case R.id.ShoppingListPicture:
                startActivity(new Intent(this, ShoppingList.class));
                break;
            case R.id.statsPicture:
                startActivity(new Intent(this, StatsActivity.class));

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ReceiptHistory:
                startActivity(new Intent(this, ReceiptHistory.class));
                return true;
            case R.id.ShoppingList:
                startActivity(new Intent(this, ShoppingList.class));
                return true;
            case R.id.StatsPage:
                startActivity(new Intent(this, StatsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
