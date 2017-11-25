package com.example.jakek.ubicomp;

import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SearchResults extends AppCompatActivity {
    static ImageAdapter adapter;
    GridView gridView;
    ArrayList<Uri> absPath;
    List<ShoppingReceiptData> pics;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_history);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        DBHandler db = new DBHandler(this);
        searchView=(SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Search View");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getApplicationContext(), SearchResults.class);

                intent.putExtra("query", query);

                startActivity(intent);
                finish();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(getBaseContext(), newText, Toast.LENGTH_LONG).show();

                return false;
            }
        });

        FloatingActionButton view = (FloatingActionButton) findViewById(R.id.fab);
        view.setVisibility(View.INVISIBLE);
    }

    protected void onResume(){
        super.onResume();
        gridView = (GridView) findViewById(R.id.gridview);
        pics = new ArrayList<>();
        DBHandler db = new DBHandler(this);
        try {
            Intent i = getIntent();
            String query = i.getStringExtra("query");

            pics = db.getSelectPics(query);
        } catch (ParseException e) {
            e.printStackTrace();

        }
        absPath = new ArrayList();

        for (int i=0; i<pics.size(); i++){
            String dir = pics.get(i).getAbsolutePath();
            File f = new File(dir);
            Uri imageUri = Uri.fromFile(f);

            absPath.add(imageUri);
        }
        adapter = new ImageAdapter(this, absPath);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // Sending image id to FullScreenActivity
                Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
                // passing array index
                i.putExtra("id", position);
                startActivity(i);
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ReceiptHistory.startCamera();
//            }
//        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        return true;
    }

    // ---------------------------------------------------------------------------------------------

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
