package com.example.jakek.ubicomp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);





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
    @Override
    protected void onResume(){
        DBHandler db = new DBHandler(this);
        super.onResume();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);

        double a = db.spendAverage();
        double l = db.spendByMonth(month + "/" + year);
        Toast.makeText(this, a+"", Toast.LENGTH_LONG).show();
        //Test numbers
//        int a = 400;
//        int l = 400;

        if (a > l){
            getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        }
        if (a == l){
            getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        }
        if (a < l){
            getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        }
    }
}
