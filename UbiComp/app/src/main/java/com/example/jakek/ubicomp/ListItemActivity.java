package com.example.jakek.ubicomp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListItemActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String shopName = intent.getStringExtra("shop_name");
        String shopAddress = intent.getStringExtra("shop_address");
        String date = intent.getStringExtra("date");

        setTitle(shopName+" "+date);
        toolbar.setTitleTextColor(0xFFFFFFFF);

//        Toast.makeText(this, shopName+" "+shopAddress+" "+ date, Toast.LENGTH_LONG).show();

        DBHandler db = new DBHandler(this);

        List<ShopEntry> allItems = db.getItemsFromShop(shopName, shopAddress, date);

        ArrayList<String> itemStrings = new ArrayList<>();

        for (ShopEntry item: allItems)
            itemStrings.add(item.getItemName()+" " + item.getItemPrice());

        ListView lv = (ListView)findViewById(R.id.list_receipts_individual);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, itemStrings);
        lv.setAdapter(arrayAdapter);
        lv.setTextFilterEnabled(true);


    }

    public void imageZoom(View v) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        ImageButton background = (ImageButton) findViewById(R.id.receiptImage);
        background.setBackgroundColor(0xFFFFFFFF);
        layout.setBackground(background.getDrawable());

    }

}
