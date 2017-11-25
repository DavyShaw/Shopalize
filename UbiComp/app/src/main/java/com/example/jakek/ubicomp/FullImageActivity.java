package com.example.jakek.ubicomp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class FullImageActivity extends Activity {
    String test;
    Uri myUri;
    URI myURI;
    DBHandler db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);

        // get intent data
//        Intent i = getIntent();

        // Selected image id
//        int position = i.getExtras().getInt("id");

        db = new DBHandler(this);
        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
        Intent i = getIntent();
        test = i.getStringExtra("id");
        ArrayList<Uri> abs = new ArrayList<>();
        myUri = Uri.parse(test);
        try {
            myURI = new URI(myUri.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        abs.add(myUri);
        ImageAdapter imageAdapter = new ImageAdapter(this, abs);

        Log.i("round 2", test);
        Log.i("Round 3", myUri.toString());
        imageView.setImageURI(abs.get(0));
    }

    public void killPic(View view) {
        finish();
    }

    public void deletePic(View view) {
        Toast.makeText(this, test, Toast.LENGTH_LONG).show();
        //need to remove from db and from storage;
        File file = new File(myURI);
        boolean deleted = file.delete();
        test = test.substring(7);
        db.removeEntry(test);
        killPic(view);

    }
}